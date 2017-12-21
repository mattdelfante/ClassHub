package com.delware.classhub.Singletons;

import com.alamkanak.weekview.WeekView;

/**
 * Overview: This class represents a single point of global access to the WeekView
 * calendar on the HomeActivity. This allows the ClassActivity and the ViewAssignmentsActivity
 * to update the WeekView calendar when assignments are added to the application or are modified.
 * @author Matt Del Fante
 */
public class SingletonWeekView {
    private static SingletonWeekView instance = null;
    private WeekView m_cal = null;

    private SingletonWeekView() {}

    public static SingletonWeekView getInstance()
    {
        if (instance == null)
            instance = new SingletonWeekView();

        return instance;
    }

    public void setWeekView(WeekView cal)
    {
        m_cal = cal;
    }

    public WeekView getWeekView()
    {
        return m_cal;
    }
}
