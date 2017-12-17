package com.delware.classhub.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
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
import android.widget.Toast;

import com.delware.classhub.DatabaseObjs.AudioRecordingModel;
import com.delware.classhub.DatabaseObjs.ClassModel;
import com.delware.classhub.R;
import com.delware.classhub.Singletons.SingletonSelectedClass;
import com.delware.classhub.Singletons.SingletonWeekView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ViewAudioRecordingsActivity extends AppCompatActivity {

    private ArrayAdapter<String> m_audioRecordingsListViewAdapter;
    private final Context m_activityContext = this;
    private Dialog m_audioRecordingDialog = null;
    private Dialog m_renameAudioRecordingDialog = null;
    private List<AudioRecordingModel> m_audioRecordings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_audio_recordings);

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

    private void initializeListView() {

        List<String> audioRecordingNames = new ArrayList<>();

        for (AudioRecordingModel m : m_audioRecordings)
        {
            audioRecordingNames.add(m.getName());
        }

        m_audioRecordingsListViewAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, audioRecordingNames);

        ListView audioRecordingsListView = (ListView) findViewById(R.id.audioRecordingsListView);
        audioRecordingsListView.setAdapter(m_audioRecordingsListViewAdapter);

        //set the click stuff
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

    private void createDialogBoxForClickingRecording(int index) {
        final AudioRecordingModel selectedRecording = m_audioRecordings.get(index);
        final String modifiedClassName = SingletonSelectedClass.getInstance().getSelectedClass().getName().replaceAll("[^_a-zA-Z0-9\\.\\-]", "");
        final String pathToRecording = getFilesDir() + "/" + modifiedClassName + "_" + selectedRecording.getName() + ".mp4";

        final Dialog dialog = new Dialog(m_activityContext);
        dialog.setContentView(R.layout.activity_view_audio_recordings_dialog);

        //all the components in the dialog box
        final Button playAudioRecordingButton = (Button) dialog.findViewById(R.id.playAudioRecordingButton);
        final Button renameAudioRecordingButton = (Button) dialog.findViewById(R.id.renameAudioRecordingButton);
        final Button deleteAudioRecordingButton = (Button) dialog.findViewById(R.id.deleteAudioRecordingButton);

        playAudioRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //temporary, I will create the UI for this part later
                //play the audio recording
                MediaPlayer tempPlayer = new MediaPlayer();
                try {
                    tempPlayer.setDataSource(pathToRecording);
                    tempPlayer.prepare();
                } catch (IOException e) {
                    Log.e("LOG: ", "Error replaying audio recording. Exception: "+e.getMessage());
                    Toast.makeText(getApplicationContext(), "An error occurred attempting to play back audio.", Toast.LENGTH_LONG).show();
                    return;
                }
                tempPlayer.start();
            }
        });

        renameAudioRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                createRenameAudioRecordingDialog(selectedRecording, pathToRecording);
                m_renameAudioRecordingDialog.show();
            }
        });

        deleteAudioRecordingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make my dialog disappear
                dialog.dismiss();

                final String name = selectedRecording.getName();
                final File f = new File(pathToRecording);

                //simple alert dialog for confimation
                new AlertDialog.Builder(m_activityContext)
                        .setTitle("Wait...")
                        .setMessage("Are you sure you want to delete the audio recording: " + name)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (f.delete() == false)
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
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        m_audioRecordingDialog = dialog;
    }

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

                //this is gross but it works
                for (int i = 0; i < updatedName.length(); i++)
                {
                    //if an illegal character is in the name
                    if (updatedName.substring(i, i+1).matches("[^_a-zA-Z0-9\\.\\-]"))
                    {
                        Toast.makeText(getApplicationContext(),
                                "An audio recording cannot have special characters.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                for (AudioRecordingModel model : recordings)
                {
                    if (model.getName().equals(updatedName))
                    {
                        Toast.makeText(getApplicationContext(),
                                "Error: Every audio recording must have a unique name.", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        return;
                    }
                }

                final String modifiedClassName = SingletonSelectedClass.getInstance().getSelectedClass().getName().replaceAll("[^_a-zA-Z0-9\\.\\-]", "");
                String renamedRecordingPath = getFilesDir() + "/" + modifiedClassName + "_" + updatedName + ".mp4";

                //attempt to update the file system
                File oldFile = new File(pathToRecordingOldName);
                File newFile = new File(renamedRecordingPath);

                //if rename file rename worked
                if (oldFile.renameTo(newFile) == true)
                {
                    //update the database
                    selectedRecording.setName(updatedName);
                    selectedRecording.save();
                }
                else
                {
                    //give an error message
                    Toast.makeText(getApplicationContext(), "Error: Failed to rename the audio recording.", Toast.LENGTH_LONG).show();
                }

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
