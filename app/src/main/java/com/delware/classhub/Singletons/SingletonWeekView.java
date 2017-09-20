package com.delware.classhub.Singletons;

import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;

/**
 * Created by Matt on 9/13/2017.
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
