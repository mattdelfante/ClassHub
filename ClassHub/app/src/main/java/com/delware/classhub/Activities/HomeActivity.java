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
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.delware.classhub.DatabaseModels.AssignmentModel;
import com.delware.classhub.DatabaseModels.ClassModel;
import com.delware.classhub.R;
import com.delware.classhub.Singletons.SingletonSelectedClass;
import com.delware.classhub.Singletons.SingletonWeekView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Overview: This class represents the startup activity within
 * the ClassHub application. The class allows ClassModel's to be
 * added to/deleted from the application and for existing ClassModel's
 * to modified.
 * @author Matt Del Fante
 */
public class HomeActivity extends AppCompatActivity
{
    //pop up dialog box when you long press the add class button
    private Dialog m_dialogForAddClassButton = null;

    //pop up alert dialog box when you long press on a class in the list view
    private Dialog m_alertDialogForLongPressingAClass = null;

    //allows updating the list view that holds all the classes
    private ArrayAdapter<String> m_classesListViewAdapter;

    //holds all of the non archived classes names
    private ArrayList<String> m_classes;

    //the context of the current activity
    private final Context m_activityContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize the database
        ActiveAndroid.initialize(this);

        //Overwrite the action bar of this activity to the custom one I created
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.default_action_bar_layout);

        setContentView(R.layout.activity_home);

        //get the name of the class at make that the title of the page
        TextView actionBarTextView = (TextView) findViewById(R.id.defaultActionBarTitle);
        actionBarTextView.setText("Class Hub");

        m_classes = new ArrayList<>();

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
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (m_dialogForAddClassButton != null)
            m_dialogForAddClassButton.dismiss();

        if (m_alertDialogForLongPressingAClass != null)
            m_alertDialogForLongPressingAClass.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (m_dialogForAddClassButton != null)
            m_dialogForAddClassButton.dismiss();

        if (m_alertDialogForLongPressingAClass != null)
            m_alertDialogForLongPressingAClass.dismiss();
    }

    /**
     * Adds a list of classes to the list view
     * @param classes the list of classes to add to the list view
     */
    private void addClassesFromDb(List<ClassModel> classes)
    {
        for (ClassModel model : classes)
            m_classes.add(model.getName());
    }

    /**
     * Defines what happens when pressing things on the WeekView calendar.
     */
    private void initializeWeekViewActions() {
        //action when clicking on an existing homework assignment
        WeekView.EventClickListener eventClickListener = new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {
                //get the assignment that was tapped
                AssignmentModel assignmentModel = AssignmentModel.getAssignment(event.getId());

                //set it as the selected class
                SingletonSelectedClass.getInstance().setSelectedClass(assignmentModel.getAssociatedClass());

                //Go to the ViewAssignmentsActivity
                Intent intent = new Intent(HomeActivity.this, ViewAssignmentsActivity.class);
                startActivity(intent);
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
            public List<WeekViewEvent> onMonthChange(int newYear, int newMonth)
            {
                List<WeekViewEvent> returnVal = new ArrayList<>();
                List<AssignmentModel> allAssignments = AssignmentModel.getAllAssignments();
                java.util.Date date = new Date();

                for (AssignmentModel a : allAssignments)
                {
                    ClassModel associatedClass = a.getAssociatedClass();

                    //if the class the assignment is associated with isn't archived and it is the
                    //correct month. Add it as an assignment in the assignment calendar
                    if (!associatedClass.isArchived() && newMonth == date.getMonth() + 1)
                        returnVal.add(createWeekViewEvent(a.getId(), a));

                }

                return returnVal;
            }
        };

        // Get a reference for the week view in the layout.
        WeekView weekView = (WeekView) findViewById(R.id.weekView);

        //store the week view calendar into the singleton
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
    private WeekViewEvent createWeekViewEvent(long eventId, AssignmentModel asgnmt)
    {
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(asgnmt.getDueDate());

        Calendar endTime = Calendar.getInstance();
        endTime.setTime(asgnmt.getDueDate());
        endTime.add(Calendar.HOUR, 1);

        WeekViewEvent assignment = new WeekViewEvent(eventId, asgnmt.getName(), startTime, endTime);

        switch (asgnmt.getPriorityLevel())
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
            case 4:
                assignment.setColor(Color.GREEN);
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
                String toastMsg;
                String className = addClassInput.getText().toString();

                if (isClassNameUnique(className))
                {
                    ClassModel newClass = new ClassModel();

                    //update the UI with new class
                    m_classes.add(className);
                    m_classesListViewAdapter.notifyDataSetChanged();

                    //save the new class to the database
                    newClass.setName(className);
                    newClass.setIsArchived(false);
                    newClass.save();

                    toastMsg = "The class: " + className + " was successfully added";

                    dialog.dismiss();
                    addClassInput.setText("");
                }
                else
                    toastMsg = "Error: The name of every class must be unique.";

                Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT).show();
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
        m_classesListViewAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, m_classes);
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
                        //makes the dialog box for long pressing a class
                        createDialogBoxForLongPressingAClass(position);
                        m_alertDialogForLongPressingAClass.show();
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

        for (ClassModel c : archivedClasses)
        {
            c.setIsArchived(false);
            c.save();
            m_classes.add(c.getName());
        }
        m_classesListViewAdapter.notifyDataSetChanged();
    }

    /**
     * Creates the dialog box for long pressing a class and initializes
     * it's actions.
     * @param index represents the index of the class that was long pressed
     */
    private void createDialogBoxForLongPressingAClass(final int index)
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
                String className = m_classes.get(index);
                createAlertDialogForConfirmation("Are you sure you want to delete the class: "
                        + className + "?", className, index);
            }
        });

        archiveClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete the class from the listView
                String className = m_classes.get(index);
                m_classes.remove(index);
                m_classesListViewAdapter.notifyDataSetChanged();


                //archive the class in the database
                ClassModel.makeClassArchived(className);
                //update the week view calendar
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
                String oldClassName = m_classes.get(index);
                String newClassName = inputEditText.getText().toString();

                //if the user typed in the same name as the old class name or the class name is unique
                if (oldClassName.equals(newClassName) || isClassNameUnique(newClassName))
                {
                    //this particular class was long clicked, update with the value inputted into the edit text
                    m_classes.set(index, newClassName);
                    m_classesListViewAdapter.notifyDataSetChanged();

                    //update the database record so the class has a new name
                    ClassModel.renameClass(oldClassName, newClassName);

                    dialog.dismiss();
                    inputEditText.setText("");
                }
                else
                    Toast.makeText(getApplicationContext(), "An existing class already has the name: " +
                            newClassName + ".", Toast.LENGTH_SHORT).show();
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
     * @param index the index of the class in the list view that the user wants to delete
     */
    public void createAlertDialogForConfirmation(String message, final String className, final int index)
    {
        //simple alert dialog for confirmation
        new AlertDialog.Builder(this)
                .setTitle("Wait...")
                .setMessage(message)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //remove the class from the list view
                        m_classes.remove(index);
                        m_classesListViewAdapter.notifyDataSetChanged();

                        //delete the class from the app
                        ClassModel.deleteClass(className, getApplicationContext());
                        SingletonWeekView.getInstance().getWeekView().notifyDatasetChanged();

                        Toast.makeText(getApplicationContext(), "The class: " + className + " was deleted.",
                                       Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //bring up the dialog box again
                        m_alertDialogForLongPressingAClass.show();
                    }
                }).show();
    }

    /**
     * Checks to see if any classes have the same name as the name specified in the parameter.
     * @param name the name of the new class name.
     * @return true if the name is unique, false if not.
     */
    private boolean isClassNameUnique(String name)
    {
        for (ClassModel model : ClassModel.getAllClasses())
        {
            if (model.getName().equals(name))
                return false;
        }

        return true;
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