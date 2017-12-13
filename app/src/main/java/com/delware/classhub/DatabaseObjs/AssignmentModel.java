package com.delware.classhub.DatabaseObjs;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.Date;
import java.util.List;

@Table(name = "Assignments")
public class AssignmentModel extends Model
{
    //The assignment needs a relationship to a class
    @Column(name = "Class")
    private ClassModel m_associatedClass;

    @Column(name = "Name")
    private String m_name;

    @Column(name = "DueDate")
    private Date m_dueDate;

    @Column(name = "PriorityLevel")
    private int m_priorityLevel;

    @Column(name = "AdditionalNotes")
    private String m_additionalNotes;

    @Column(name = "IsCompleted")
    private Boolean m_isCompleted;

    public AssignmentModel()
    {
        super();

        m_associatedClass = null;
        m_name = null;
        m_dueDate = null;
        m_priorityLevel = 1;
        m_additionalNotes = null;
        m_isCompleted = false;
    }

    public static List<AssignmentModel> getAllAssignments()
    {
        return new Select().from(AssignmentModel.class).execute();
    }

    public static AssignmentModel getAssignment(long id)
    {
        return new Select().from(AssignmentModel.class).where("Id = ?", id).executeSingle();
    }

    public ClassModel getAssociatedClass() {
        return m_associatedClass;
    }

    public void setAssociatedClass(ClassModel associatedClass) {
        m_associatedClass = associatedClass;
    }

    public String getName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    public Date getDueDate() {
        return m_dueDate;
    }

    public void setDueDate(Date dueDate) {
        m_dueDate = dueDate;
    }

    public int getPriorityLevel() {
        return m_priorityLevel;
    }

    public void setPriorityLevel(int priorityLevel) {
        m_priorityLevel = priorityLevel;
    }

    public String getAdditionalNotes() {
        return m_additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        m_additionalNotes = additionalNotes;
    }

    public Boolean getIsCompleted() {
        return m_isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        m_isCompleted = isCompleted;
    }
}
