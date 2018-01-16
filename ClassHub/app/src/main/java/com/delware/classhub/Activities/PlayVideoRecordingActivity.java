package com.delware.classhub.Activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.VideoView;

import com.delware.classhub.OtherClasses.MyMediaController;
import com.delware.classhub.R;

public class PlayVideoRecordingActivity extends AppCompatActivity
{
    private VideoView m_videoView = null;
    private MyMediaController m_mc = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //overwrite the action bar for this activity with a different one
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_action_bar_layout);

        setContentView(R.layout.activity_play_video_recording);

        //Set the content of the action bar title
        TextView actionBarTextView = (TextView) findViewById(R.id.defaultActionBarTitle);
        actionBarTextView.setText("Video Playback");

        String pathToVideoRecording = getIntent().getExtras().getString("videoRecordingPath");

        m_videoView = (VideoView) this.findViewById(R.id.videoViewPlayBack);
        m_mc = new MyMediaController(this);

        m_videoView.setVideoPath(pathToVideoRecording);
        m_videoView.setMediaController(m_mc);
        m_mc.setAnchorView(m_videoView);
        m_videoView.start();
    }
}
