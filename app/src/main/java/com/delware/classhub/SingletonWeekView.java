package com.delware.classhub;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;

/**
 * Created by Matt on 9/13/2017.
 */

public class SingletonWeekView {
    private static SingletonWeekView instance = null;
    private ArrayList<WeekViewEvent> m_events;
    private WeekView m_cal = null;

    private SingletonWeekView()
    {
        m_events = new ArrayList<WeekViewEvent>();
    }

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

    public  ArrayList<WeekViewEvent> getEvents()
    {
        return m_events;
    }
}
