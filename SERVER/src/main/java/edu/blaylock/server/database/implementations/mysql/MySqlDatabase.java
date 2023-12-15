package edu.blaylock.server.database.implementations.mysql;

import edu.blaylock.server.database.fields.Field;
import edu.blaylock.server.database.implementations.IDatabase;
import edu.blaylock.server.database.models.IModel;
import edu.blaylock.server.database.tablespecs.TableSpec;
import edu.blaylock.server.exceptions.DataAccessException;

import java.lang.reflect.Array;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MySqlDatabase implements IDatabase {

    /**
     * Connection pool for all operations
     */
    ConnectionPool connectionPool = new ConnectionPool();

    /**
     * Create new IDatabase instance. Creates mysql database if it doesn't exist
     *
     * @throws DataAccessException Database error
     */
    public MySqlDatabase() throws DataAccessException {
        Connection connection = connectionPool.getConnection();

        try {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + ConnectionPool.DB_NAME + ';');
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } finally {
            connectionPool.returnConnection(connection);
        }
    }

    /**
     * Create a table according to table specs if it doesn't exist
     *
     * @param tableSpecification Class of tables spec: Name of field and their types as well. Contains the table name
     * @param <T>                TableSpec
     * @throws DataAccessException Error creating table. Likely code error
     */
    @Override
    public <T extends TableSpec<?>> void createTable(Class<T> tableSpecification) throws DataAccessException {
        Connection connection = connectionPool.getConnection();

        try {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(createTableStatement(tableSpecification));
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } finally {
            connectionPool.returnConnection(connection);
        }

    }

    /**
     * Grab all models in the table specified.
     *
     * @param spec Description of table and fields
     * @param <T>  Model type described by table spec
     * @return Array of models
     * @throws DataAccessException Error retreiving items
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends IModel> T[] queryAll(TableSpec<T> spec) throws DataAccessException {
        List<T> models = new ArrayList<>();

        Connection connection = connectionPool.getConnection();
        try {
            String sql = "SELECT * FROM " + spec.getName() + " ;";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                try (ResultSet results = statement.executeQuery()) {
                    while (results.next()) {
                        models.add(spec.getmodelFromResultSet(results));
                    }
                }
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } finally {
            connectionPool.returnConnection(connection);
        }
        return models.toArray((T[]) Array.newInstance(spec.getModelClass(), 0));
    }

    /**
     * Grab all record that match the value in the field
     *
     * @param spec  Table description
     * @param field name of field to search
     * @param key   value to match
     * @param <T>   Model Type
     * @return Array of matching models
     * @throws DataAccessException error retrieving data (field doesn't exist)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends IModel> T[] queryRecordsByField(TableSpec<T> spec, String field, Object key) throws DataAccessException {
        List<T> models = new ArrayList<>();

        Connection connection = connectionPool.getConnection();
        try {
            String sql = "SELECT * FROM %s WHERE %s = ? ;";
            sql = String.format(sql, spec.getName(), field);

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                spec.getFieldByName(field).addFieldValue(statement, 1, key);

                try (ResultSet results = statement.executeQuery()) {
                    while (results.next()) {
                        models.add(spec.getmodelFromResultSet(results));
                    }
                }
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        } finally {
            connectionPool.returnConnection(connection);
        }
        return models.toArray((T[]) Array.newInstance(spec.getModelClass(), 0));
    }

    @Override
    public <T extends IModel> void addRecord(TableSpec<T> spec, T model) throws DataAccessException {
        Connection connection = connectionPool.getConnection();
        try {
            String sql = "INSERT INTO " + spec.getName() + " VALUES (" + getValuesStatement(spec) + ");";
            executeSqlWithModelData(spec, connection, sql, model);
        } finally {
            connectionPool.returnConnection(connection);
        }
    }

    @Override
    public <T extends IModel> Object addRecordGetGeneratedKeys(TableSpec<T> spec, T model) throws DataAccessException {
        Connection connection = connectionPool.getConnection();
        try {
            String columns = Arrays.stream(spec.getSignature()).filter((field) -> !field.attributes.primary()).map((field) -> field.name).collect(Collectors.joining(","));
            String sql = "INSERT INTO " + spec.getName() + "(" + columns + ") VALUES (" + getValuesStatementExcludeKey(spec) + ");";
            return executeSqlWithModelDataExcludeKey(spec, connection, sql, model);
        } finally {
            connectionPool.returnConnection(connection);
        }
    }


    @Override
    public <T extends IModel> void deleteRecord(TableSpec<T> spec, T model) throws DataAccessException {
        Connection connection = connectionPool.getConnection();
        try {
            String sql = "DELETE FROM " + spec.getName() + " WHERE " + getEqualsStatement(spec) + ";";
            executeSqlWithModelData(spec, connection, sql, model);
        } finally {
            connectionPool.returnConnection(connection);
        }
    }

    @Override
    public <T extends IModel> int updateFieldInRecord(TableSpec<T> spec, String keyField, Object keyValue, String replacementField, Object replacementValue) throws DataAccessException {
        Connection connection = connectionPool.getConnection();
        try {
            String sql = "UPDATE %s SET %s = ? WHERE %s = ? ;";
            sql = String.format(sql, spec.getName(), replacementField, keyField);

            int result;
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                spec.getFieldByName(replacementField).addFieldValue(statement, 1, replacementValue);
                spec.getFieldByName(keyField).addFieldValue(statement, 2, keyValue);

                result = statement.executeUpdate();
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
            return result;
        } finally {
            connectionPool.returnConnection(connection);
        }
    }

    @Override
    public void dropDatabase() throws DataAccessException {
        Connection connection = connectionPool.getConnection();
        try {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("DROP DATABASE %s".formatted(ConnectionPool.DB_NAME));
                statement.executeUpdate("CREATE DATABASE %s".formatted(ConnectionPool.DB_NAME));
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }

        } finally {
            connectionPool.returnConnection(connection);
        }
    }

    /**
     * Returns a SQL string that will create a table from table spec when executed
     *
     * @param specClass Class of spec to for table to create
     * @param <T>       Type of TableSpec
     * @return SQL statement
     * @throws DataAccessException Error creating TableSpec
     */
    private <T extends TableSpec<?>> String createTableStatement(Class<T> specClass) throws DataAccessException {
        T tableSpec = getTableSpecFromClass(specClass);

        return "CREATE TABLE IF NOT EXISTS " + tableSpec.getName() + " (" +
                String.join(",", (Iterable<String>) Arrays.stream(tableSpec.getSignature())
                        .map(Field::getSqlTypeDescription)::iterator) +
                ");";
    }

    /**
     * Populate sql query with model data and execute
     *
     * @param spec  Table spec to follow
     * @param conn  Sql connection
     * @param sql   Sql statement with things to format
     * @param model Model to populate with
     * @param <T>   Model Type
     * @throws DataAccessException Error with executing statement
     */
    private <T extends IModel> void executeSqlWithModelData(TableSpec<T> spec, Connection conn, String sql, T model) throws DataAccessException {
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            spec.setStatementData(statement, model, 1);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private <T extends IModel> Object executeSqlWithModelDataExcludeKey(TableSpec<T> spec, Connection conn, String sql, T model) throws DataAccessException {
        try (PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            spec.setStatementDataExcludeKey(statement, model, 1);
            statement.executeUpdate();
            try (ResultSet result = statement.getGeneratedKeys()) {
                result.next();
                return result.getObject(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Create a string with the form of '&lt;column_name&gt;=? AND ...' Use for Where sequel statement
     *
     * @param spec Table spec to follow
     * @return Partial SQL statement
     */
    private String getEqualsStatement(TableSpec<?> spec) {
        return String.join(" AND ", (Iterable<String>) Arrays.stream(spec.getSignature())
                .map(field -> field.name + " = ?")::iterator);
    }

    /**
     * Create a string of form '?,?,...' equal to number of columns in table
     *
     * @param spec Table spec to follow
     * @return Partial sql statement
     */
    private String getValuesStatement(TableSpec<?> spec) {
        return String.join(",", Collections.nCopies(spec.getSignature().length, "?"));
    }

    private <T extends IModel> String getValuesStatementExcludeKey(TableSpec<T> spec) {
        return String.join(",", Collections.nCopies(spec.getSignature().length - 1, "?"));
    }
}
