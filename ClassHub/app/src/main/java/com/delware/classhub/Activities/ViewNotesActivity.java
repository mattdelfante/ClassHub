package com.delware.classhub.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.delware.classhub.DatabaseModels.NoteModel;
import com.delware.classhub.R;
import com.delware.classhub.Singletons.SingletonSelectedClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Overview: This class allows users to interact with existing notes
 * associated with a particular class
 * @author Matt Del Fante
 */
public class ViewNotesActivity extends AppCompatActivity {

    //list of notes for the particular class
    private List<NoteModel> m_notes = null;

    //Dialog for pressing a note
    private Dialog m_noteDialog = null;

    //Dialog for renaming a note
    private Dialog m_renameNoteDialog = null;

    ///The activity context
    private Context m_activityContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //overwrite the action bar for this activity with a different one
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_action_bar_layout);

        setContentView(R.layout.activity_view_notes);

        //Set the content of the action bar title
        TextView actionBarTextView = (TextView) findViewById(R.id.defaultActionBarTitle);
        actionBarTextView.setText("Notes");

        m_notes = SingletonSelectedClass.getInstance().getSelectedClass().getNotes();

        initializeListView();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (m_noteDialog != null)
            m_noteDialog.dismiss();

        if (m_renameNoteDialog != null)
            m_renameNoteDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (m_noteDialog != null)
            m_noteDialog.dismiss();

        if (m_renameNoteDialog != null)
            m_renameNoteDialog.dismiss();
    }

    /**
     * Initializes the list view with buttons for each of the notes
     * associated with the particular class.
     */
    private void initializeListView()
    {
        List<String> noteNames = new ArrayList<>();

        //adds all of the names of the audio recordings to the list view
        for (NoteModel m : m_notes)
            noteNames.add(m.getName());

        ArrayAdapter<String> notesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, noteNames);

        ListView notesListView = (ListView) findViewById(R.id.notesListView);
        notesListView.setAdapter(notesAdapter);

        //when a list view item is clicked, these functions are invoked
        notesListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        createDialogBoxForClickingNote(position);
                        m_noteDialog.show();
                    }
                }
        );
    }

    /**
     * Creates the dialog box that displays when a note in the ListView is pressed on.
     * @param index the index of the selected note in the ListView
     */
    private void createDialogBoxForClickingNote(int index)
    {
        final NoteModel selectedNote = m_notes.get(index);
        final Dialog dialog = new Dialog(m_activityContext);
        dialog.setContentView(R.layout.activity_view_notes_dialog);

        final Button viewEditNoteButton = (Button) dialog.findViewById(R.id.viewEditNoteButton);
        final Button renameNoteButton = (Button) dialog.findViewById(R.id.renameNoteButton);
        final Button deleteNoteButton = (Button) dialog.findViewById(R.id.deleteNoteButton);

        viewEditNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go to the ViewEditNotesActivity
                Intent i = new Intent(ViewNotesActivity.this, ViewEditNoteActivity.class);
                i.putExtra("noteId", selectedNote.getId().toString());
                startActivity(i);
            }
        });

        renameNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                //show the rename note dialog
                createRenameNoteDialog(selectedNote);
                m_renameNoteDialog.show();
            }
        });

        deleteNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                final String name = selectedNote.getName();

                //Show an alert dialog confirming if the user is sure he/she wants to delete the note
                new AlertDialog.Builder(m_activityContext)
                        .setTitle("Wait...")
                        .setMessage("Are you sure you want to delete the note: " + name)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //delete the note from the db
                                selectedNote.delete();

                                Toast.makeText(getApplicationContext(), "The note: " + name + " was deleted.", Toast.LENGTH_SHORT).show();

                                //Reset the activity while making the transition seamless
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                overridePendingTransition(0, 0);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
            }
        });

        m_noteDialog = dialog;
    }

    /**
     * Creates the dialog box that displays when the rename note option is selected
     * in the previous Dialog.
     * @param selectedNote the NoteModel that was selected from the ListView.
     */
    private void createRenameNoteDialog(final NoteModel selectedNote)
    {
        final Dialog dialog = new Dialog(m_activityContext);
        dialog.setContentView(R.layout.activity_view_notes_rename_note_dialog);

        //all the components in the dialog box
        final EditText input = (EditText) dialog.findViewById(R.id.renameNoteInput);
        final Button doneButton = (Button) dialog.findViewById(R.id.renameNoteDoneButton);
        final Button cancelButton = (Button) dialog.findViewById(R.id.renameNoteCancelButton);

        input.setText(selectedNote.getName());

        final List<NoteModel> notes = SingletonSelectedClass.getInstance().getSelectedClass().getNotes();

        //done button is only available to show when there is information inputted into
        //the edit text associated with the dialog box
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            //enabled done button if a class name was entered
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();

                //not all whitespace or empty
                if (input.trim().length() > 0)
                    doneButton.setEnabled(true);
                else
                    doneButton.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedName = input.getText().toString();

                for (NoteModel model : notes)
                {
                    //if there is an note already with this name
                    if (model.getName().equals(updatedName))
                    {
                        //display an error message
                        Toast.makeText(getApplicationContext(),
                                "This class already has a note with the name: " +
                                        updatedName + ".", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                //update the database to hold the updated name
                selectedNote.setName(updatedName);
                selectedNote.save();

                //Reset the activity while making the transition seamless
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input.setText("");
                dialog.dismiss();
            }
        });

        m_renameNoteDialog = dialog;
    }
}
