package com.delware.classhub.DatabaseModels;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Overview: This class is an ORM object for ActiveAndroid.
 * An instance of the AudioRecordingModel class represents an audio recording but the actual
 * audio recording is stored in internal storage on the android device.
 * @author Matt Del Fante
 */
@Table(name = "AudioRecordings")
public class AudioRecordingModel extends Model {
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
