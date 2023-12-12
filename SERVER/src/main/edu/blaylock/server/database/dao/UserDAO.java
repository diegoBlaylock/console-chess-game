package edu.blaylock.server.database.dao;

import edu.blaylock.server.database.implementations.IDatabase;
import edu.blaylock.server.database.models.User;
import edu.blaylock.server.database.tablespecs.TableSpec;
import edu.blaylock.server.database.tablespecs.UserSpec;
import edu.blaylock.server.exceptions.DataAccessException;

/**
 * DAO object describing the following table:<br/>
 * Name - "blaylock_user"<br/>
 * Fields (Name:DBType:JavaType) -<br/>
 * &emsp;("name":String:String), ("password":String:String), ("email":String:String)<br/>
 * Conversion - User to/from Object[3]<br/>
 */
public class UserDAO extends DAO<User> {
    /**
     * Table description
     */
    public static final TableSpec<User> SPECIFICATION = new UserSpec();


    /**
     * Create new DAO for User with given Database/Connection
     *
     * @param database IDatabase to call
     */
    public UserDAO(IDatabase database) throws DataAccessException {
        super(database);
    }

    public UserDAO() throws DataAccessException {
        super();
    }

    /**
     * Get User model from database by name
     *
     * @param name username to search for
     * @return Model if found, null otherwise
     * @throws DataAccessException Database error
     */
    public User getUserByName(String name) throws DataAccessException {
        User[] models = findModelsByAttribute("name", name);
        if (models.length == 0) return null;
        return models[0];
    }

    @Override
    public TableSpec<User> getSpecification() {
        return SPECIFICATION;
    }

}
