package com.delware.classhub.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.delware.classhub.DatabaseObjs.VideoRecordingModel;
import com.delware.classhub.R;
import com.delware.classhub.Singletons.SingletonSelectedClass;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewVideoRecordingsActivity extends AppCompatActivity
{
    //current activity context
    private final Context m_activityContext = this;

    private List<VideoRecordingModel> m_videoRecordings = null;

    //dialog box that pops up when an audio recording is pressed
    private Dialog m_videoRecordingDialog = null;

    //dialog box that pops up when the user attempts to rename an audio recording
    private Dialog m_renameVideoRecordingDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //overwrite the action bar for this activity with a different one
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_action_bar_layout);

        setContentView(R.layout.activity_view_video_recordings);

        //Set the content of the action bar title
        TextView actionBarTextView = (TextView) findViewById(R.id.defaultActionBarTitle);
        actionBarTextView.setText("Video Recordings");

        m_videoRecordings = SingletonSelectedClass.getInstance().getSelectedClass().getVideoRecordings();

        initializeListView();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (m_videoRecordingDialog != null)
            m_videoRecordingDialog.dismiss();

        if (m_renameVideoRecordingDialog != null)
            m_renameVideoRecordingDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (m_videoRecordingDialog != null)
            m_videoRecordingDialog.dismiss();

        if (m_renameVideoRecordingDialog != null)
            m_renameVideoRecordingDialog.dismiss();
    }

    private void initializeListView()
    {
        List<String> videoRecordingNames = new ArrayList<>();

        //adds all of the names of the audio recordings to the list view
        for (VideoRecordingModel m : m_videoRecordings)
            videoRecordingNames.add(m.getName());

        ArrayAdapter<String> videoRecordingsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, videoRecordingNames);

        ListView videoRecordingsListView = (ListView) findViewById(R.id.videoRecordingsListView);
        videoRecordingsListView.setAdapter(videoRecordingsAdapter);

        //when a list view item is clicked, these functions are invoked
        videoRecordingsListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        createDialogBoxForClickingRecording(position);
                        m_videoRecordingDialog.show();
                    }
                }
        );
    }

    private void createDialogBoxForClickingRecording(int index)
    {
        //The AudioRecordingModel that is associated with the audio recording that was selected.
        final VideoRecordingModel selectedRecording = m_videoRecordings.get(index);
        String uniquePrefix = SingletonSelectedClass.getInstance().getSelectedClass().getId() + "video";

        //The path to audio recording on the file system
        final String pathToRecording = getFilesDir() + "/" + uniquePrefix + "_" + selectedRecording.getName() + ".mp4";

        final Dialog dialog = new Dialog(m_activityContext);
        dialog.setContentView(R.layout.activity_view_video_recordings_dialog);

        //all the components in the dialog box
        final Button playVideoRecordingButton = (Button) dialog.findViewById(R.id.playVideoRecordingButton);
        final Button renameVideoRecordingButton = (Button) dialog.findViewById(R.id.renameVideoRecordingButton);
        final Button deleteVideoRecordingButton = (Button) dialog.findViewById(R.id.deleteVideoRecordingButton);

        playVideoRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //Go to the PlayAudioRecordingActivity
                Intent i = new Intent(ViewVideoRecordingsActivity.this, PlayVideoRecordingActivity.class);
                //pass the PlayAudioRecordingActivity the pathToRecording
                i.putExtra("videoRecordingPath", pathToRecording);
                startActivity(i);
            }
        });

        renameVideoRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show a different dialog box
                dialog.dismiss();
                createRenameVideoRecordingDialog(selectedRecording, pathToRecording);
                m_renameVideoRecordingDialog.show();
            }
        });

        deleteVideoRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                final String name = selectedRecording.getName();
                final File f = new File(pathToRecording);

                //Show an alert dialog confirming if the user is sure he/she wants to delete the audio
                //recording
                new AlertDialog.Builder(m_activityContext)
                        .setTitle("Wait...")
                        .setMessage("Are you sure you want to delete the video recording: " + name)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //delete the audio recording from internal storage
                                if (!f.delete())
                                    //only executes if the audio recording could not be deleted
                                    Log.i("LOG: ", "The video recording: " + name + " was not deleted.");

                                //delete the audio recording from the db
                                selectedRecording.delete();

                                Toast.makeText(getApplicationContext(), "The video recording: " + name + " was deleted.", Toast.LENGTH_SHORT).show();

                                //Reset the activity while making the transition seamless
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                overridePendingTransition(0, 0);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
            }
        });

        m_videoRecordingDialog = dialog;
    }

    private void createRenameVideoRecordingDialog(final VideoRecordingModel selectedRecording, final String pathToRecordingOldName)
    {
        final Dialog dialog = new Dialog(m_activityContext);
        dialog.setContentView(R.layout.activity_view_video_recordings_rename_recording_dialog);

        //all the components in the dialog box
        final EditText input = (EditText) dialog.findViewById(R.id.renameVideoRecordingInput);
        final Button doneButton = (Button) dialog.findViewById(R.id.renameVideoRecordingDoneButton);
        final Button cancelButton = (Button) dialog.findViewById(R.id.renameVideoRecordingCancelButton);

        input.setText(selectedRecording.getName());

        final List<VideoRecordingModel> recordings = SingletonSelectedClass.getInstance().getSelectedClass().getVideoRecordings();

        //done button is only available to show when there is information inputted into
        //the edit text associated with the dialog box
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            //enabled done button if a class name was entered
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();

                //not all whitespace or empty
                if (input.trim().length() > 0)
                    doneButton.setEnabled(true);
                else
                    doneButton.setEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedName = input.getText().toString();

                for (int i = 0; i < updatedName.length(); i++)
                {
                    //if an illegal character is in the audio recording name
                    if (updatedName.substring(i, i+1).matches("[^ _a-zA-Z0-9\\.\\-]"))
                    {
                        //display an error message
                        Toast.makeText(getApplicationContext(),
                                "An video recording cannot have special characters.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                for (VideoRecordingModel model : recordings)
                {
                    //if there is an audio recording already with this name
                    if (model.getName().equals(updatedName))
                    {
                        //display an error message
                        Toast.makeText(getApplicationContext(),
                                "This class already has an video recording with the name: " +
                                        updatedName + ".", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                String uniquePrefix = SingletonSelectedClass.getInstance().getSelectedClass().getId() + "video";
                String renamedRecordingPath = getFilesDir() + "/" + uniquePrefix + "_" + updatedName + ".mp4";

                //attempt to update the file system of the changed name
                File oldFile = new File(pathToRecordingOldName);
                File newFile = new File(renamedRecordingPath);

                //if renaming the file worked
                if (oldFile.renameTo(newFile))
                {
                    //update the database to hold the updated name
                    selectedRecording.setName(updatedName);
                    selectedRecording.save();
                }
                else
                    //give an error message
                    Toast.makeText(getApplicationContext(), "Error: Failed to rename the video recording.", Toast.LENGTH_LONG).show();

                //Reset the activity while making the transition seamless
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input.setText("");
                dialog.dismiss();
            }
        });

        m_renameVideoRecordingDialog = dialog;
    }
}
