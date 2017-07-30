package com.delware.classhub;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.RectF;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
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
    private EditText m_inputForAddClassButtonDialogMenu;
    private AlertDialog m_alertDialogForAddClassButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //I am going to overwrite the action bar for this activity
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activity_home_action_bar);

        setContentView(R.layout.activity_home);

        initializeWeekViewActions();

        setClassListViewContent();
        createContextMenu();
        createInputDialogBox();
    }

    private void setClassListViewContent()
    {
        //pull the classes from the database and put the into a string array
        String[] classes = {"Class1", "Class2", "Class3", "Class4"};

        ListAdapter listViewAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, classes);
        ListView classesListView = (ListView) findViewById(R.id.classesListView);
        classesListView.setAdapter(listViewAdapter);

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

    /***************************Context Menu For Add Class Button *****************************************/
    private void createContextMenu()
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
        ConstraintLayout layout = (ConstraintLayout) findViewById(R.id.homeActivityMainConstraintView);

        switch (id)
        {
            //just changes background color right now, will change so it will do the functionality
            //of the archive class and delete class
            case R.id.archiveClass:
                layout.setBackgroundColor(Color.GREEN);
                break;
            case R.id.deleteClass:
                layout.setBackgroundColor(Color.RED);
                break;
        }
        return super.onContextItemSelected(item);
    }

    /***************************Dialog Box Stuff*****************************************/
    private void createInputDialogBox()
    {
        //below is information for the change name menu
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        //title of the dialog
        alertDialogBuilder.setTitle("Please Enter the Class Name");
        //Message below the title
        //alertDialogBuilder.setMessage("Please Enter the Class Name");

        m_inputForAddClassButtonDialogMenu = new EditText(this);
        alertDialogBuilder.setView(m_inputForAddClassButtonDialogMenu);

        //set done button
        alertDialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String className = m_inputForAddClassButtonDialogMenu.getText().toString();
                Toast.makeText(getApplicationContext(), "Added Class: " + className, Toast.LENGTH_SHORT).show();
                m_inputForAddClassButtonDialogMenu.setText("");
            }
        });

        //set cancel button
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ///dialog.dismiss();
                Toast.makeText(getApplicationContext(), "Canceled", Toast.LENGTH_SHORT).show();
                m_inputForAddClassButtonDialogMenu.setText("");
            }
        });

        //create the dialog
        m_alertDialogForAddClassButton = alertDialogBuilder.create();
    }

    //shows the created dialog box when a certain button is clicked
    public void displayAddClassDialogBox(View v)
    {
        m_alertDialogForAddClassButton.show();
    }
}
