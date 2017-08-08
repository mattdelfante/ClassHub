package com.delware.classhub;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.RectF;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    //needed for the Week calendar
    private WeekView m_weekView;
    private List<WeekViewEvent> m_allAssignments;
    private WeekView.EventClickListener m_eventClickListener;
    private WeekView.EmptyViewClickListener m_emptyViewClickListener;
    private MonthLoader.MonthChangeListener m_monthChangeListener;

    //needed for when pressing the add class button
    private Dialog m_dialogForAddClassButton;
    private Dialog m_alertDialogForLongPressingAClass;

    private LongClickedClass m_longClickedClass;
    private ArrayAdapter<String> m_classesListViewAdapter;

    //pull the classes from the database and put the into a string array
    private ArrayList<String> m_classes;

    private final Context m_activityContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        m_longClickedClass = new LongClickedClass();
        m_classes = new ArrayList<String>();

        //pull these classes information from the database
        m_classes.add("Class1");
        m_classes.add("Class2");
        m_classes.add("Class3");
        m_classes.add("Class4");

        //I am going to overwrite the action bar for this activity
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activity_home_action_bar);

        setContentView(R.layout.activity_home);

        initializeWeekViewActions();
        createAddClassOnClickDialog();
        registerContextMenuForAddClassButton();
        setClassListViewContent();
        createDialogBoxForLongPressingAClass();
    }

    private void initializeWeekViewActions() {

        m_allAssignments = new ArrayList<WeekViewEvent>();

        //action when clicking on an existing homework assignment
        m_eventClickListener = new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {
                Toast.makeText(getApplicationContext(), "Clicked Hw problem", Toast.LENGTH_SHORT).show();
            }
        };

        //action when clicking empty date in the calendar
        m_emptyViewClickListener = new WeekView.EmptyViewClickListener() {
            @Override
            public void onEmptyViewClicked(Calendar time) {

//            //ADDS AN EVENT TO JULY 25th WHEN CLICKING ON AN EMPTY SPOT IN THE CALENDAR
//            //THIS LOGIC CAN BE USED TO ADD CALENDAR ASSIGNMENTS THROUGHOUT THE APP
//            Calendar startTime = Calendar.getInstance();
//            startTime.set(Calendar.HOUR_OF_DAY, 3);
//            startTime.set(Calendar.MINUTE, 0);
//            startTime.set(Calendar.MONTH, 6);
//            startTime.set(Calendar.YEAR, 2017);
//            Calendar endTime = (Calendar) startTime.clone();
//            endTime.add(Calendar.HOUR, 1);
//            endTime.set(Calendar.MONTH, 6);
//
//            WeekViewEvent event = new WeekViewEvent(1, "tempHwAssignment", startTime, endTime);
//            event.setColor(getResources().getColor(R.color.DeepPink));
//
//            m_allAssignments.add(event);
//
//            //Refreshses event
//            m_weekView.notifyDatasetChanged();

                Toast.makeText(getApplicationContext(), "Clicked Nothing", Toast.LENGTH_SHORT).show();
            }
        };

        //action that populates the calendar with homework assignments
        m_monthChangeListener = new MonthLoader.MonthChangeListener() {
            @Override
            public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {

                List<WeekViewEvent> returnVal = new ArrayList<WeekViewEvent>();
                java.util.Date date = new Date();

                //only show the current year's information
                for (WeekViewEvent event : m_allAssignments)
                {
                    //WeekView have January start on Month 1 where WeekView has
                    //January start on 0. That is why there is the + 1 for the java date
                    if (newMonth == date.getMonth() + 1)
                    {
                        returnVal.add(event);
                    }
                }
                return returnVal;
            }
        };

        // Get a reference for the week view in the layout.
        m_weekView = (WeekView) findViewById(R.id.weekView);

        //set the events that were defined above
        m_weekView.setOnEventClickListener(m_eventClickListener);
        m_weekView.setMonthChangeListener(m_monthChangeListener);
        m_weekView.setEmptyViewClickListener(m_emptyViewClickListener);
    }

    private void createAddClassOnClickDialog()
    {
        final Dialog dialog = new Dialog(m_activityContext);
        dialog.setContentView(R.layout.activity_home_add_class_dialog);

        final EditText renameClassEditText = (EditText) dialog.findViewById(R.id.addClassInput);
        final Button doneButton = (Button) dialog.findViewById(R.id.addClassDoneButton);
        final Button cancelButton = (Button) dialog.findViewById(R.id.addClassCancelButton);

        //done button is only available to show when there is information inputted into
        //the edit text associated with the dialog box
        renameClassEditText.addTextChangedListener(new TextWatcher() {
            //start off as a disabled done button
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

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
            public void afterTextChanged(Editable s) {

            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String className = renameClassEditText.getText().toString();

                m_classes.add(className);
                m_classesListViewAdapter.notifyDataSetChanged();

                Toast.makeText(getApplicationContext(), "Added: " + className, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                renameClassEditText.setText("");
            }
        });

        //dismiss dialog when cancel is pressed
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                renameClassEditText.setText("");
            }
        });

        //create the dialog
        m_dialogForAddClassButton = dialog;
    }

    private void setClassListViewContent()
    {
        m_classesListViewAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, m_classes);
        ListView classesListView = (ListView) findViewById(R.id.classesListView);
        classesListView.setAdapter(m_classesListViewAdapter);

        //set the click stuff
        classesListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //gets the value of the
                        String className = String.valueOf(parent.getItemAtPosition(position));
                        Toast.makeText(getApplicationContext(),className, Toast.LENGTH_SHORT).show();
                    }
                }
        );

        //long press button
        classesListView.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        displayLongPressedClassDialogBox(view);

                        //remember the last class that was long clicked
                        m_longClickedClass.parent = parent;
                        m_longClickedClass.view = view;
                        m_longClickedClass.position = position;
                        m_longClickedClass.id = id;

                        //idk what to return
                        return true;
                    }
                }
        );

    }

    private void registerContextMenuForAddClassButton()
    {
        //below is information for the context menu on long presses
        //access the button
        Button addClassButton = (Button) findViewById(R.id.addClassButton);

        //register for context menu
        this.registerForContextMenu(addClassButton);
    }

    //need this to create context menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        //if you long pressed on the correct thing
        if (v.getId() == R.id.addClassButton) {
            //then populate the context menu of that thing with the correct info
            this.getMenuInflater().inflate(R.menu.add_class_context_menu, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    //here is how you do something once you selected the an item in a context menu
    //Thus, if archive class happens, do the logic to store in database and make thing go away.
    //If delete class happens, delete everything
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();

        //gets the layout of the main activity
        PercentRelativeLayout layout = (PercentRelativeLayout) findViewById(R.id.homeActivityMainPercentRelativeLayout);

        switch (id)
        {
            //just changes background color right now, will change so it will do the functionality
            //of the archive class and delete class
            case R.id.retrieveArchivedClasses:
                Toast.makeText(getApplicationContext(), "Retrieving Archived Classes..." , Toast.LENGTH_SHORT).show();
                layout.setBackgroundColor(Color.GREEN);
                break;
            case R.id.addClassContextMenuCancelButton:
                closeContextMenu();
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void createDialogBoxForLongPressingAClass()
    {
        final Dialog dialog = new Dialog(m_activityContext);
        dialog.setContentView(R.layout.activity_home_class_dialog);

        //configure actions of the dialog box
        final EditText inputEditText = (EditText) dialog.findViewById(R.id.renameClassInput);
        final Button doneButton = (Button) dialog.findViewById(R.id.classDoneButton);
        final Button cancelButton = (Button) dialog.findViewById(R.id.classCancelButton);
        final Button deleteClassButton = (Button) dialog.findViewById(R.id.deleteClassButton);
        final Button archiveClassButton = (Button) dialog.findViewById(R.id.archiveClassButton);

        //done button is only available to show when there is information inputted into
        //the edit text associated with the dialog box
        inputEditText.addTextChangedListener(new TextWatcher() {
            //start off as a disabled done button
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

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
            public void afterTextChanged(Editable s) {

            }
        });

        deleteClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                createAlertDialogForConfirmation("Are you sure you want to delete the class: " + m_classes.get(m_longClickedClass.position) + "?");
            }
        });

        archiveClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete the class from the listView

                String className = m_classes.get(m_longClickedClass.position);
                m_classes.remove(m_longClickedClass.position);
                m_classesListViewAdapter.notifyDataSetChanged();

                //send the message
                Toast.makeText(getApplicationContext(), className + " was archived!" , Toast.LENGTH_SHORT).show();

                //make the dialog box disappear
                dialog.dismiss();
            }
        });

        //rename the list view item when done button is pressed
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this particular class was long clicked, update with the value inputted into the edit text
                m_classes.set(m_longClickedClass.position, inputEditText.getText().toString());

                m_classesListViewAdapter.notifyDataSetChanged();

                dialog.dismiss();

                inputEditText.setText("");
            }
        });

        //dismiss dialog when cancel is pressed
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                inputEditText.setText("");
            }
        });

        m_alertDialogForLongPressingAClass  = dialog;
    }

    public void createAlertDialogForConfirmation(String message)
    {
        //simple alert dialog for confimation
        new AlertDialog.Builder(this)
                .setTitle("Wait...")
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //DELETE THE CLASS FROM THE APP
                        //remove it from the list view
                        m_classes.remove(m_longClickedClass.position);
                        m_classesListViewAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //bring up the dialog box again
                        displayLongPressedClassDialogBox(null);
                    }
                }).show();
    }

    //shows the created dialog box when a certain button is clicked
    public void displayLongPressedClassDialogBox(View v)
    {
        m_alertDialogForLongPressingAClass.show();
    }

    //shows the created dialog box when a certain button is clicked
    public void displayAddClassDialogBox(View v)
    {
        m_dialogForAddClassButton.show();
    }
}
