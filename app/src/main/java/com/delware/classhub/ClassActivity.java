package com.delware.classhub;

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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alamkanak.weekview.WeekViewEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

        final EditText asgnmtNameInput = (EditText) dialog.findViewById(R.id.assignmentNameInput);
        final Button dueDateButton = (Button) dialog.findViewById(R.id.addDueDateButton);
        final Button priorityLevelButton = (Button) dialog.findViewById(R.id.priorityLevelButton);
        final EditText notesInput = (EditText) dialog.findViewById(R.id.additionalNotesField);
        final Button cancelButton = (Button) dialog.findViewById(R.id.classCancelButton);
        final Button doneButton = (Button) dialog.findViewById(R.id.classDoneButton);

        final Assignment newAssignment = new Assignment();

        //assignment name input stuff
        asgnmtNameInput.addTextChangedListener(new TextWatcher() {
            //start off as a disabled done button
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                String input = s.toString();

                //not all whitespace or empty
                if (input.trim().length() > 0)
                {
                    newAssignment.setName(input);

                    if (isValidAssignment(newAssignment))
                        doneButton.setEnabled(true);
                }
                else
                {
                    newAssignment.setName(null);
                    doneButton.setEnabled(false);
                }
            }
        });
        //assignment name input stuff

        //date stuff
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog tpd = new TimePickerDialog(m_activityContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        myCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        myCalendar.set(Calendar.MINUTE, selectedMinute);

                        //Now the assignments date is properly set
                        newAssignment.setDueDate(myCalendar);

                        if (isValidAssignment(newAssignment))
                            doneButton.setEnabled(true);

                    }
                }, 0, 0, false);
                tpd.show();
            }

        };

        dueDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(ClassActivity.this, date,
                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });
        //date stuff

        //priority level stuff
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
                        newAssignment.setPriorityLevel(which + 1);
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
                        newAssignment.setPriorityLevel(1);
                    }
                });

                adBuilder.show();
            }
        });
        //priority level stuff

        //notes stuff
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
                    newAssignment.setNotes(input);
                else
                    newAssignment.setNotes("");
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
                Calendar endTime = (Calendar) newAssignment.getDueDate().clone();
                endTime.add(Calendar.HOUR, 1);

                //add the assignment to the calendar
                WeekViewEvent assignment = new WeekViewEvent(SingletonWeekView.getInstance().getEvents().size() + 1,
                        newAssignment.getName(), newAssignment.getDueDate(), endTime);

                //assignment shows up as different colors dependent on
                //priority level
                switch (newAssignment.getPriorityLevel())
                {
                    case 1:
                        assignment.setColor(Color.BLUE);
                        break;
                    case 2:
                        assignment.setColor(Color.YELLOW);
                        break;
                    case 3:
                        assignment.setColor(Color.RED);
                        break;
                    default:
                        assignment.setColor(Color.BLUE);
                        break;
                }

                SingletonWeekView.getInstance().getEvents().add(assignment);
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

    private Boolean isValidAssignment(Assignment a)
    {
        return a.getName() != null && a.getDueDate() != null;
    }

    public void showAddAssignmentDialog(View v) {m_addAssignmentDialog.show();}
}
