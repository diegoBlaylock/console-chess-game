package edu.blaylock.server.database.tablespecs;

import edu.blaylock.server.database.fields.Field;
import edu.blaylock.server.database.models.IModel;
import edu.blaylock.server.exceptions.DataAccessException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Describes various properties of the table as well as how to go from a model to an array of java objects.
 *
 * @param <T> Model type
 */
public abstract class TableSpec<T extends IModel> {
    /**
     * Name of table
     */
    protected final String name;

    /**
     * Class of T
     */
    private final Class<T> modelClass;

    /**
     * A one-to-one object with a model. It supplies various functions to implement CRUD, create, read, update, delete
     *
     * @param name  Name of "table" to be created
     * @param clazz Model class
     */
    protected TableSpec(String name, Class<T> clazz) {
        this.name = name;
        this.modelClass = clazz;
    }

    public String getName() {
        return this.name;
    }

    public Class<T> getModelClass() {
        return this.modelClass;
    }

    /**
     * Describes the fields of the table (Names as well as datatype)
     *
     * @return Array of fields
     */
    public abstract Field<?>[] getSignature();

    /**
     * Converts from a model to an object array (list of Objects that match the Signature type). This list
     * should simply organize the data of the Model into an Object Array with the Types specified by the fields
     *
     * @param model Model to convert
     * @return Array of fields
     */
    protected abstract Object[] convertModelToArray(T model);

    /**
     * Converts from a record to a model
     *
     * @param fieldValues Array of field values matching the java types of the Signature fields
     * @return Model
     */
    protected abstract T convertArrayToModel(Object[] fieldValues);

    /**
     * Use in phase 4, Convert from java mode to database record for a row in the table
     *
     * @param model Model to be converted
     * @return Array of database typed objects
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Object[] getDatabaseRecordFromModel(T model) {
        Object[] javaObjects = convertModelToArray(model);
        Object[] results = new Object[javaObjects.length];
        for (int i = 0; i < javaObjects.length; i++) {
            Field field = getSignature()[i];
            results[i] = field.serialize(javaObjects[i]);
        }
        return results;
    }

    /**
     * Convert from database types to model from a row in the table
     *
     * @param dbObjects Array of db types
     * @return Array of java objects
     */
    public T getModelFromDatabaseRecord(Object[] dbObjects) {
        Object[] results = new Object[dbObjects.length];
        for (int i = 0; i < dbObjects.length; i++) {
            results[i] = getSignature()[i].deserialize(dbObjects[i]);
        }

        return convertArrayToModel(results);
    }

    public Field<?> getFieldByName(String name) throws DataAccessException {
        for (Field<?> field : getSignature()) {
            if (name.equals(field.name)) return field;
        }
        throw new DataAccessException("Field not found locally");
    }

    /**
     * Set all parameters in a prepared statement to the data from a model
     *
     * @param model  Model that will be converted to an array of objects through getDatabaseRecordFromModel
     * @param offset Where in prepared statement to place data
     * @throws SQLException Error from prepared statement
     */
    public void setStatementData(PreparedStatement statement, T model, int offset) throws SQLException {
        Object[] data = getDatabaseRecordFromModel(model);

        for (int i = 0; i < getSignature().length; i++) {
            statement.setObject(i + offset, data[i], getSignature()[i].type);
        }
    }

    public void setStatementDataExcludeKey(PreparedStatement statement, T model, int offset) throws SQLException {
        Object[] data = getDatabaseRecordFromModel(model);
        int current_index = 0;
        for (int i = 0; i < getSignature().length; i++) {
            if (getSignature()[i].attributes.primary()) continue;
            statement.setObject(current_index + offset, data[i], getSignature()[i].type);
            current_index++;
        }
    }

    /**
     * Get Model from a result set
     *
     * @param results Result Set
     * @return Model
     * @throws SQLException Error from result set
     */
    public T getmodelFromResultSet(ResultSet results) throws SQLException {
        Object[] record = new Object[getSignature().length];

        for (int i = 0; i < record.length; i++) {
            record[i] = results.getObject(i + 1);
        }

        return getModelFromDatabaseRecord(record);
    }


}
