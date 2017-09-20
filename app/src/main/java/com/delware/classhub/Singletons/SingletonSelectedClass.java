package com.delware.classhub.Singletons;

import com.delware.classhub.DatabaseObjs.ClassModel;

public class SingletonSelectedClass {
    private static SingletonSelectedClass m_instance = null;
    private ClassModel m_selectedClass;

    private SingletonSelectedClass(){}

    public static SingletonSelectedClass getInstance()
    {
        if (m_instance == null) {
            m_instance = new SingletonSelectedClass();
        }

        return m_instance;
    }

    public void setSelectedClass (ClassModel _class)
    {
        m_selectedClass = _class;
    }

    public ClassModel getSelectedClass()
    {
        return m_selectedClass;
    }
}
