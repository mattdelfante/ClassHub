package com.delware.classhub.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.delware.classhub.DatabaseObjs.AssignmentModel;
import com.delware.classhub.R;
import com.delware.classhub.Singletons.SingletonSelectedClass;
import com.delware.classhub.Singletons.SingletonWeekView;

import java.util.Calendar;

public class ClassActivity extends AppCompatActivity {

    private Context m_activityContext = this;
    private Dialog m_addAssignmentDialog;

    /**
     * Creates everything needed for the ClassActivity
     * @param savedInstanceState the state of the application
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //overwrite the action bar for this activity with a different one
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activity_class_action_bar);

        setContentView(R.layout.activity_class);

        //get the name of the class at make that the title of the page
        TextView actionBarTextView = (TextView) findViewById(R.id.classActivityActionBarTitle);
        actionBarTextView.setText(SingletonSelectedClass.getInstance().getSelectedClass().name);

        createAddAssignmentOnClickDialog();
    }

    /**
     * Creates the logic for what happens when clicking the Add Assignment button.
     */
    private void createAddAssignmentOnClickDialog()
    {
        final Dialog dialog = new Dialog(m_activityContext);
        dialog.setContentView(R.layout.activity_class_add_asgnmt_dialog);

        final EditText asgnmtNameInput = (EditText) dialog.findViewById(R.id.assignmentNameInput);
        final Button dueDateButton = (Button) dialog.findViewById(R.id.addDueDateButton);
        final Button priorityLevelButton = (Button) dialog.findViewById(R.id.priorityLevelButton);
        final EditText notesInput = (EditText) dialog.findViewById(R.id.additionalNotesField);
        final Button cancelButton = (Button) dialog.findViewById(R.id.classCancelButton);
        final Button doneButton = (Button) dialog.findViewById(R.id.classDoneButton);

        final AssignmentModel newAssignment = new AssignmentModel();
        newAssignment.associatedClass = SingletonSelectedClass.getInstance().getSelectedClass();

        //assignment name input logic
        asgnmtNameInput.addTextChangedListener(new TextWatcher() {
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
                    newAssignment.name = input;

                    if (isValidAssignment(newAssignment))
                        doneButton.setEnabled(true);
                }
                else
                {
                    newAssignment.name = null;
                    doneButton.setEnabled(false);
                }
            }
        });

        //Due date logic
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                //set the date that the assginment is due
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                //after the date is selected, allow the user to pick the time that the assignment is due
                TimePickerDialog tpd = new TimePickerDialog(m_activityContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        myCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        myCalendar.set(Calendar.MINUTE, selectedMinute);

                        //Now the assignments date is properly set
                        newAssignment.dueDate = myCalendar.getTime();

                        if (isValidAssignment(newAssignment))
                            doneButton.setEnabled(true);

                    }
                }, 0, 0, false);
                tpd.show();
            }

        };

        //display the calendar date picker when clicking the due date button
        dueDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(ClassActivity.this, date,
                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });

        //priority level logic
        priorityLevelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                CharSequence[] choices = new CharSequence[3];
                choices[0] = "Priority Level One";
                choices[1] = "Priority Level Two";
                choices[2] = "Priority Level Three";

                AlertDialog.Builder adBuilder = new AlertDialog.Builder(m_activityContext);
                adBuilder.setTitle("Select A Priority Level");

                adBuilder.setSingleChoiceItems(choices, 0, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //index 0 is priority level 1, so add 1 to the index to get the correct priority level
                        newAssignment.priorityLevel = which + 1;
                    }
                })
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //goes back to Priority Level One
                        newAssignment.priorityLevel = 1;
                    }
                });

                adBuilder.show();
            }
        });
        //priority level stuff

        //notes logic
        //if click on the edit text and have the default text in it, clear it out
        notesInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                {
                    if (notesInput.getText().toString().equals("Enter additional notes here..."))
                        notesInput.setText("");;
                }
            }
        });

        notesInput.addTextChangedListener(new TextWatcher() {
            //start off as a disabled done button
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();
                //not all whitespace or empty
                if (input.trim().length() > 0)
                    newAssignment.additionalNotes = input;
                else
                    newAssignment.additionalNotes = null;
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        //notes stuff

        //cancel button stuff
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                notesInput.setText("Enter additional notes here...");
                asgnmtNameInput.setText("");
                asgnmtNameInput.setFocusable(true);
            }
        });
        //cancel button stuff

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ADD TO THE VIEW ASSIGNMENTS PAGE

                //add the assignment to the database
                newAssignment.save();
                SingletonWeekView.getInstance().getWeekView().notifyDatasetChanged();

                //Reset the activity while making the transition seamless
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }
        });

        //create the dialog
        m_addAssignmentDialog = dialog;
    }

    /**
     * Determines weather or not the assignment is a valid assignment. An assignment is
     * valid if it has a name and a due date.
     * @param a The current assignment.
     * @return True if a valid assignment false if not.
     */
    private Boolean isValidAssignment(AssignmentModel a)
    {
        return a.name != null && a.dueDate != null;
    }

    /**
     * Displays the Add Assignment dialog
     * @param v the current view
     */
    public void showAddAssignmentDialog(View v)
    {
        m_addAssignmentDialog.show();
    }
}
