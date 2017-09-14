package com.delware.classhub;

/**
 * Created by Matt on 8/20/2017.
 */

public class SingletonValues {
    private static SingletonValues instance = null;
    private String selectedClassName;

    private SingletonValues(){}

    public static SingletonValues getInstance()
    {
        if (instance == null) {
            instance = new SingletonValues();
        }

        return instance;
    }

    public void setSelectedClassName (String name)
    {
        selectedClassName = name;
    }

    public String getSelectedClassName()
    {
        return selectedClassName;
    }

}
