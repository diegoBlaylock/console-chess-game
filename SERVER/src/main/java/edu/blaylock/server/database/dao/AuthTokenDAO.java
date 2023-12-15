package edu.blaylock.server.database.dao;

import edu.blaylock.server.database.implementations.IDatabase;
import edu.blaylock.server.database.models.AuthToken;
import edu.blaylock.server.database.tablespecs.AuthTokenSpec;
import edu.blaylock.server.database.tablespecs.TableSpec;
import edu.blaylock.server.exceptions.DataAccessException;

/**
 * DAO object describing the following table: <br/>
 * Name - "blaylock_authtoken"<br/>
 * Fields (Name:DBType:JavaType) -<br/>
 * &emsp;("authToken":String:String), ("username":String:String)<br/>
 * Conversion - AuthToken to/from Object[2]
 */
public class AuthTokenDAO extends DAO<AuthToken> {
    /**
     * Table description
     */
    public static final TableSpec<AuthToken> SPECIFICATION = new AuthTokenSpec();

    /**
     * Create new DAO for AuthTokan with given Database/Connection
     *
     * @param database IDatabase to call
     */
    public AuthTokenDAO(IDatabase database) throws DataAccessException {
        super(database);
    }

    /**
     * Create DAO with global database
     *
     * @throws DataAccessException Error accessing database
     */
    public AuthTokenDAO() throws DataAccessException {
        super();
    }

    /**
     * Return AuthToken from string token from database. Null if it doesn't exist
     *
     * @param token Token to search for
     * @return AuthToken if the model exists, null otherwise
     * @throws DataAccessException Database error
     */
    public AuthToken getAuthToken(String token) throws DataAccessException {
        AuthToken[] models = findModelsByAttribute("authToken", token);
        if (models.length == 0) return null;
        return models[0];
    }

    /**
     * If an AuthToken with the username already exists, update the token, otherwise create one
     *
     * @param authToken token
     * @throws DataAccessException Database error
     */
    public void updateOrCreate(AuthToken authToken) throws DataAccessException {
        if (findModelsByAttribute("username", authToken.username()).length > 0)
            updateFieldByFieldAttribute("username", authToken.username(), "authToken", authToken.authToken());
        else
            create(authToken);
    }

    @Override
    public TableSpec<AuthToken> getSpecification() {
        return SPECIFICATION;
    }
}
