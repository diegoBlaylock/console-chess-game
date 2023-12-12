package edu.blaylock.server.database.dao;

import edu.blaylock.server.ServerGlobals;
import edu.blaylock.server.database.implementations.IDatabase;
import edu.blaylock.server.database.models.IModel;
import edu.blaylock.server.database.tablespecs.TableSpec;
import edu.blaylock.server.exceptions.DataAccessException;

/**
 * This class forms a simple representation of a table in a database that is meant to convert to a model in java.
 * This class provides a table specification that describes the name of the table in the database
 * as well as the fields (name, type, attributes) within the table. Converting from a model to database: <br/>
 * &emsp;Java IModel &lt;-&gt; Java Type Object Array &lt;-&gt; Database Type Object Array &lt;-&gt; Database record
 * <br/> This class only implements methods for interfacing the database. All these conversions are handled by
 * the table specification from getSpecification
 *
 * @param <T> Model that this DAO/Table will represent
 */
public abstract class DAO<T extends IModel> {

    /**
     * database for accessing data
     */
    protected final IDatabase database;

    /**
     * A one-to-one object with a model. It supplies various functions to implement CRUD, create, read, update, delete
     *
     * @param database Database to use (Connection in future)
     */
    public DAO(IDatabase database) throws DataAccessException {
        this.database = database;
        this.database.createTable(getSpecification().getClass());
    }

    public DAO() throws DataAccessException {
        this(ServerGlobals.database());
    }

    /**
     * Get the table specification for this DAO
     *
     * @return specification
     */
    public abstract TableSpec<T> getSpecification();

    /**
     * Store a new model in database
     *
     * @param model Model to be stored
     * @throws DataAccessException Thrown in case of Database Error
     */
    public void create(T model) throws DataAccessException {
        this.database.addRecord(getSpecification(), model);
    }

    /**
     * Update all rows in table with the following value in the specified field
     *
     * @param keyField         Name of the field to search
     * @param key              Value of the field to match
     * @param valueField       Name of Field to replace
     * @param replacementValue Value to Replace in Field
     * @return Number of records updated
     * @throws DataAccessException Thrown in case of Database Error
     */
    public int updateFieldByFieldAttribute(String keyField, Object key, String valueField, Object replacementValue) throws DataAccessException {
        return this.database.updateFieldInRecord(getSpecification(), keyField, key, valueField, replacementValue);
    }

    /**
     * Delete a model from the database
     *
     * @param model Delete model in database
     * @throws DataAccessException Thrown in case of Database Error
     */
    public void delete(T model) throws DataAccessException {
        this.database.deleteRecord(getSpecification(), model);
    }

    /**
     * Return array of models found to have attribute
     *
     * @param field name of field to search
     * @param value value in field to match
     * @return array of models
     * @throws DataAccessException Thrown if a database error occurred.
     */
    public T[] findModelsByAttribute(String field, Object value) throws DataAccessException {
        return this.database.queryRecordsByField(getSpecification(), field, value);
    }

    /**
     * Return all models in the table
     *
     * @return All models
     * @throws DataAccessException Thrown if there is a table
     */
    public T[] findAll() throws DataAccessException {
        return this.database.queryAll(getSpecification());
    }


}
