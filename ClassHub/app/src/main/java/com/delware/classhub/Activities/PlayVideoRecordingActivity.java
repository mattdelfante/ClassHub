package com.delware.classhub.Activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.VideoView;

import com.delware.classhub.OtherClasses.ClassHubMediaController;
import com.delware.classhub.R;

/**
 * Overview: This class allows the user to playback previously recorded video recordings.
 * @author Matt Del Fante
 */
public class PlayVideoRecordingActivity extends AppCompatActivity
{
    //The surface that the video will be played on
    private VideoView m_videoView = null;

    //The media controller that controls the video playback
    private ClassHubMediaController m_mc = null;

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

        //This will hold the path to the internal storage location where the video recording
        //will be located.
        String pathToVideoRecording = getIntent().getExtras().getString("videoRecordingPath");

        m_videoView = (VideoView) this.findViewById(R.id.videoViewPlayBack);
        m_mc = new ClassHubMediaController(this);

        m_videoView.setVideoPath(pathToVideoRecording);
        m_videoView.setMediaController(m_mc);
        m_mc.setAnchorView(m_videoView);

        //start playing the video on activity creation
        m_videoView.start();
    }
}
