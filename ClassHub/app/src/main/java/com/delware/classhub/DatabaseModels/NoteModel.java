package com.delware.classhub.DatabaseModels;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

/**
 * Overview: This class is an ORM object for ActiveAndroid.
 * An instance of the NoteModel class represents a single note but
 * the static methods allow operations on all NoteModel objects in the database.
 * @author Matt Del Fante
 */
@Table(name = "Notes")
public class NoteModel extends Model {
    @Column(name = "Class")
    private ClassModel m_associatedClass;

    @Column(name = "Name")
    private String m_name;

    @Column(name = "Note")
    private String m_note;

    public NoteModel()
    {
        super();
        m_associatedClass = null;
        m_name = null;
        m_note = null;
    }

    /**
     * Returns the NoteModel with the same id as the passed parameter from the database.
     * @param id the unique id of the NoteModel to be returned.
     * @return The NoteModel with the corresponding id.
     */
    public static NoteModel getNote(long id)
    {
        return new Select().from(NoteModel.class).where("Id = ?", id).executeSingle();
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

    public String getNote() { return m_note; }

    public void setNote(String note) { m_note = note; }
}
