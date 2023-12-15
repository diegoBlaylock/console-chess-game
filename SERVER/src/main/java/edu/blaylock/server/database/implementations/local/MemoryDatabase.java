package edu.blaylock.server.database.implementations.local;

import edu.blaylock.server.database.implementations.IDatabase;
import edu.blaylock.server.database.models.IModel;
import edu.blaylock.server.database.tablespecs.TableSpec;
import edu.blaylock.server.exceptions.DataAccessException;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.util.*;

/**
 * In-memory representation of database
 */
public class MemoryDatabase implements IDatabase {

    /**
     * Container for tables
     */
    Map<String, Table> tableMap = new HashMap<>();

    @Override
    public <T extends TableSpec<?>> void createTable(Class<T> tableSpecification) throws DataAccessException {
        T tableSpec = getTableSpecFromClass(tableSpecification);
        tableMap.putIfAbsent(tableSpec.getName(), new Table(tableSpec));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IModel> T[] queryAll(TableSpec<T> spec) throws DataAccessException {
        List<T> results = new ArrayList<>();
        Table table = getTable(spec);
        table.iterator().forEachRemaining((record) -> results.add(spec.getModelFromDatabaseRecord(record)));
        return results.toArray((T[]) Array.newInstance(spec.getModelClass(), 0));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IModel> T[] queryRecordsByField(TableSpec<T> spec, String field, Object key) throws DataAccessException {
        Table table = getTable(spec);
        Object[][] queryResults = table.findField(field, key);

        T[] modelResults = (T[]) Array.newInstance(spec.getModelClass(), queryResults.length);
        for (int i = 0; i < queryResults.length; i++)
            modelResults[i] = spec.getModelFromDatabaseRecord(queryResults[i]);

        return modelResults;
    }

    @Override
    public <T extends IModel> void addRecord(TableSpec<T> spec, T model) throws DataAccessException {
        Table table = getTable(spec);
        table.addTuple(spec.getDatabaseRecordFromModel(model));
    }

    @Override
    public <T extends IModel> ResultSet addRecordGetGeneratedKeys(TableSpec<T> spec, T model) throws DataAccessException {
        return null;
    }

    @Override
    public <T extends IModel> void deleteRecord(TableSpec<T> spec, T comparison) throws DataAccessException {
        Table table = getTable(spec);
        for (Iterator<Object[]> it = table.iterator(); it.hasNext(); ) {
            T model = spec.getModelFromDatabaseRecord(it.next());
            if (comparison.equals(model)) {
                it.remove();
                break;
            }
        }
    }

    @Override
    public <T extends IModel> int updateFieldInRecord(TableSpec<T> spec, String first, Object firstValue, String fieldName, Object replacement) throws DataAccessException {
        Table table = getTable(spec);
        return table.replaceField(first, firstValue, fieldName, replacement);
    }


    @Override
    public void dropDatabase() {
        for (Table table : tableMap.values()) {
            table.deleteAll();
        }
    }

    /**
     * HTML representation of database for debugging
     *
     * @return database
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<pre>");

        for (Map.Entry<String, Table> entry : tableMap.entrySet()) {
            builder.append(entry.getKey()).append(": \n  ")
                    .append("Primary Index: ").append(entry.getValue().indexPrimary).append("\n  ")
                    .append("Headers: ").append(Arrays.toString(entry.getValue().header)).append("\n  ")
                    .append("Records:\n");

            for (Object[] record : entry.getValue().tupleMap.values()) {
                builder.append("    ").append(Arrays.toString(record)).append("\n");
            }
            builder.append('\n');

        }
        builder.append("</pre>");
        return builder.toString();
    }


    /**
     * Get table for spec
     *
     * @param spec Table specification
     * @return Table
     * @throws DataAccessException if table not found
     */
    private Table getTable(TableSpec<?> spec) throws DataAccessException {
        Table table = tableMap.get(spec.getName());
        if (table == null) throw new DataAccessException("Table not found");
        return table;
    }
}
