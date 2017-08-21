package com.delware.classhub;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ClassActivity extends AppCompatActivity {

    private Context m_activityContext = this;
    private Dialog m_addAssignmentDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //I am going to overwrite the action bar for this activity
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activity_class_action_bar);

        setContentView(R.layout.activity_class);

        //get the name of the class at make that the title of the page
        TextView actionBarTextView = (TextView) findViewById(R.id.classActivityActionBarTitle);
        actionBarTextView.setText(SingletonValues.getInstance().getSelectedClassName());

        createAddAssignmentOnClickDialog();
    }

    private void createAddAssignmentOnClickDialog()
    {
        final Dialog dialog = new Dialog(m_activityContext);
        dialog.setContentView(R.layout.activity_class_add_asgnmt_dialog);

        //create the dialog
        m_addAssignmentDialog = dialog;
    }

    public void showAddAssignmentDialog(View v) {m_addAssignmentDialog.show();}
}
