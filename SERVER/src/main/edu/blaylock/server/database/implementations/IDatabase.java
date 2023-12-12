package edu.blaylock.server.database.implementations;

import edu.blaylock.server.database.models.IModel;
import edu.blaylock.server.database.tablespecs.TableSpec;
import edu.blaylock.server.exceptions.DataAccessException;

import java.lang.reflect.InvocationTargetException;

/**
 * Basic interface for interaction between java code and a database. Should be extensible enough for both an
 * in-memory database and mySQL database, but lacks many useful features.
 */
public interface IDatabase {

    /**
     * Will create a table prescribed by the TableSpec if the table doesn't exist.
     *
     * @param tableSpecification Name of field and their types as well. Contains the table name
     * @param <T>                TableSpec class
     * @throws DataAccessException Thrown if there are any errors, or table Specification is faults i.e. More than one
     *                             primary key
     */
    <T extends TableSpec<?>> void createTable(Class<T> tableSpecification) throws DataAccessException;

    /**
     * Returns array of all models
     *
     * @param spec Description of table and fields
     * @param <T>  Model type specified by TableSpec
     * @return array of models
     * @throws DataAccessException Database error
     */
    <T extends IModel> T[] queryAll(TableSpec<T> spec) throws DataAccessException;

    /**
     * Get array of models that match the key in the field
     *
     * @param spec  Table description
     * @param field name of field to search
     * @param key   value to match
     * @param <T>   Model type
     * @return array of model results
     * @throws DataAccessException Database error, field and spec mismatch
     */
    <T extends IModel> T[] queryRecordsByField(TableSpec<T> spec, String field, Object key) throws DataAccessException;

    /**
     * Attempts to create a new record described by model. Throws error if any Uniqueness constraints fail
     *
     * @param spec  Description of table and fields
     * @param model Model containing data to store
     * @param <T>   IModel type of TableSpec and model
     * @throws DataAccessException Thrown if any uniqueness constraints fail.
     */
    <T extends IModel> void addRecord(TableSpec<T> spec, T model) throws DataAccessException;

    <T extends IModel> Object addRecordGetGeneratedKeys(TableSpec<T> spec, T model) throws DataAccessException;

    /**
     * Deletes all records in database that match the condition.
     *
     * @param spec  Description of table and fields
     * @param model model that should be deleted
     * @param <T>   Model table described by TableSpec
     * @throws DataAccessException Database error
     */
    <T extends IModel> void deleteRecord(TableSpec<T> spec, T model) throws DataAccessException;

    /**
     * Update the specified field in all records that meet the specified condition
     *
     * @param spec             Table and fields specification
     * @param keyField         Name of field to match
     * @param keyValue         Value to match
     * @param replacementField Name of field to change
     * @param replacementValue Java type object to replace Field with. The type is specified in dao by the field specified
     * @param <T>              Model described by a record
     * @return Number of records updated
     * @throws DataAccessException Thrown for two reasons, if the fieldName doesn't exist or replacementObject doesn't
     *                             match field type, Or if the field updated fails the uniquesness constraint of the field.
     */
    <T extends IModel> int updateFieldInRecord(TableSpec<T> spec, String keyField, Object keyValue,
                                               String replacementField, Object replacementValue) throws DataAccessException;

    /**
     * Delete all data from tables
     */
    void dropDatabase() throws DataAccessException;

    default <T extends TableSpec<?>> T getTableSpecFromClass(Class<T> tClass) throws DataAccessException {
        try {
            return tClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
