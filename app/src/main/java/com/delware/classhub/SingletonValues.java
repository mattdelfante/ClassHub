package com.delware.classhub;

/**
 * Created by Matt on 8/20/2017.
 */

public class SingletonValues {
    private static SingletonValues instance = null;
    private SingletonValues(){}
    private String selectedClassName;

    public static SingletonValues getInstance()
    {
        if (instance == null) {
            instance = new SingletonValues();
        }

        return instance;
    }

    public void setSelectedClassName (String name)
    {
        instance.selectedClassName = name;
    }

    public String getSelectedClassName()
    {
        return instance.selectedClassName;
    }
}
