package com.delware.classhub.DatabaseObjs;
import android.content.ContentProvider;
import android.content.Context;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.delware.classhub.Activities.HomeActivity;

import java.io.File;
import java.util.List;

@Table(name = "Classes")
public class ClassModel extends Model
{
    //IMPORTANT NOTE ABOUT ACTIVE ANDROID
    //One important thing to note is that ActiveAndroid creates an id field for your tables.
    //This field is an auto-incrementing primary key.

    @Column(name = "Name")
    private String m_name;

    //booleans get serialized to ints, 0 means false, 1 means true
    @Column(name = "IsArchived")
    private Boolean m_isArchived;

    public ClassModel()
    {
        super();
        m_name = null;
        m_isArchived = false;
    }

    public String getName()
    { return m_name; }

    public void setName(String name)
    { m_name = name; }

    public Boolean isArchived()
    { return m_isArchived; }

    public void setIsArchived(Boolean isArchived)
    { m_isArchived = isArchived; }

    //returns a list of Assignments that are assoicated with a class
    public List<AssignmentModel> getAssignments()
    {
        return getMany(AssignmentModel.class, "Class");
    }

    public List<AudioRecordingModel> getAudioRecordings()
    {
        return getMany(AudioRecordingModel.class, "Class");
    }

    public static List<ClassModel> getNonArchivedClasses()
    {
        return new Select().from(ClassModel.class).where("IsArchived = ?", 0).execute();
    }

    public static List<ClassModel> getArchivedClasses()
    {
        return new Select().from(ClassModel.class).where("IsArchived = ?", 1).execute();
    }

    public static List<ClassModel> getAllClasses()
    {
        return new Select().from(ClassModel.class).execute();
    }

    public static void deleteClass(String className, Context context)
    {
        ClassModel classModel = getClass(className);

        deleteAssociatedAssignments(classModel);
        deleteAssociatedAudioRecordings(classModel, context);

        //delete the class
        new Delete().from(ClassModel.class).where("Name = ?", className).execute();
    }

    private static void deleteAssociatedAssignments(ClassModel classModel)
    {
        List<AssignmentModel> assignments = classModel.getAssignments();

        //delete all of the assignments associated with a class
        for (AssignmentModel model : assignments)
        {
            new Delete().from(AssignmentModel.class).where("Name = ?", model.getName()).execute();
        }
    }

    private static void deleteAssociatedAudioRecordings(ClassModel classModel, Context context)
    {
        List<AudioRecordingModel> audioRecordings = classModel.getAudioRecordings();

        String modifiedClassName = classModel.getName().replaceAll("[^_a-zA-Z0-9\\.\\-]", "");

        String pathToRecording = context.getFilesDir() + "/" + modifiedClassName + "_";

        for (AudioRecordingModel model : audioRecordings)
        {
            File f = new File(pathToRecording + model.getName() + ".mp4");

            //delete the audio recording from internal storage
            if (f.delete() == false)
                Log.i("LOG: ", "The audio recording: " + model.getName() + " was not deleted.");

            //delete the audio recording from the db
            new Delete().from(AudioRecordingModel.class).where("Name = ?", model.getName()).execute();
        }
    }

    public static void renameClass(String oldClassName, String newClassName)
    {
        ClassModel _class = new Select().from(ClassModel.class).where("Name = ?", oldClassName).executeSingle();
        _class.m_name = newClassName;
        _class.save();
    }

    public static void makeClassArchived(String className)
    {
        ClassModel _class = new Select().from(ClassModel.class).where("Name = ?", className).executeSingle();
        _class.m_isArchived = true;
        _class.save();
    }

    public static ClassModel getClass(String className)
    {
        return new Select().from(ClassModel.class).where("Name = ?", className).executeSingle();
    }
}
