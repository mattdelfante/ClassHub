package com.delware.classhub.Activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class RecordAudioActivity extends AppCompatActivity {

    private MediaRecorder m_recorder = null;
    private AudioRecordingModel m_audioRecording = null;
    private boolean m_problemOccurred = false;
    private boolean m_isPaused = false;
    private List<String> m_audioRecordingFiles = null;

    //This needs to implement the media recorder stuff
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_audio);

        m_audioRecording = new AudioRecordingModel();
        m_isPaused = false;
        m_problemOccurred = false;
        m_audioRecordingFiles = new ArrayList<String>();

        try {
            beginRecordingAudio();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Failed to record audio.", Toast.LENGTH_SHORT).show();

            //go back to the AudioRecordingsActivity
            Intent intent = new Intent(RecordAudioActivity.this, AudioRecordingsActivity.class);
            startActivity(intent);
        }
    }

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

    private void stopAudioRecording()
    {
        if (m_recorder != null)
        {
            m_recorder.stop();
            m_recorder.release();
            m_recorder = null;
        }
    }

    public void pauseContinueHandler(View view) throws IOException {
        Button b = (Button) findViewById(R.id.pauseContinueButton);

        //if audio recording is paused
        if (m_isPaused)
        {
            beginRecordingAudio();
            b.setText("Pause Recording");
            m_isPaused = false;
        }
        else
        {
            stopAudioRecording();
            b.setText("Continue Recording");
            m_isPaused = true;
        }
    }

    public void finishAudioRecording(View view)
    {
        String toastMsg;

        stopAudioRecording();

        mergeMediaFiles(true);

        //if no problems occured
        if (m_problemOccurred == false)
        {
            //add the associated class to the model and save it to the database
            m_audioRecording.setAssociatedClass(SingletonSelectedClass.getInstance().getSelectedClass());
            m_audioRecording.save();

            toastMsg = "The audio recording was successfully saved!";
        }
        else
            toastMsg = "The audio recording was unable to be saved.";

        Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT).show();

        //Probably wrap in a try catch
        //now I am done with all of the non-merged files, so delete them
        for (String fileName : m_audioRecordingFiles)
        {
            File f = new File(fileName);

            if (f.delete() == false)
                Log.i("LOG: ", "The audio recording: " + fileName + " was not deleted.");
        }

        //go back to the AudioRecordingsActivity
        Intent intent = new Intent(RecordAudioActivity.this, AudioRecordingsActivity.class);
        startActivity(intent);
    }

    private boolean mergeMediaFiles(boolean isAudio)
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

            String targetFile = createFileName();

            FileChannel fileChannel = new RandomAccessFile(String.format(targetFile), "rw").getChannel();

            container.writeContainer(fileChannel);
            fileChannel.close();
        }
        catch (IOException e)
        {
            m_problemOccurred = true;
            Log.e("LOG: ", "Error merging media files. Exception: "+e.getMessage());
            return false;
        }

        return true;
    }

    private String createFileName()
    {
        String timeStamp = new SimpleDateFormat("MM-dd-yyyy_HHmmss").format(Calendar.getInstance().getTime());

        m_audioRecording.setName(timeStamp);

        //the modified class name removes all characters that aren't valid file names
        String modifiedClassName = SingletonSelectedClass.getInstance().getSelectedClass().getName().replaceAll("[^_a-zA-Z0-9\\.\\-]", "");

        //the fileName has the format /classname_12-16-17_141422.mp4
        String fileName =  "/" + modifiedClassName + "_" + timeStamp + ".mp4";

        return getFilesDir() + fileName;
    }
}
