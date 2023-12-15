package edu.blaylock.server.database.fields;

import java.sql.JDBCType;

/**
 * A field that serializes from Integer to Integer and vice-versa
 */
public class IntField extends Field<Integer> {


    public IntField(String name, ColumnAttributes attributes) {
        super(name, JDBCType.INTEGER, attributes);
    }

    /**
     * Create non-unique Intfield
     *
     * @param name name of field e.g. "roundNumber"
     */
    public IntField(String name) {
        this(name, ColumnAttributes.defaultAttrs());
    }


    @Override
    public Integer serialize(Integer deserialized) {
        return deserialized;
    }

    @Override
    public Integer deserialize(Object serialized) {
        if (serialized == null) return null;
        return (Integer) serialized;
    }
}
