package edu.blaylock.server.services;

/**
 * Miscenllaneous utils used by many request classes
 */
public class RequestUtils {

    /**
     * Checks a list of string to see if any are null or empty
     *
     * @param strings list of string to check
     * @return whether any paramaters are empty or null
     */
    public static boolean areAnyEmpty(String... strings) {
        for (String current : strings) {
            if (current == null || current.isEmpty()) return true;
        }
        return false;
    }
}
