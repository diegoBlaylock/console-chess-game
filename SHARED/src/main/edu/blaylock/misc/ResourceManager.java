package edu.blaylock.misc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResourceManager {

    public static String DEFAULT_BOARD_CONFIG = loadText("resources/default_board.txt");

    public static String loadText(String path) {
        try {
            return Files.readString(Path.of(path)).replaceAll("\\r", "");
        } catch (IOException e) {
            System.err.println("Can't find resource: " + path);
            System.exit(-1);
        }

        return null;
    }
}
