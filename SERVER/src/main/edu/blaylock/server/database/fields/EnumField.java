package edu.blaylock.server.database.fields;

import java.sql.JDBCType;

public class EnumField<T extends Enum<T>> extends Field<T> {
    Class<T> enumClass;

    public EnumField(String name, Class<T> clazz) {
        super(name, JDBCType.VARCHAR, ColumnAttributes.builder().notNull().maxLength(64).build());
        enumClass = clazz;
    }

    @Override
    public String serialize(T deserialized) {
        return deserialized.toString();
    }

    @Override
    public T deserialize(Object serialized) {
        return T.valueOf(enumClass, (String) serialized);
    }
}
