package com.delware.classhub.DatabaseObjs;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import java.util.Date;

@Table(name = "Assignments")
public class AssignmentModel extends Model
{
    public AssignmentModel()
    {
        super();

        associatedClass = null;
        name = null;
        dueDate = null;
        priorityLevel = 1;
        additionalNotes = null;
        isCompleted = false;
    }

    //The assignment needs a relationship to a class
    @Column(name = "Class")
    public ClassModel associatedClass;

    @Column(name = "Name")
    public String name;

    @Column(name = "DueDate")
    public Date dueDate;

    @Column(name = "PriorityLevel")
    public int priorityLevel;

    @Column(name = "AdditionalNotes")
    public String additionalNotes;

    @Column(name = "IsCompleted")
    public Boolean isCompleted;


}
