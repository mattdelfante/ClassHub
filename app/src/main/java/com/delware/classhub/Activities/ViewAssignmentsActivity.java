package com.delware.classhub.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.delware.classhub.CustomAdapters.ViewAssignmentsArrayAdapter;
import com.delware.classhub.DatabaseObjs.AssignmentModel;
import com.delware.classhub.DatabaseObjs.ClassModel;
import com.delware.classhub.R;
import com.delware.classhub.Singletons.SingletonSelectedClass;
import com.delware.classhub.Singletons.SingletonWeekView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ViewAssignmentsActivity extends AppCompatActivity {

    private ViewAssignmentsArrayAdapter m_adapter = null;
    private final Context m_activityContext = this;
    private Dialog m_assignmentDialog = null;
    private AssignmentModel m_selectedAssignment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activity_view_assignments_action_bar);

        setContentView(R.layout.activity_view_assignments);

        initializeListView();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (m_assignmentDialog != null)
            m_assignmentDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (m_assignmentDialog != null)
            m_assignmentDialog.dismiss();
    }

    private void initializeListView() {
        m_adapter = new ViewAssignmentsArrayAdapter(this);

        ListView assignmentsListView = (ListView) findViewById(R.id.assignmentsListView);
        assignmentsListView.setAdapter(m_adapter);

        //set the click stuff
        assignmentsListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String assignmentName = String.valueOf(parent.getItemAtPosition(position));
                        clickedAssignmentHandler(assignmentName);
                    }
                }
        );
    }

    private void clickedAssignmentHandler(String assignmentName)
    {
        List<AssignmentModel> assignments = SingletonSelectedClass.getInstance().getSelectedClass().getAssignments();

        for (AssignmentModel assignment : assignments)
        {
            if (assignment.getName().equals(assignmentName))
            {
                m_selectedAssignment = assignment;
                break;
            }
        }

        createDialogBoxForPressingAssignment();
        m_assignmentDialog.show();
    }

    private void createDialogBoxForPressingAssignment()
    {
        final Dialog dialog = new Dialog(m_activityContext);
        dialog.setContentView(R.layout.activity_view_assignments_asgnmt_dialog);

        //all the components in the dialog box
        final EditText editAssignmentNameInput = (EditText) dialog.findViewById(R.id.editAssignmentNameInput);
        final Button editDueDateButton = (Button) dialog.findViewById(R.id.editDueDateButton);
        final Button editPriorityLevelButton = (Button) dialog.findViewById(R.id.editPriorityLevelButton);
        final Button completedAssignmentButton = (Button) dialog.findViewById(R.id.completedAssignmentButton);
        final Button deleteAssignmentButton = (Button) dialog.findViewById(R.id.deleteAssignmentButton);
        final EditText editAdditionalNotes = (EditText) dialog.findViewById(R.id.editAdditionalNotes);
        final Button viewAssingmentCancelButton = (Button) dialog.findViewById(R.id.viewAssingmentCancelButton);
        final Button viewAssingmentDoneButton = (Button) dialog.findViewById(R.id.viewAssingmentDoneButton);

        //start off with the dialog holding the values of the assignment
        editAssignmentNameInput.setText(m_selectedAssignment.getName());
        editAssignmentNameInput.addTextChangedListener(new TextWatcher() {
            //start off as a disabled done button
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s)
            {
                String input = s.toString();

                //not all whitespace or empty
                if (input.trim().length() > 0)
                {
                    m_selectedAssignment.setName(input);

                    if (isValidAssignment())
                        viewAssingmentDoneButton.setEnabled(true);
                }
                else
                {
                    m_selectedAssignment.setName(null);
                    viewAssingmentDoneButton.setEnabled(false);
                }
            }
        });

        //due date stuff
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(m_selectedAssignment.getDueDate());

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                //set the date that the assginment is due
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                //after the date is selected, allow the user to pick the time that the assignment is due
                TimePickerDialog tpd = new TimePickerDialog(m_activityContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        calendar.set(Calendar.MINUTE, selectedMinute);

                        //Now the assignments date is properly set
                        m_selectedAssignment.setDueDate(calendar.getTime());

                        if (isValidAssignment())
                            viewAssingmentDoneButton.setEnabled(true);

                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                tpd.show();
            }

        };

        //display the calendar date picker when clicking the due date button
        editDueDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(ViewAssignmentsActivity.this, date,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });
        //due date stuff

        //priority level logic
        editPriorityLevelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                CharSequence[] choices = new CharSequence[3];
                choices[0] = "Priority Level One";
                choices[1] = "Priority Level Two";
                choices[2] = "Priority Level Three";

                AlertDialog.Builder adBuilder = new AlertDialog.Builder(m_activityContext);
                adBuilder.setTitle("Select A Priority Level");

                adBuilder.setSingleChoiceItems(choices, m_selectedAssignment.getPriorityLevel() - 1, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //index 0 is priority level 1, so add 1 to the index to get the correct priority level
                        m_selectedAssignment.setPriorityLevel(which + 1);
                    }
                })
                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        });

                adBuilder.show();
            }
        });
        //priority level stuff

        //completed assignment button
        completedAssignmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //priority level 4 means the assignment is completed
                m_selectedAssignment.setPriorityLevel(4);
                m_selectedAssignment.setIsCompleted(true);

                //add the assignment to the database
                m_selectedAssignment.save();
                SingletonWeekView.getInstance().getWeekView().notifyDatasetChanged();
                Toast.makeText(getApplicationContext(), "The assignment, " + m_selectedAssignment.getName() +
                        ", was marked as completed.", Toast.LENGTH_SHORT).show();

                //Reset the activity while making the transition seamless
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });
        //completed assignment button

        //delete assingment button
        deleteAssignmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = m_selectedAssignment.getName();
                m_selectedAssignment.delete();
                Toast.makeText(getApplicationContext(), "The assignment, " + name + ", was deleted.", Toast.LENGTH_SHORT).show();
                SingletonWeekView.getInstance().getWeekView().notifyDatasetChanged();

                //Reset the activity while making the transition seamless
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });
        //delete assingment button

        //additional notes stuff
        editAdditionalNotes.setText(m_selectedAssignment.getAdditionalNotes());
        editAdditionalNotes.addTextChangedListener(new TextWatcher() {
            //start off as a disabled done button
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();
                //not all whitespace or empty
                if (input.trim().length() > 0)
                    m_selectedAssignment.setAdditionalNotes(input);
                else
                    m_selectedAssignment.setAdditionalNotes("");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        //additional notes stuff

        //cancel button stuff
        viewAssingmentCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //cancel button stuff

        //done button stuff
        viewAssingmentDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add the assignment to the database
                m_selectedAssignment.save();
                SingletonWeekView.getInstance().getWeekView().notifyDatasetChanged();

                //Reset the activity while making the transition seamless
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });
        //done button stuff

        m_assignmentDialog = dialog;
    }

    /**
     * Determines weather or not the assignment is a valid assignment. An assignment is
     * valid if it has a name and a due date.
     * @return True if a valid assignment false if not.
     */
    private Boolean isValidAssignment()
    {
        return m_selectedAssignment.getName() != null && m_selectedAssignment.getDueDate() != null;
    }
}
