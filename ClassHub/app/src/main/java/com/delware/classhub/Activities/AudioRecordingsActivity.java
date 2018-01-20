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
 * Overview: This class allows users to navigate
 * to other Activities so the user can create, view,
 * edit, or delete audio recordings, video
 * recording and/or notes from the Class they are associated with.
 * @author Matt Del Fante
 */
public class AudioRecordingsActivity extends AppCompatActivity {

    //This activity will essentially act as a caller of the RecordAudioActivity and
    //the ViewAudioRecordingsActivity activity
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //overwrite the action bar for this activity with a different one
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_action_bar_layout);

        setContentView(R.layout.activity_audio_recordings);

        //get the name of the class at make that the title of the page
        TextView actionBarTextView = (TextView) findViewById(R.id.defaultActionBarTitle);
        actionBarTextView.setText(SingletonSelectedClass.getInstance().getSelectedClass().getName());
    }

    /**
     * Navigates the user to the RecordAudioActivity so the user can create an audio recording.
     * @param v The calling View.
     */
    public void goToRecordAudioActivity(View v)
    {
        Intent intent = new Intent(AudioRecordingsActivity.this, RecordAudioActivity.class);
        startActivity(intent);
    }

    /**
     * Navigates the user to the ViewAudioRecordingsActivity so the user can
     * interact with existing audio recordings.
     * @param v The calling View.
     */
    public void goToViewAudioRecordingsActivity(View v)
    {
        Intent intent = new Intent(AudioRecordingsActivity.this, ViewAudioRecordingsActivity.class);
        startActivity(intent);
    }
}