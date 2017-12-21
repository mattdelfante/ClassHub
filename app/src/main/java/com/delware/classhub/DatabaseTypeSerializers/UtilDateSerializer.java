package com.delware.classhub.DatabaseTypeSerializers;
import com.activeandroid.serializer.TypeSerializer;
import java.util.Date;

/**
 * Overview: This class extends the TypeSerializer class so I can allow the ActiveAndroid
 * ORM serialize and deserialize Date objects so dates can be stored as values in the database.
 * @author Matt Del Fante
 */
final public class UtilDateSerializer extends TypeSerializer {
    @Override
    public Class<?> getDeserializedType() {
        return Date.class;
    }

    @Override
    public Class<?> getSerializedType() {
        return Long.class;
    }

    @Override
    public Long serialize(Object data) {
        if (data == null) {
            return null;
        }

        return ((Date) data).getTime();
    }

    @Override
    public Date deserialize(Object data) {
        if (data == null) {
            return null;
        }

        return new Date((Long) data);
    }
}