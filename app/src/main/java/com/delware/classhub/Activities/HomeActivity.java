package com.delware.classhub.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
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

import com.activeandroid.ActiveAndroid;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.delware.classhub.DatabaseObjs.AssignmentModel;
import com.delware.classhub.DatabaseObjs.ClassModel;
import com.delware.classhub.Pojos.LongClickedClass;
import com.delware.classhub.R;
import com.delware.classhub.Singletons.SingletonSelectedClass;
import com.delware.classhub.Singletons.SingletonWeekView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity
{
    //pop up dialog box when you long press the add class button
    private Dialog m_dialogForAddClassButton;

    //pop up alert dialog box when you long press on a class in the list view
    private Dialog m_alertDialogForLongPressingAClass;

    //holds the meta data about a class when you long press a class
    private LongClickedClass m_longClickedClass;

    //allows updating the list view that holds all the classes
    private ArrayAdapter<String> m_classesListViewAdapter;

    //holds all of the non archived classes names
    private ArrayList<String> m_classes;

    //the context of the current activity
    private final Context m_activityContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //deletes the database, do this if you need to reset the schema or anything
        //this.deleteDatabase("ClassHubDB");

        //initialize the database
        ActiveAndroid.initialize(this);

        //Overwrite the action bar of this activity to the custom one I created
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.activity_home_action_bar);

        setContentView(R.layout.activity_home);

        m_longClickedClass = new LongClickedClass();
        m_classes = new ArrayList<String>();

        //makes it so the list view has all the non archived classes from the database
        addClassesFromDb(ClassModel.getNonArchivedClasses());

        //defines what happens on the week view calendar
        initializeWeekViewActions();

        //defines what happens when you click the add class button
        createAddClassOnClickDialog();

        //allows the context menu to pop up when long pressing the add class button
        registerContextMenuForAddClassButton();

        //initializes the classes list view
        initializeClassListView();

        //makes the dialog box for long pressing a class
        createDialogBoxForLongPressingAClass();
    }

    /**
     * Adds a list of classes to the list view
     * @param classes the list of classes to add to the list view
     */
    private void addClassesFromDb(List<ClassModel> classes)
    {
        for (ClassModel model : classes)
            m_classes.add(model.name);
    }

    /**
     * Defines what happens when pressing things on the WeekView calendar.
     */
    private void initializeWeekViewActions() {
        //action when clicking on an existing homework assignment
        WeekView.EventClickListener eventClickListener = new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {
                Toast.makeText(getApplicationContext(), "Clicked Hw problem", Toast.LENGTH_SHORT).show();
            }
        };

        //action when clicking empty date in the calendar
        WeekView.EmptyViewClickListener emptyViewClickListener = new WeekView.EmptyViewClickListener() {
            @Override
            public void onEmptyViewClicked(Calendar time) {

            }
        };

        //action that populates the calendar with homework assignments
        MonthLoader.MonthChangeListener monthChangeListener = new MonthLoader.MonthChangeListener() {
            @Override
            public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {

                List<WeekViewEvent> returnVal = new ArrayList<WeekViewEvent>();
                List<ClassModel> nonArchivedClasses = ClassModel.getNonArchivedClasses();
                List<AssignmentModel> classAssignments = null;
                java.util.Date date = new Date();
                int numAssignments = 0;

                for (ClassModel currClass : nonArchivedClasses)
                {
                    classAssignments = currClass.getAssignments();

                    for (AssignmentModel asgnmt : classAssignments)
                    {
                        if (newMonth == date.getMonth() + 1)
                        {
                            numAssignments++;

                            returnVal.add(createWeekViewEvent(numAssignments, asgnmt));
                        }
                    }
                }

                return returnVal;
            }
        };

        // Get a reference for the week view in the layout.
        WeekView weekView = (WeekView) findViewById(R.id.weekView);

        //store the weekview calendar into the singleton
        if (SingletonWeekView.getInstance().getWeekView() == null)
            SingletonWeekView.getInstance().setWeekView(weekView);

        //set the events that were defined above
        weekView.setOnEventClickListener(eventClickListener);
        weekView.setMonthChangeListener(monthChangeListener);
        weekView.setEmptyViewClickListener(emptyViewClickListener);
    }

    /**
     * Creates a weekview event that corresponds to a class's assignment
     * @param eventId the id of the weekview event
     * @param asgnmt the assignment that the event is being created for
     * @return the weekview event for an assignment
     */
    private WeekViewEvent createWeekViewEvent(int eventId, AssignmentModel asgnmt)
    {
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(asgnmt.dueDate);

        Calendar endTime = Calendar.getInstance();
        endTime.setTime(asgnmt.dueDate);
        endTime.add(Calendar.HOUR, 1);

        WeekViewEvent assignment = new WeekViewEvent(eventId, asgnmt.name, startTime, endTime);

        switch (asgnmt.priorityLevel)
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

        return assignment;
    }

    /**
     * Creates the add class button dialog box.
     */
    private void createAddClassOnClickDialog()
    {
        final Dialog dialog = new Dialog(m_activityContext);
        dialog.setContentView(R.layout.activity_home_add_class_dialog);

        final EditText addClassInput = (EditText) dialog.findViewById(R.id.addClassInput);
        final Button doneButton = (Button) dialog.findViewById(R.id.addClassDoneButton);
        final Button cancelButton = (Button) dialog.findViewById(R.id.addClassCancelButton);

        //done button is only available to show when there is information inputted into
        //the edit text associated with the dialog box
        addClassInput.addTextChangedListener(new TextWatcher() {
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
                ClassModel newClass = new ClassModel();
                String className = addClassInput.getText().toString();

                //update the UI with new class
                m_classes.add(className);
                m_classesListViewAdapter.notifyDataSetChanged();

                //save the new class to the database
                newClass.name = className;
                newClass.isArchived = false;
                newClass.save();

                Toast.makeText(getApplicationContext(), "Added: " + className, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                addClassInput.setText("");
            }
        });

        //dismiss dialog when cancel is pressed
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                addClassInput.setText("");
            }
        });

        //create the dialog
        m_dialogForAddClassButton = dialog;
    }

    /**
     * Initializes the actions of the Classes list view in the app
     */
    private void initializeClassListView()
    {
        m_classesListViewAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, m_classes);
        ListView classesListView = (ListView) findViewById(R.id.classesListView);
        classesListView.setAdapter(m_classesListViewAdapter);

        //set the click stuff
        classesListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //gets the class name from the list view and store it in the singleton
                        String className = String.valueOf(parent.getItemAtPosition(position));

                        //set the singleton so that this class is now the selected class
                        SingletonSelectedClass.getInstance().setSelectedClass(ClassModel.getClass(className));

                        //Go to the ClassActivity
                        Intent intent = new Intent(HomeActivity.this, ClassActivity.class);
                        startActivity(intent);
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

    /**
     * Registers the add class button for a context menu
     */
    private void registerContextMenuForAddClassButton()
    {
        //below is information for the context menu on long presses
        //access the button
        Button addClassButton = (Button) findViewById(R.id.addClassButton);

        //register for context menu
        this.registerForContextMenu(addClassButton);
    }

    /**
     * creates the context menu needed for the add class button
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        //if you long pressed on the correct thing
        if (v.getId() == R.id.addClassButton) {
            //then populate the context menu of that thing with the correct info
            this.getMenuInflater().inflate(R.menu.add_class_context_menu, menu);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    /**
     * Defines the actions that are taken when an item from the add class
     * context menu is selected.
     * @param item the selected context menu item
     * @return true when if the process succeeded
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.retrieveArchivedClasses:
                //get the archived classes and update weekview with it's assignments
                getArchivedClasses();
                SingletonWeekView.getInstance().getWeekView().notifyDatasetChanged();
                Toast.makeText(getApplicationContext(), "Archived classes were retrieved" , Toast.LENGTH_SHORT).show();
                break;
            case R.id.addClassContextMenuCancelButton:
                closeContextMenu();
                break;
        }

        return super.onContextItemSelected(item);
    }

    /**
     * Gets the archived classes from the database, marks them as
     * non archived and then adds them to the classes list view.
     */
    private void getArchivedClasses() {
        List<ClassModel> archivedClasses = ClassModel.getArchivedClasses();

        for (ClassModel _class : archivedClasses)
        {
            _class.isArchived = false;
            _class.save();
            m_classes.add(_class.name);
        }

        m_classesListViewAdapter.notifyDataSetChanged();
    }

    /**
     * Creates the dialog box for long pressing a class and intializes
     * it's actions.
     */
    private void createDialogBoxForLongPressingAClass()
    {
        final Dialog dialog = new Dialog(m_activityContext);
        dialog.setContentView(R.layout.activity_home_class_dialog);

        //all the components in the dialog box
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

        deleteClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                createAlertDialogForConfirmation("Are you sure you want to delete the class: " + m_classes.get(m_longClickedClass.position) + "?", m_classes.get(m_longClickedClass.position));
            }
        });

        archiveClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete the class from the listView
                String className = m_classes.get(m_longClickedClass.position);
                m_classes.remove(m_longClickedClass.position);
                m_classesListViewAdapter.notifyDataSetChanged();

                //archive the class in the databse
                ClassModel.makeClassArchived(className);
                //update the weekview calendar
                SingletonWeekView.getInstance().getWeekView().notifyDatasetChanged();

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
                String oldClassName = m_classes.get(m_longClickedClass.position);
                String newClassName = inputEditText.getText().toString();

                //this particular class was long clicked, update with the value inputted into the edit text
                m_classes.set(m_longClickedClass.position, newClassName);
                m_classesListViewAdapter.notifyDataSetChanged();

                //update the database record so the class has a new name
                ClassModel.renameClass(oldClassName, newClassName);

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

    /**
     * Creates the alert dialog box for confirming to delete a class
     * @param message the message displayed in the alert dialog confirmation
     * @param className the name of the class that is going to be deleted
     */
    public void createAlertDialogForConfirmation(String message, final String className)
    {
        //simple alert dialog for confimation
        new AlertDialog.Builder(this)
                .setTitle("Wait...")
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //remove the class from the list view
                        m_classes.remove(m_longClickedClass.position);
                        m_classesListViewAdapter.notifyDataSetChanged();

                        //delete the class from the app
                        ClassModel.deleteClass(className);
                        SingletonWeekView.getInstance().getWeekView().notifyDatasetChanged();
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

    /**
     * Displays the dialog box when long pressing a class
     * @param v the view that the dialog box is displayed
     */
    public void displayLongPressedClassDialogBox(View v)
    {
        m_alertDialogForLongPressingAClass.show();
    }

    /**
     * Displays the dialog box when long pressing the add class button
     * @param v the view that the dialog box is displayed
     */
    public void displayAddClassDialogBox(View v)
    {
        m_dialogForAddClassButton.show();
    }
}