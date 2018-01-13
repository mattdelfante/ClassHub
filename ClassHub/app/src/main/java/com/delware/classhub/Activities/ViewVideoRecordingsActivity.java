package com.delware.classhub.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.VideoView;

import com.delware.classhub.DatabaseObjs.VideoRecordingModel;
import com.delware.classhub.OtherClasses.MyMediaController;
import com.delware.classhub.R;
import com.delware.classhub.Singletons.SingletonSelectedClass;

import java.util.List;

public class ViewVideoRecordingsActivity extends AppCompatActivity
{
    private VideoView m_videoView = null;
    private MyMediaController m_mc = null;
    private List<VideoRecordingModel> m_videoRecordings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_video_recordings);

        m_videoRecordings = SingletonSelectedClass.getInstance().getSelectedClass().getVideoRecordings();

        m_videoView = (VideoView) this.findViewById(R.id.videoViewPlayBack);

        m_mc = new MyMediaController(this);

        String uniquePrefix = SingletonSelectedClass.getInstance().getSelectedClass().getId() + "video";
        String pathToRecording = getFilesDir() + "/" + uniquePrefix + "_" + m_videoRecordings.get(0).getName() + ".mp4";

        m_videoView.setVideoPath(pathToRecording);
        m_videoView.setMediaController(m_mc);
        m_mc.setAnchorView(m_videoView);
        m_videoView.start();
    }
}
