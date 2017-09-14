package com.delware.classhub;

import java.util.Calendar;

/**
 * Created by Matt on 9/12/2017.
 */

public class Assignment
{
    private String m_name;
    private Calendar m_dueDate;
    private int m_priorityLevel;
    private String m_notes;

    public Assignment()
    {
        m_name = null;
        m_dueDate = null;

        //default to lowest priority level and no notes
        m_priorityLevel = 0;
        m_notes = "";
    }

    public void setName(String name)
    {
        m_name = name;
    }

    public void setDueDate(Calendar date)
    {
        m_dueDate = date;
    }

    public void setPriorityLevel(int level)
    {
        m_priorityLevel = level;
    }

    public void setNotes(String notes)
    {
        m_notes = notes;
    }

    public String getName(){ return m_name; }
    public String getNotes() { return m_notes; }
    public Calendar getDueDate() { return m_dueDate; }
    public int getPriorityLevel() {return m_priorityLevel;}
}
