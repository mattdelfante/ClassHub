package com.delware.classhub.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.delware.classhub.DatabaseModels.VideoRecordingModel;
import com.delware.classhub.R;
import com.delware.classhub.Singletons.SingletonSelectedClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Overview: This class provides the choice for the user to either create a
 * video recording or view previously created video recordings.
 * @author Matt Del Fante
 */
public class VideoRecordingsActivity extends AppCompatActivity {

    //A code that represents requesting to record video
    final private int REQUEST_VIDEO = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //overwrite the action bar for this activity with a different one
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_action_bar_layout);

        setContentView(R.layout.activity_video_recordings);

        //get the name of the class at make that the title of the page
        TextView actionBarTextView = (TextView) findViewById(R.id.defaultActionBarTitle);
        actionBarTextView.setText(SingletonSelectedClass.getInstance().getSelectedClass().getName());
    }

    /**
     * Allows the user to use the native video app on his or her android device to make a
     * video recording.
     * @param v The calling View.
     */
    public void goToRecordVideoActivity(View v)
    {
        Intent callVideoAppIntent = new Intent();
        callVideoAppIntent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(callVideoAppIntent, REQUEST_VIDEO);
    }

    /**
     * This method determines what happens the onActivityResult function is called.
     * @param requestCode The request of the activity.
     * @param resultCode The result of the activity.
     * @param data The return value from the activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if the user just finished creating the video recording
        if (requestCode == REQUEST_VIDEO && resultCode == RESULT_OK)
        {
            String toastMsg;
            String timeStamp = new SimpleDateFormat("MM-dd-yyyy_HHmmss").format(Calendar.getInstance().getTime());
            String uniquePrefix = SingletonSelectedClass.getInstance().getSelectedClass().getId() + "video";
            String fileName =  "/" + uniquePrefix + "_" + timeStamp + ".mp4";

            //get the uri of the video recording
            Uri uri = data.getData();
            String oldFilePath = getRealPathFromUri(getApplicationContext(), uri);

            File oldFile = new File(oldFilePath);
            File newFile = new File(getFilesDir() + fileName);

            //transfer the video file from the camera app to internal storage of the app
            if (transferFileToInternalStorage(oldFile, newFile))
            {
                VideoRecordingModel model = new VideoRecordingModel();
                model.setName(timeStamp);
                model.setAssociatedClass(SingletonSelectedClass.getInstance().getSelectedClass());
                model.save();

                if (!oldFile.delete())
                    Log.e("LOG: ", "The original video recording was not deleted.");

                toastMsg = "The video recording was successfully saved to the app!";
            }
            else
                toastMsg = "The video recording was saved to your camera roll.";

            Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Transfers a .mp4 file from the android device's native camera app to the internal storage
     * of Class Hub.
     * @param src The path of the .mp4 file in internal storage.
     * @param dest The path to internal storage where the .mp4 file will be stored.
     * @return True on successful transfer, else false.
     */
    private static boolean transferFileToInternalStorage(File src, File dest)
    {
        boolean returnVal = false;
        FileChannel inChannel = null;
        FileChannel outChannel = null;

        try
        {
            //transfers a file to internal storage
            inChannel = new FileInputStream(src).getChannel();
            outChannel = new FileOutputStream(dest).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);

            returnVal = true;
        }
        catch (Exception e)
        {
            Log.e("LOG: ", "Error transferring file to internal storage. Exception: "+e.getMessage());
        }
        finally
        {
            if (inChannel != null)
            {
                try {
                    inChannel.close();
                } catch (IOException e) {
                    Log.e("LOG: ", "Error closing input FileChannel. Exception: "+e.getMessage());
                }
            }

            if (outChannel != null)
            {
                try {
                    outChannel.close();
                } catch (IOException e) {
                    Log.e("LOG: ", "Error closing output FileChannel. Exception: " + e.getMessage());
                }
            }
        }

        return returnVal;
    }

    /**
     * This method determines the physical file path of a .mp4 file that was saved to the native
     * camera app of the android device from a URI.
     * @param context The application's context
     * @param contentUri The uri of the .mp4 file.
     * @return The physical file path of the .mp4 file.
     */
    private static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Video.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Navigates the user to the ViewAudioRecordingsActivity so the user can
     * interact with existing audio recordings.
     * @param v The calling View.
     */
    public void goToViewVideoRecordingsActivity(View v)
    {
        Intent intent = new Intent(VideoRecordingsActivity.this, ViewVideoRecordingsActivity.class);
        startActivity(intent);
    }
}
