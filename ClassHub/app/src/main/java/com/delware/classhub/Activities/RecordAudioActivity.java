package com.delware.classhub.Activities;

import android.content.DialogInterface;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.coremedia.iso.boxes.Container;
import com.delware.classhub.DatabaseObjs.AudioRecordingModel;
import com.delware.classhub.R;
import com.delware.classhub.Singletons.SingletonSelectedClass;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Overview: This class allows users to create an audio recording
 * and associate the audio recording with a particular class
 * @author Matt Del Fante
 */
public class RecordAudioActivity extends AppCompatActivity
{
    //allows audio recordings to be created
    private MediaRecorder m_recorder = null;

    //the audio recording that is being created
    private AudioRecordingModel m_audioRecording = null;

    //gets set to true if any issues occur when creating an audio recording
    private boolean m_problemOccurred = false;

    //true when the audio recording is paused, false if not
    private boolean m_isPaused = false;

    //all of the audio files that are created every time the user pauses an audio recording
    //and then resumes the audio recording
    private List<String> m_audioRecordingFiles = null;

    //This needs to implement the media recorder stuff
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //overwrite the action bar for this activity with a different one
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_action_bar_layout);

        setContentView(R.layout.activity_record_audio);

        //Set the content of the action bar title
        TextView actionBarTextView = (TextView) findViewById(R.id.defaultActionBarTitle);
        actionBarTextView.setText("Record Audio");

        m_audioRecording = new AudioRecordingModel();
        m_isPaused = false;
        m_problemOccurred = false;
        m_audioRecordingFiles = new ArrayList<>();

        try {
            beginRecordingAudio();
        } catch (Exception e) {
            //couldn't create the recording, display an error message
            Toast.makeText(getApplicationContext(), "Failed to record audio.", Toast.LENGTH_SHORT).show();

            //go back to the AudioRecordingsActivity
            finish();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        stopAudioRecording();

        //delete all of the audio recording files
        for (String fileName : m_audioRecordingFiles)
        {
            File f = new File(fileName);

            if (!f.delete())
                Log.i("LOG: ", "The audio recording: " + fileName + " was not deleted.");
        }
    }

    /**
     * Begins recording a .mp4 audio file and saves it to internal storage on the device.
     * @throws IOException if the media recorder isn't set up properly.
     */
    private void beginRecordingAudio() throws IOException
    {
        if (m_recorder != null)
            m_recorder.release();

        //set up the media recorder to record mp4s
        m_recorder = new MediaRecorder();
        m_recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        m_recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        m_recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        //If the date is 12/14/17 and the time is 2:14 pm, the fileName will read: /20171214_141422.mp4
        String fileName = "/" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime()) + ".mp4";

        m_audioRecordingFiles.add(getFilesDir()+fileName);

        m_recorder.setOutputFile(m_audioRecordingFiles.get(m_audioRecordingFiles.size() - 1));

        m_recorder.prepare();

        // Recording is now started
        m_recorder.start();
    }

    /**
     * Stops recording audio.
     */
    private void stopAudioRecording()
    {
        if (m_recorder != null)
        {
            m_recorder.stop();
            m_recorder.release();
            m_recorder = null;
        }
    }

    /**
     * Either allows an audio recording to be paused or resumed depending on what
     * context the method is called in.
     * @param view The view that invokes the method.
     */
    public void pauseContinueAudioRecordingHandler(View view) {
        Button b = (Button) findViewById(R.id.pauseContinueAudioRecordingButton);

        //if audio recording is paused
        if (m_isPaused)
        {
            //start another audio recording
            try{
                beginRecordingAudio();
                b.setText("Pause Recording");
                m_isPaused = false;
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "The audio recording was unable to continue.", Toast.LENGTH_SHORT).show();
                Log.i("LOG: ", "The audio recording: was not able to be continued. Exception: " + e.getMessage());
                finishAudioRecording(null);
            }
        }
        else
        {
            //end the current audio recording
            stopAudioRecording();
            b.setText("Continue Recording");
            m_isPaused = true;
        }
    }

    /**
     * Finishes recording audio for the current audio recording. Invokes a method that displays
     * an alert dialog which provides the option for the user to delete the audio recording or
     * save the audio recording.
     * @param view The view that invokes the method.
     */
    public void finishAudioRecording(View view)
    {
        stopAudioRecording();

        //merge all of the m
        mergeMediaFiles(true);

        //now I am done with all of the non-merged files, so delete them
        for (String fileName : new ArrayList<>(m_audioRecordingFiles))
        {
            File f = new File(fileName);

            if (!f.delete())
                Log.i("LOG: ", "The audio recording: " + fileName + " was not deleted.");
            else
                m_audioRecordingFiles.remove(fileName);
        }

        //if no problems occurred
        if (!m_problemOccurred)
            //ask the user what he or she would like to do with the audio recording
            promptUserFoAction();
        else {
            Toast.makeText(getApplicationContext(), "The audio recording was unable to be created.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Displays an alert dialog which gives the user an option to delete the audio recording or
     * save the audio recording. The method performs the corresponding actions depending on which
     * action the user selects.
     */
    private void promptUserFoAction()
    {
        //the unique prefix for each class' audio recordings
        String uniquePrefix = SingletonSelectedClass.getInstance().getSelectedClass().getId() + "audio";
        String filePath = getFilesDir() + "/" + uniquePrefix + "_" + m_audioRecording.getName() + ".mp4";
        final File f = new File(filePath);

        //simple alert dialog for confirmation
        final AlertDialog.Builder adb = new AlertDialog.Builder(this);

        adb.setTitle("What Would You Like To Do?");

        adb.setMessage("Do you want to save the audio recording or delete the audio recording?");

        adb.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //add the associated class to the model and save it to the database
                m_audioRecording.setAssociatedClass(SingletonSelectedClass.getInstance().getSelectedClass());
                m_audioRecording.save();

                Toast.makeText(getApplicationContext(), "The audio recording was successfully saved!", Toast.LENGTH_SHORT).show();

                //go back to the AudioRecordingsActivity
                finish();
            }
        });

        adb.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                confirmDeletionOfAudioRecording(f, adb);
            }
        });

        //makes it so the user can't press the back button on his or her device
        adb.setCancelable(false);

        adb.show();
    }

    private void confirmDeletionOfAudioRecording(final File f, final AlertDialog.Builder prevAdb) {
        //simple alert dialog for confirmation
        new AlertDialog.Builder(this)
                .setTitle("Wait...")
                .setMessage("Are you sure you want to delete the audio recording?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String toastMsg;

                        //if deleting the audio recording was successful
                        if (f.delete())
                            toastMsg = "The audio recording was successfully deleted!";
                        else
                            toastMsg = "An issue occurred deleting the audio recording";

                        Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT).show();

                        //go back to the AudioRecordingsActivity
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //show the previous alert dialog builder
                        prevAdb.show();
                    }
                })
                .setCancelable(false)
                .show();
    }

    /**
     * Merges multiple audio recording files into one file. This is how the pause and resume
     * functionality works. Every time an audio recording is resumed, a new audio recording
     * is created. So this method merges all of the audio recording files into one.
     * @param isAudio A flag representing if the audio recording is an audio recording or not
     */
    private void mergeMediaFiles(boolean isAudio)
    {
        try
        {
            String mediaKey = isAudio ? "soun" : "vide";

            List<Movie> listMovies = new ArrayList<>();
            for (String filename : m_audioRecordingFiles)
            {
                listMovies.add(MovieCreator.build(filename));
            }

            List<Track> listTracks = new LinkedList<>();
            for (Movie movie : listMovies)
            {
                for (Track track : movie.getTracks())
                {
                    if (track.getHandler().equals(mediaKey))
                    {
                        listTracks.add(track);
                    }
                }
            }

            Movie outputMovie = new Movie();
            if (!listTracks.isEmpty())
            {
                outputMovie.addTrack(new AppendTrack(listTracks.toArray(new Track[listTracks.size()])));
            }

            Container container = new DefaultMp4Builder().build(outputMovie);

            String targetFile = createFilePath();

            FileChannel fileChannel = new RandomAccessFile(String.format(targetFile), "rw").getChannel();

            container.writeContainer(fileChannel);
            fileChannel.close();
        }
        catch (IOException e)
        {
            m_problemOccurred = true;
            Log.e("LOG: ", "Error merging media files. Exception: "+e.getMessage());
        }
    }

    /**
     * Creates a string that represents a path to the internal storage of the phone where the
     * audio recording will be saved.
     * @return The string representation of the path to the audio recording file.
     */
    private String createFilePath()
    {
        String timeStamp = new SimpleDateFormat("MM-dd-yyyy_HHmmss").format(Calendar.getInstance().getTime());

        m_audioRecording.setName(timeStamp);

        //the modified class name removes all characters that aren't valid file names
        String uniquePrefix = SingletonSelectedClass.getInstance().getSelectedClass().getId() + "audio";

        //the fileName has the format /uniquePrefix_12-16-17_141422.mp4
        String fileName =  "/" + uniquePrefix + "_" + timeStamp + ".mp4";

        return getFilesDir() + fileName;
    }
}
