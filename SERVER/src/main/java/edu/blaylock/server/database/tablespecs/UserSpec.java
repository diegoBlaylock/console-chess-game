package edu.blaylock.server.database.tablespecs;

import edu.blaylock.server.database.fields.ColumnAttributes;
import edu.blaylock.server.database.fields.Field;
import edu.blaylock.server.database.fields.VarCharField;
import edu.blaylock.server.database.models.User;

/**
 * Info on the User table in database (name and formattings)
 * Converts from database types to java model
 */
public class UserSpec extends TableSpec<User> {
    /**
     * Name of table in database
     */
    static final String TABLE_NAME = "blaylock_user";

    /**
     * Fields in table (name, type, attributes)
     */
    static final Field<?>[] SIGNATURE = new Field[]{
            new VarCharField("name",
                    ColumnAttributes.builder().maxLength(42).notNull().primary().build()),
            new VarCharField("password",
                    ColumnAttributes.builder().maxLength(64).notNull().build()),
            new VarCharField("email",
                    ColumnAttributes.builder().maxLength(256).notNull().index().unique().build())
    };

    public UserSpec() {
        super(TABLE_NAME, User.class);
    }

    @Override
    public Field<?>[] getSignature() {
        return SIGNATURE;
    }

    @Override
    public Object[] convertModelToArray(User model) {
        return new Object[]{model.name(), model.password(), model.email()};
    }

    @Override
    public User convertArrayToModel(Object[] fieldValues) {
        return new User((String) fieldValues[0], (String) fieldValues[1], (String) fieldValues[2]);
    }
}
