package com.delware.classhub.Activities;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.delware.classhub.R;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Overview: This class allows users to playback an audio recording with the ability for the user
 * to seek through the audio, pause the audio and stop listening to the audio.
 * @author Matt Del Fante
 */
public class PlayAudioRecordingActivity extends AppCompatActivity
{
    //allows the user to seek through and audio recording
    private SeekBar m_seekBar = null;

    //Helps the seek bar UI to be updated.
    private Handler m_handler = null;

    //Helps the seek bar
    private Runnable m_runnable = null;

    //Allows the audio recording to be played
    private MediaPlayer m_mediaPlayer = null;

    //true if the playback is paused, false if not.
    private boolean m_isPaused = false;

    //The holder of the gif that plays when audio is being played
    private GifImageView m_gifView;

    //The gif that plays when audio is being played
    private GifDrawable m_gifDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //overwrite the action bar for this activity with a different one
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_action_bar_layout);

        setContentView(R.layout.activity_play_audio_recording);

        //Set the content of the action bar title
        TextView actionBarTextView = (TextView) findViewById(R.id.defaultActionBarTitle);
        actionBarTextView.setText("Audio Playback");

        //retrieve the path to the current audio recording
        String pathToAudioRecording = getIntent().getExtras().getString("audioRecordingPath");

        m_handler = new Handler();
        m_seekBar = (SeekBar) findViewById(R.id.audioRecordingSeekBar);
        m_mediaPlayer = new MediaPlayer();
        m_mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        //Handles starting the gif
        try {
            m_gifView = (GifImageView) findViewById(R.id.playbackAudioGif);
            m_gifDrawable = new GifDrawable(getResources(), R.drawable.speaker);
            m_gifDrawable.setSpeed(0.33f);
            m_gifView.setImageDrawable(m_gifDrawable);
        } catch (IOException e) {
            Log.i("LOG: ", "The audio recording gif was not able to be played.");
        }

        try {
            m_mediaPlayer.setDataSource(pathToAudioRecording);
            m_mediaPlayer.prepare();
        } catch (IOException e) {
            Log.e("LOG: ", "Error replaying audio recording. Exception: "+e.getMessage());
            Toast.makeText(getApplicationContext(), "An error occurred attempting to play back audio.", Toast.LENGTH_LONG).show();
            finish();
        }

        m_seekBar.setMax(m_mediaPlayer.getDuration());
        playCycle();
        m_mediaPlayer.start();

        m_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (fromUser)
                {
                    m_mediaPlayer.seekTo(progress);

                    //trying to seek to an earlier part of the audio recording when audio is done playing
                    if (!m_mediaPlayer.isPlaying() && !m_isPaused)
                    {
                        //if the user had the audio recording paused, change the button to say Pause
                        Button b = (Button) findViewById(R.id.pauseContinueAudioPlayBackButton);
                        b.setText("Pause");
                        m_isPaused = false; //the audio recording will no longer be paused

                        //restart the gif
                        m_gifDrawable.start();

                        //gets the seek bar to the correct position
                        m_seekBar.setProgress(progress);
                        m_mediaPlayer.start(); //start the recording again
                        playCycle();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (!m_isPaused && m_mediaPlayer != null)
        {
            m_mediaPlayer.start();
            playCycle();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if (m_mediaPlayer != null)
            m_mediaPlayer.pause();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (m_mediaPlayer != null)
        {
            m_mediaPlayer.stop();
            m_mediaPlayer.release();
        }
        m_handler.removeCallbacks(m_runnable);
    }

    /**
     * Performs the logic for allowing audio playback to be paused and resumed.
     * @param v the view that invokes the method.
     */
    public void pauseContinueAudioPlayBackHandler(View v)
    {
        Button b = (Button) findViewById(R.id.pauseContinueAudioPlayBackButton);

        if (m_isPaused)
        {
            b.setText("Pause");
            m_mediaPlayer.start();
            playCycle();
            m_gifDrawable.start();
            m_isPaused = false;
        }
        else
        {
            b.setText("Play");
            m_mediaPlayer.pause();
            m_gifDrawable.stop();
            m_isPaused = true;
        }
    }

    /**
     * Stops audio playback and finishes the activity.
     * @param v the view that invokes the method.
     */
    public void stopAudioPlayBackButtonHandler(View v)
    {
        m_mediaPlayer.stop();
        m_mediaPlayer.release();
        m_mediaPlayer = null;
        m_handler.removeCallbacks(m_runnable);

        //Go back to the ViewAudioRecordingsActivity
        finish();
    }

    /**
     * Creates up the logic so the seek bar will seek along with
     * the audio recording.
     */
    private void playCycle()
    {
        //update the seek bar to where the media recorder is in the audio recording
        m_seekBar.setProgress(m_mediaPlayer.getCurrentPosition());

        if (m_mediaPlayer != null && m_mediaPlayer.isPlaying())
        {
            if (!m_gifDrawable.isPlaying())
                m_gifDrawable.start();

            m_runnable = new Runnable() {
                @Override
                public void run() {
                    playCycle();
                }
            };

            //makes it so the seek bar will update every 50 ms
            m_handler.postDelayed(m_runnable, 50);
        }
        else if (m_mediaPlayer != null && !m_mediaPlayer.isPlaying())
            m_gifDrawable.stop();
    }
}