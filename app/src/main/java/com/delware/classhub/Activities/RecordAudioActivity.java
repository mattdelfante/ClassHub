package com.delware.classhub.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.delware.classhub.R;

public class RecordAudioActivity extends AppCompatActivity {

    //This needs to implement the media recorder stuff
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_record_audio);
    }
}
