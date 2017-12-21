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

import com.delware.classhub.DatabaseObjs.AudioRecordingModel;
import com.delware.classhub.R;
import com.delware.classhub.Singletons.SingletonSelectedClass;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Overview: This class allows users to interact with existing audio recordings
 * associated with a particular class
 * @author Matt Del Fante
 */
public class ViewAudioRecordingsActivity extends AppCompatActivity
{
    //current activity context
    private final Context m_activityContext = this;

    //dialog box that pops up when an audio recording is pressed
    private Dialog m_audioRecordingDialog = null;

    //dialog box that pops up when the user attempts to rename an audio recording
    private Dialog m_renameAudioRecordingDialog = null;

    //All of the audio recordings for a particular class
    private List<AudioRecordingModel> m_audioRecordings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //overwrite the action bar for this activity with a different one
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_action_bar_layout);

        setContentView(R.layout.activity_view_audio_recordings);

        //Set the content of the action bar title
        TextView actionBarTextView = (TextView) findViewById(R.id.defaultActionBarTitle);
        actionBarTextView.setText("Audio Recordings");

        m_audioRecordings = SingletonSelectedClass.getInstance().getSelectedClass().getAudioRecordings();

        initializeListView();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (m_audioRecordingDialog != null)
            m_audioRecordingDialog.dismiss();

        if (m_renameAudioRecordingDialog != null)
            m_renameAudioRecordingDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (m_audioRecordingDialog != null)
            m_audioRecordingDialog.dismiss();

        if (m_renameAudioRecordingDialog != null)
            m_renameAudioRecordingDialog.dismiss();
    }

    /**
     * Initializes the actions of the Classes list view in the app
     */
    private void initializeListView() {

        List<String> audioRecordingNames = new ArrayList<>();

        //adds all of the names of the audio recordings to the list view
        for (AudioRecordingModel m : m_audioRecordings)
            audioRecordingNames.add(m.getName());

        ArrayAdapter<String> audioRecordingsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, audioRecordingNames);

        ListView audioRecordingsListView = (ListView) findViewById(R.id.audioRecordingsListView);
        audioRecordingsListView.setAdapter(audioRecordingsAdapter);

        //when a list view item is clicked, these functions are invoked
        audioRecordingsListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        createDialogBoxForClickingRecording(position);
                        m_audioRecordingDialog.show();
                    }
                }
        );
    }

    /**
     * Creates a dialog box for when an audio recording list view element is clicked.
     * The dialog box allows users to play, rename and delete audio recordings
     * @param index the index of the list view item that was selected.
     */
    private void createDialogBoxForClickingRecording(int index)
    {
        //The AudioRecordingModel that is associated with the audio recording that was selected.
        final AudioRecordingModel selectedRecording = m_audioRecordings.get(index);
        String uniquePrefix = SingletonSelectedClass.getInstance().getSelectedClass().getId() + "audio";

        //The path to audio recording on the file system
        final String pathToRecording = getFilesDir() + "/" + uniquePrefix + "_" + selectedRecording.getName() + ".mp4";

        final Dialog dialog = new Dialog(m_activityContext);
        dialog.setContentView(R.layout.activity_view_audio_recordings_dialog);

        //all the components in the dialog box
        final Button playAudioRecordingButton = (Button) dialog.findViewById(R.id.playAudioRecordingButton);
        final Button renameAudioRecordingButton = (Button) dialog.findViewById(R.id.renameAudioRecordingButton);
        final Button deleteAudioRecordingButton = (Button) dialog.findViewById(R.id.deleteAudioRecordingButton);

        playAudioRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //Go to the PlayAudioRecordingActivity
                Intent i = new Intent(ViewAudioRecordingsActivity.this, PlayAudioRecordingActivity.class);
                //pass the PlayAudioRecordingActivity the pathToRecording
                i.putExtra("audioRecordingPath", pathToRecording);
                startActivity(i);
            }
        });

        renameAudioRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show a different dialog box
                dialog.dismiss();
                createRenameAudioRecordingDialog(selectedRecording, pathToRecording);
                m_renameAudioRecordingDialog.show();
            }
        });

        deleteAudioRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                final String name = selectedRecording.getName();
                final File f = new File(pathToRecording);

                //Show an alert dialog confirming if the user is sure he/she wants to delete the audio
                //recording
                new AlertDialog.Builder(m_activityContext)
                        .setTitle("Wait...")
                        .setMessage("Are you sure you want to delete the audio recording: " + name)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //delete the audio recording from internal storage
                                if (!f.delete())
                                    //only executes if the audio recording could not be deleted
                                    Log.i("LOG: ", "The audio recording: " + name + " was not deleted.");

                                //delete the audio recording from the db
                                selectedRecording.delete();

                                Toast.makeText(getApplicationContext(), "The audio recording: " + name + " was deleted.", Toast.LENGTH_SHORT).show();

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

        m_audioRecordingDialog = dialog;
    }

    /**
     * Creates a dialog box that allows a user to rename an audio recording
     * @param selectedRecording the AudioRecordingModel that is going to be renamed
     * @param pathToRecordingOldName the path to the old-named audio recording in internal storage
     */
    private void createRenameAudioRecordingDialog(final AudioRecordingModel selectedRecording, final String pathToRecordingOldName)
    {
        final Dialog dialog = new Dialog(m_activityContext);
        dialog.setContentView(R.layout.activity_view_audio_recordings_rename_recording_dialog);

        //all the components in the dialog box
        final EditText input = (EditText) dialog.findViewById(R.id.renameAudioRecordingInput);
        final Button doneButton = (Button) dialog.findViewById(R.id.renameAudioRecordingDoneButton);
        final Button cancelButton = (Button) dialog.findViewById(R.id.renameAudioRecordingCancelButton);

        input.setText(selectedRecording.getName());

        final List<AudioRecordingModel> recordings = SingletonSelectedClass.getInstance().getSelectedClass().getAudioRecordings();

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
                                "An audio recording cannot have special characters.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                for (AudioRecordingModel model : recordings)
                {
                    //if there is an audio recording already with this name
                    if (model.getName().equals(updatedName))
                    {
                        //display an error message
                        Toast.makeText(getApplicationContext(),
                                "This class already has an audio recording with the name: " +
                                        updatedName + ".", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                String uniquePrefix = SingletonSelectedClass.getInstance().getSelectedClass().getId() + "audio";
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
                    Toast.makeText(getApplicationContext(), "Error: Failed to rename the audio recording.", Toast.LENGTH_LONG).show();

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

        m_renameAudioRecordingDialog = dialog;
    }
}