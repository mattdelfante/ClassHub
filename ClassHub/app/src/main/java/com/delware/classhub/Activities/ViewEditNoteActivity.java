package com.delware.classhub.Activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.delware.classhub.DatabaseModels.NoteModel;
import com.delware.classhub.R;

/**
 * Overview: This class allows users to view or edit existing notes
 * associated with a particular class
 * @author Matt Del Fante
 */
public class ViewEditNoteActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //overwrite the action bar for this activity with a different one
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_action_bar_layout);

        setContentView(R.layout.activity_view_edit_note);

        //Set the content of the action bar title
        TextView actionBarTextView = (TextView) findViewById(R.id.defaultActionBarTitle);
        actionBarTextView.setText("View/Edit A Note");

        String noteId = getIntent().getExtras().getString("noteId");
        final NoteModel selectedNote = NoteModel.getNote(Integer.parseInt(noteId));

        final EditText editText = (EditText) this.findViewById(R.id.editNoteField);
        Button saveButton = (Button) this.findViewById(R.id.saveEditsButton);
        Button discardButton = (Button) this.findViewById(R.id.discardEditsButton);

        //put the note's content into the edit text
        editText.setText(selectedNote.getNote());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String note = editText.getText().toString();
                selectedNote.setNote(note);
                selectedNote.save();
                Toast.makeText(getApplicationContext(), "The note was successfully saved!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
