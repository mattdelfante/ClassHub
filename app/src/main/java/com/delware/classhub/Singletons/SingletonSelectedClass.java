package com.delware.classhub.Singletons;

import com.delware.classhub.DatabaseObjs.ClassModel;

/**
 * Overview: This class represents a single point of global access to the ClassModel
 * the user chose to interact with on the HomeActivity. All of the activities in the Class Hub
 * app interact with this class in order to access or modify the selected class' information.
 * @author Matt Del Fante
 */
public class SingletonSelectedClass
{
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
