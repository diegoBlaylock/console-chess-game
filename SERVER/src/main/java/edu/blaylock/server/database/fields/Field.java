package edu.blaylock.server.database.fields;

import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Represents attributes of a table field. Also provides methods to serialize
 * and deserialize an object to a database type
 *
 * @param <JAVA_TYPE> Type of java object to serialize
 */
public abstract class Field<JAVA_TYPE> {

    /**
     * name of field
     */
    public final String name;
    /**
     * Type in database e.g. STRING, INT, etc.
     */
    public final JDBCType type;

    public final ColumnAttributes attributes;

    /**
     * Populate class with attributes of a database field
     *
     * @param name       Name of field
     * @param dbType     Type of field
     * @param attributes Column attributes
     */
    protected Field(String name, JDBCType dbType, ColumnAttributes attributes) {
        this.name = name;
        this.type = dbType;
        this.attributes = attributes;
    }

    /**
     * Convert from Java-type to DB type specified by 'type'
     *
     * @param deserialized Object to serialize
     * @return Database storable Serialized representation
     */
    public abstract Object serialize(JAVA_TYPE deserialized);

    /**
     * Convert from Database type to Javatype
     *
     * @param serialized Database representation of object (Based on 'type')
     * @return Java type
     */
    public abstract JAVA_TYPE deserialize(Object serialized);

    /**
     * Create A string description of this field type used by MySql
     *
     * @return mysql type
     */
    public String getSqlTypeDescription() {
        StringBuilder result = new StringBuilder();
        result.append(name).append(" ").append(type.getName());

        addToBuilderIf(type == JDBCType.VARCHAR, "(%d)".formatted(attributes.maxLength()), result);
        addToBuilderIf(!attributes.nullable(), " NOT NULL", result);
        addToBuilderIf(attributes.primary(), " PRIMARY KEY", result);
        addToBuilderIf(attributes.autoIncrement(), " AUTO_INCREMENT", result);

        if (!attributes.primary()) {
            addToBuilderIf(attributes.unique(), " UNIQUE", result);
            addToBuilderIf(attributes.index(), ",INDEX(%s)".formatted(name), result);
        }

        return result.toString();
    }

    /**
     * Add to a prepared statement the serialized value
     *
     * @param statement Sql statement
     * @param index     Where to place object
     * @param value     object to convert and place
     * @throws SQLException In case of error with sql
     */
    @SuppressWarnings("unchecked")
    public void addFieldValue(PreparedStatement statement, int index, Object value) throws SQLException {
        statement.setObject(index, serialize((JAVA_TYPE) value), type);
    }

    /**
     * Helper function to simplify adding a string to a string builder conditionally
     *
     * @param condition Boolean that tells whether to add or not
     * @param string    String to add
     * @param builder   What will be added
     */
    private void addToBuilderIf(boolean condition, String string, StringBuilder builder) {
        if (condition) {
            builder.append(string);
        }
    }
}
