package com.delware.classhub.DatabaseObjs;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Matt on 12/15/2017.
 */

@Table(name = "AudioRecordings")
public class AudioRecordingModel extends Model {

    //The assignment needs a relationship to a class
    @Column(name = "Class")
    private ClassModel m_associatedClass;

    @Column(name = "Name")
    private String m_name;

    public AudioRecordingModel()
    {
        super();
        m_associatedClass = null;
        m_name = null;
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
}
