package com.delware.classhub.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.delware.classhub.OtherClasses.ViewAssignmentsArrayAdapter;
import com.delware.classhub.DatabaseModels.AssignmentModel;
import com.delware.classhub.R;
import com.delware.classhub.Singletons.SingletonSelectedClass;
import com.delware.classhub.Singletons.SingletonWeekView;

import java.util.Calendar;
import java.util.List;

/**
 * Overview: This class allows users to view and edit
 * existing assignments associated with a certain class.
 * @author Matt Del Fante
 */
public class ViewAssignmentsActivity extends AppCompatActivity
{
    private final Context m_activityContext = this;
    private Dialog m_assignmentDialog = null;
    private AssignmentModel m_selectedAssignment = null;
    private String m_originalAssignmentName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //overwrite the action bar title
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_action_bar_layout);

        setContentView(R.layout.activity_view_assignments);

        //Set the content of the action bar title
        TextView actionBarTextView = (TextView) findViewById(R.id.defaultActionBarTitle);
        actionBarTextView.setText("Assignments");

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

    /**
     * Initializes the list view's values and sets up the logic for when an item is clicked
     */
    private void initializeListView() {
        ViewAssignmentsArrayAdapter adapter = new ViewAssignmentsArrayAdapter(this);

        ListView assignmentsListView = (ListView) findViewById(R.id.assignmentsListView);
        assignmentsListView.setAdapter(adapter);

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

    /**
     * Displays the dialog box which allows a user to interact with an assignment.
     * @param assignmentName the name of the assignment that was clicked on.
     */
    private void clickedAssignmentHandler(String assignmentName)
    {
        m_originalAssignmentName = assignmentName;
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

    /**
     * Creates the dialog box needed for a user to interact with an assignment.
     */
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
        final Button viewAssignmentCancelButton = (Button) dialog.findViewById(R.id.viewAssingmentCancelButton);
        final Button viewAssignmentDoneButton = (Button) dialog.findViewById(R.id.viewAssingmentDoneButton);

        if (m_selectedAssignment.getIsCompleted())
        {
            editPriorityLevelButton.setEnabled(false);
            completedAssignmentButton.setEnabled(false);
        }

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
                    viewAssignmentDoneButton.setEnabled(true);
                else
                    viewAssignmentDoneButton.setEnabled(false);
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
                        //m_selectedAssignment.setDueDate(calendar.getTime());

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

        //will hold the priority level that the user selects
        final int[] priorityLevel = new int[]{m_selectedAssignment.getPriorityLevel()};

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

                adBuilder.setSingleChoiceItems(choices, priorityLevel[0] - 1, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //index 0 is priority level 1, so add 1 to the index to get the correct priority level
                        priorityLevel[0] = which + 1;
                    }
                })
                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                priorityLevel[0] = m_selectedAssignment.getPriorityLevel();
                            }
                        })
                        .setCancelable(false);

                adBuilder.show();
            }
        });

        //completed assignment button
        completedAssignmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //priority level 4 means the assignment is completed
                m_selectedAssignment.setPriorityLevel(4);
                m_selectedAssignment.setIsCompleted(true);

                //add the assignment to the database
                m_selectedAssignment.save();

                Toast.makeText(getApplicationContext(), "The assignment, " + m_selectedAssignment.getName() +
                        ", was marked as completed.", Toast.LENGTH_SHORT).show();

                //Reset the activity while making the transition seamless
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });

        //delete assignment button
        deleteAssignmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDeletionOfAssignment();
            }
        });

        //additional notes stuff
        editAdditionalNotes.setText(m_selectedAssignment.getAdditionalNotes());

        //cancel button stuff
        viewAssignmentCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        //done button stuff
        viewAssignmentDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toastMsg;
                String assignmentName = editAssignmentNameInput.getText().toString();
                String additionalNotes = editAdditionalNotes.getText().toString();

                //if the user didn't change the assignment name or the assignment name is unique
                if (m_originalAssignmentName.equals(assignmentName) || isUniqueAssignmentName(assignmentName))
                {
                    m_selectedAssignment.setName(assignmentName);
                    m_selectedAssignment.setAdditionalNotes(additionalNotes);
                    m_selectedAssignment.setDueDate(calendar.getTime());

                    if (!m_selectedAssignment.getIsCompleted())
                        m_selectedAssignment.setPriorityLevel(priorityLevel[0]);

                    //update the assignment in the database
                    m_selectedAssignment.save();

                    toastMsg = "The assignment: " + assignmentName + " was successfully updated.";

                    //Reset the activity while making the transition seamless
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(getIntent());
                    overridePendingTransition(0, 0);
                }
                else
                    toastMsg = "This class already has an assignment with the name: " + assignmentName + ".";

                Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT).show();
            }
        });

        m_assignmentDialog = dialog;
    }

    /**
     * Creates and displays an alert dialog that asks the user if he or she is sure her or she
     * wants to delete the assignment. On confirmation, the assignment is deleted from the database
     * and on cancellation the alert dialog disappears.
     */
    private void confirmDeletionOfAssignment()
    {
        final String name = m_selectedAssignment.getName();

        //simple alert dialog for confirmation
        new AlertDialog.Builder(this)
                .setTitle("Wait...")
                .setMessage("Are you sure you want to delete the assignment: " + name + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //delete the assignment form the database
                        m_selectedAssignment.delete();

                        //Reset the activity while making the transition seamless
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);

                        Toast.makeText(getApplicationContext(), "The assignment, " + name + ", was deleted.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    /**
     * Determines weather or not the assignment has a unique name or not.
     * @param name The name of the assignment.
     * @return True if the name is unique, else false.
     */
    private boolean isUniqueAssignmentName(String name)
    {
        for (AssignmentModel model : SingletonSelectedClass.getInstance().getSelectedClass().getAssignments())
        {
            if (model.getName().equals(name))
                return false;
        }
        return true;
    }
}
