package edu.blaylock.server.database.fields;

import java.sql.JDBCType;

/**
 * A field that serializes from String to String and vice-versa
 */
public class VarCharField extends Field<String> {

    public VarCharField(String name, ColumnAttributes attributes) {
        super(name, JDBCType.VARCHAR, attributes);
    }

    /**
     * Create non-unique Intfield
     *
     * @param name name of field e.g. "studentName"
     */
    public VarCharField(String name, int max_length) {
        this(name, ColumnAttributes.builder().maxLength(max_length).build());
    }


    @Override
    public String serialize(String deserialized) {
        return deserialized;
    }

    @Override
    public String deserialize(Object serialized) {
        return (String) serialized;
    }
}
