package edu.blaylock.server.database.implementations.local;

import edu.blaylock.server.database.fields.Field;
import edu.blaylock.server.database.tablespecs.TableSpec;
import edu.blaylock.server.exceptions.DataAccessException;
import edu.blaylock.utils.gson.GsonUtils;

import java.util.*;

/**
 * In-memory representation of a database table
 */
public class Table {
    /**
     * Name of table
     */
    transient String name;
    /**
     * Header fields of table
     */
    String[] header;
    /**
     * index of primary key
     */
    int indexPrimary;

    /**
     * Hashed table of primary key to table row
     */
    Map<Object, Object[]> tupleMap = new HashMap<>();

    /**
     * Create table based on specification
     *
     * @param tableSpec table spec
     */
    protected Table(TableSpec<?> tableSpec) {
        Field<?>[] fields = tableSpec.getSignature();
        name = tableSpec.getName();
        header = new String[fields.length];

        for (int i = 0; i < fields.length; i++) {
            Field<?> field = fields[i];
            if (field.attributes.primary()) {
                this.indexPrimary = i;
            }
            header[i] = field.name;
        }
    }

    /**
     * Add tuple to table
     *
     * @param tuple tuple
     */
    public void addTuple(Object[] tuple) {
        tupleMap.put(tuple[indexPrimary], tuple);
    }

    /**
     * Iterator of tuples
     *
     * @return Iterator of tuples
     */
    public Iterator<Object[]> iterator() {
        return tupleMap.values().iterator();
    }

    /**
     * Search table for all tuples that have a matching value in specified field
     *
     * @param field Field to look in
     * @param value Value of field
     * @return Array of tuples(Object arrays)
     * @throws DataAccessException Database error
     */
    public Object[][] findField(String field, Object value) throws DataAccessException {
        List<Object[]> results = new ArrayList<>();
        int search_index = getIndexOfField(field);
        if (indexPrimary == search_index) {
            if (tupleMap.containsKey(value)) results.add(tupleMap.get(value));
        } else {
            for (Iterator<Object[]> iterator = iterator(); iterator.hasNext(); ) {
                Object[] fields = iterator.next();
                if (fields[search_index].equals(value)) {
                    results.add(fields);
                }
            }
        }
        return results.toArray(new Object[0][header.length]);
    }

    /**
     * Replaces replacement value in replacementField for all tuples that match the keyValue in keyField
     *
     * @param keyField         Field name to match
     * @param keyValue         Field value to match
     * @param replacementField Field name to replace
     * @param replacementValue Field value that will replace
     * @return Number of changes made
     * @throws DataAccessException Database error
     */
    public int replaceField(String keyField, Object keyValue,
                            String replacementField, Object replacementValue) throws DataAccessException {
        int replacementIndex = getIndexOfField(replacementField);
        Object[][] matchingRecords = findField(keyField, keyValue);

        for (Object[] row : matchingRecords) {
            if (replacementIndex == indexPrimary) tupleMap.remove(row[indexPrimary]);
            row[replacementIndex] = replacementValue;
            if (replacementIndex == indexPrimary) addTuple(row);
        }

        return matchingRecords.length;
    }

    /**
     * Get the index of a field by name
     *
     * @param field name of the field
     * @return index of field
     * @throws DataAccessException Field doesn't exist
     */
    private int getIndexOfField(String field) throws DataAccessException {
        for (int i = 0; i < header.length; i++) {
            if (header[i].equals(field)) {
                return i;
            }
        }

        throw new DataAccessException("Couldn't access field");
    }

    /**
     * Delete all rows
     */
    public void deleteAll() {
        tupleMap.clear();
    }

    /**
     * Simple representation of this table. For debugging
     *
     * @return String
     */
    @Override
    public String toString() {
        return GsonUtils.standard().toJson(this);
    }
}
