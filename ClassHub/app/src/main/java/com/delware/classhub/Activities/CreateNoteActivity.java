package com.delware.classhub.Activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.delware.classhub.DatabaseModels.NoteModel;
import com.delware.classhub.R;
import com.delware.classhub.Singletons.SingletonSelectedClass;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Overview: This class allows users to create notes that are associated with a particular
 * class.
 * @author Matt Del Fante
 */
public class CreateNoteActivity extends AppCompatActivity
{
    //The corresponding objects tied to the UI of the activity
    EditText m_editText = null;
    Button m_cancelButton = null;
    Button m_saveButton = null;

    //The note that is being created
    NoteModel m_newNote = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //overwrite the action bar for this activity with a different one
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_action_bar_layout);

        setContentView(R.layout.activity_create_note);

        //Set the content of the action bar title to say Create A Note
        TextView actionBarTextView = (TextView) findViewById(R.id.defaultActionBarTitle);
        actionBarTextView.setText("Create A Note");

        m_newNote = new NoteModel();
        m_editText = (EditText) this.findViewById(R.id.createNoteField);
        m_cancelButton = (Button) this.findViewById(R.id.cancelNoteButton);
        m_saveButton = (Button) this.findViewById(R.id.saveNoteButton);

        m_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();

                //not all whitespace or empty, enable the save note button
                if (input.trim().length() > 0)
                    m_saveButton.setEnabled(true);
                else
                    m_saveButton.setEnabled(false);
            }
        });

        m_cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        m_saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeStamp = new SimpleDateFormat("MM-dd-yyyy_HHmmss").format(Calendar.getInstance().getTime());

                //The note name defaults to the current time and date
                m_newNote.setName(timeStamp);
                m_newNote.setNote(m_editText.getText().toString());
                m_newNote.setAssociatedClass(SingletonSelectedClass.getInstance().getSelectedClass());

                m_newNote.save();

                Toast.makeText(getApplicationContext(), "The note was successfully saved!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

}
