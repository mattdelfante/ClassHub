package com.delware.classhub.DatabaseObjs;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import java.util.List;

@Table(name = "Classes")
public class ClassModel extends Model
{
    //IMPORTANT NOTE ABOUT ACTIVE ANDROID
    //One important thing to note is that ActiveAndroid creates an id field for your tables.
    //This field is an auto-incrementing primary key.

    public ClassModel()
    {
        super();
        name = null;
        isArchived = false;
    }

    @Column(name = "Name")
    public String name;

    //booleans get serialized to ints, 0 means false, 1 means true
    @Column(name = "IsArchived")
    public Boolean isArchived;

    //returns a list of Assignments that are assoicated with a class
    public List<AssignmentModel> getAssignments()
    {
        return getMany(AssignmentModel.class, "Class");
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

    public static void deleteClass(String className)
    {
        new Delete().from(ClassModel.class).where("Name = ?", className).execute();
    }

    public static void renameClass(String oldClassName, String newClassName)
    {
        ClassModel _class = new Select().from(ClassModel.class).where("Name = ?", oldClassName).executeSingle();
        _class.name = newClassName;
        _class.save();
    }

    public static void makeClassArchived(String className)
    {
        ClassModel _class = new Select().from(ClassModel.class).where("Name = ?", className).executeSingle();
        _class.isArchived = true;
        _class.save();
    }

    public static ClassModel getClass(String className)
    {
        return new Select().from(ClassModel.class).where("Name = ?", className).executeSingle();
    }
}
