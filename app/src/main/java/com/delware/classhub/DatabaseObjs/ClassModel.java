package com.delware.classhub.DatabaseObjs;
import android.content.Context;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.io.File;
import java.util.List;

/**
 * Overview: This class is an ORM object for ActiveAndroid.
 * An instance of the ClassModel class represents a single class but
 * the static methods allow operations on all ClassModel objects in the database.
 * @author Matt Del Fante
 */
@Table(name = "Classes")
public class ClassModel extends Model
{
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

    /**
     * @return a list of AssignmentModels that are associated with the calling ClassModel.
     */
    public List<AssignmentModel> getAssignments()
    {
        return getMany(AssignmentModel.class, "Class");
    }

    /**
     * @return a list of AudioRecordingModels that are associated with the calling ClassModel.
     */
    public List<AudioRecordingModel> getAudioRecordings()
    {
        return getMany(AudioRecordingModel.class, "Class");
    }

    /**
     *@return a list of all the ClassModels in the database.
     */
    public static List<ClassModel> getAllClasses()
    {
        return new Select().from(ClassModel.class).execute();
    }

    /**
     * @return a list of ClassModels that are not archived.
     */
    public static List<ClassModel> getNonArchivedClasses()
    {
        return new Select().from(ClassModel.class).where("IsArchived = ?", 0).execute();
    }

    /**
     * @return a list of ClassModels that are archived.
     */
    public static List<ClassModel> getArchivedClasses()
    {
        return new Select().from(ClassModel.class).where("IsArchived = ?", 1).execute();
    }

    /**
     * Deletes a class from the database. This includes deleting all of the class' associated
     * AssignmentModels, AudioRecordingModels, NoteModels and VideoRecordingModels.
     * @param className the name of the class to be deleted.
     * @param context the context of the activity that called the method.
     */
    public static void deleteClass(String className, Context context)
    {
        ClassModel classModel = getClass(className);

        deleteAssociatedAssignments(classModel);
        deleteAssociatedAudioRecordings(classModel, context);

        //delete the class
        new Delete().from(ClassModel.class).where("Id = ?", classModel.getId()).execute();
    }

    /**
     * Deletes all of the AssignmentModel objects that are associated with the class model
     * that is going to be deleted.
     * @param classModel the class that is getting deleted.
     */
    private static void deleteAssociatedAssignments(ClassModel classModel)
    {
        List<AssignmentModel> assignments = classModel.getAssignments();

        //delete all of the assignments associated with a class
        for (AssignmentModel model : assignments)
            new Delete().from(AssignmentModel.class).where("Id = ?", model.getId()).execute();
    }

    /**
     * Deletes all of the AudioRecordingModel objects and VideoRecordingModel objects
     * that are associated with the class model that is going to be deleted.
     * @param classModel the class that is getting deleted.
     * @param context the context of the activity that called the method.
     */
    private static void deleteAssociatedAudioRecordings(ClassModel classModel, Context context)
    {
        List<AudioRecordingModel> audioRecordings = classModel.getAudioRecordings();

        String uniquePrefix = classModel.getId() + "audio";

        String pathToAudioRecording = context.getFilesDir() + "/" + uniquePrefix + "_";

        for (AudioRecordingModel model : audioRecordings)
        {
            File f = new File(pathToAudioRecording + model.getName() + ".mp4");

            //delete the audio recording from internal storage
            if (!f.delete())
                Log.i("LOG: ", "The audio recording: " + model.getName() + " was not deleted.");

            //delete the audio recording from the db
            new Delete().from(AudioRecordingModel.class).where("Id = ?", model.getId()).execute();
        }
    }

    /**
     * Renames a class to a new name in the database.
     * @param oldClassName the name of the class before it was renamed.
     * @param newClassName the updated name of the class.
     */
    public static void renameClass(String oldClassName, String newClassName)
    {
        ClassModel _class = new Select().from(ClassModel.class).where("Name = ?", oldClassName).executeSingle();
        _class.m_name = newClassName;
        _class.save();
    }

    /**
     * Updates the database to set a ClassModel archived.
     * @param className the class being archived.
     */
    public static void makeClassArchived(String className)
    {
        ClassModel _class = new Select().from(ClassModel.class).where("Name = ?", className).executeSingle();
        _class.m_isArchived = true;
        _class.save();
    }

    /**
     * @return the ClassModel with the name className.
     * @param className the class being retrieved.
     */
    public static ClassModel getClass(String className)
    {
        return new Select().from(ClassModel.class).where("Name = ?", className).executeSingle();
    }
}
