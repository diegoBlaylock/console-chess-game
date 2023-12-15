package edu.blaylock.server.database.models;

/**
 * Model describing a single user
 *
 * @param name     String name
 * @param password String password
 * @param email    String email
 */
public record User(String name, String password, String email) implements IModel {

    @Override
    public boolean equals(Object o) {
        if (o instanceof User user) {
            return this.name.equals(user.name);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}

