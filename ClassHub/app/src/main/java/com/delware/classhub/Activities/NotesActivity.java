package com.delware.classhub.Activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.delware.classhub.R;
import com.delware.classhub.Singletons.SingletonSelectedClass;

/**
 * Overview: This class provides the choice for the user to either create a note
 * or view previously created notes.
 * @author Matt Del Fante
 */
public class NotesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //overwrite the action bar for this activity with a different one
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_action_bar_layout);

        setContentView(R.layout.activity_notes);

        //get the name of the class and make that the title of the page
        TextView actionBarTextView = (TextView) findViewById(R.id.defaultActionBarTitle);
        actionBarTextView.setText(SingletonSelectedClass.getInstance().getSelectedClass().getName());
    }

    /**
     * Navigates the user to the CreateNoteActivity so the user can create a note.
     * @param view The calling View.
     */
    public void goToCreateNoteActivity(View view) {
        Intent intent = new Intent(NotesActivity.this, CreateNoteActivity.class);
        startActivity(intent);
    }

    /**
     * Navigates the user to the ViewNotesActivity so the user can view previously made notes.
     * @param view The calling View.
     */
    public void goToViewNotesActivity(View view) {
        Intent intent = new Intent(NotesActivity.this, ViewNotesActivity.class);
        startActivity(intent);
    }
}
