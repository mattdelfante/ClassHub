package com.delware.classhub.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.delware.classhub.R;

public class AudioRecordingsActivity extends AppCompatActivity {

    //This activity will essentially act as a caller of the RecordAudioActivity and
    //the ViewAudioRecordingsActivity activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //action bar stuff can go here

        setContentView(R.layout.activity_audio_recordings);
    }

    public void goToRecordAudioActivity(View v)
    {
        Intent intent = new Intent(AudioRecordingsActivity.this, RecordAudioActivity.class);
        startActivity(intent);
    }

    public void goToViewAudioRecordingsActivity(View v)
    {
        Intent intent = new Intent(AudioRecordingsActivity.this, ViewAudioRecordingsActivity.class);
        startActivity(intent);
    }
}
