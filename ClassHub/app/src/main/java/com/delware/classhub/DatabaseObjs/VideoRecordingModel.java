package com.delware.classhub.DatabaseObjs;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Matt on 1/10/2018.
 */
@Table(name = "VideoRecordings")
public class VideoRecordingModel extends Model {
    //The assignment needs a relationship to a class
    @Column(name = "Class")
    private ClassModel m_associatedClass;

    @Column(name = "Name")
    private String m_name;

    public VideoRecordingModel()
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
