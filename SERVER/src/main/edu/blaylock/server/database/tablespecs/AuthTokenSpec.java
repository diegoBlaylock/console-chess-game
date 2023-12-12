package edu.blaylock.server.database.tablespecs;

import edu.blaylock.server.database.fields.ColumnAttributes;
import edu.blaylock.server.database.fields.Field;
import edu.blaylock.server.database.fields.VarCharField;
import edu.blaylock.server.database.models.AuthToken;

/**
 * Info on the AuthToken table in database (name and formattings)
 * Converts from database types to java model
 */
public class AuthTokenSpec extends TableSpec<AuthToken> {
    /**
     * Name of table in database
     */
    static final String TABLE_NAME = "blaylock_authtoken";

    /**
     * Fields in table (name, type, attributes)
     */
    static final Field<?>[] SIGNATURE = new Field[]{
            new VarCharField("authToken",
                    ColumnAttributes.builder().maxLength(36).notNull().primary().build()),
            new VarCharField("username",
                    ColumnAttributes.builder().maxLength(42).notNull().index().unique().build())};

    public AuthTokenSpec() {
        super(TABLE_NAME, AuthToken.class);
    }

    @Override
    public Field<?>[] getSignature() {
        return SIGNATURE;
    }

    @Override
    public Object[] convertModelToArray(AuthToken model) {
        return new Object[]{model.authToken(), model.username()};
    }

    @Override
    public AuthToken convertArrayToModel(Object[] fieldValues) {
        return new AuthToken((String) fieldValues[0], (String) fieldValues[1]);
    }

}
