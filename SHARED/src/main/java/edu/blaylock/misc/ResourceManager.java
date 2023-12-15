package edu.blaylock.misc;

import java.io.*;
import java.util.Objects;
import java.util.stream.Collectors;

public class ResourceManager {
    public static String DEFAULT_BOARD_CONFIG = loadText("/default_board.txt");

    public static void loadDll(String path) throws IOException {
        InputStream in = ResourceManager.class.getResourceAsStream(path);
        assert in != null;

        File temp = File.createTempFile("temp", ".dll");
        temp.deleteOnExit();
        FileOutputStream fos = new FileOutputStream(temp);

        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            fos.write(buffer, 0, read);
        }
        fos.close();
        in.close();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.load(temp.getAbsolutePath());
    }

    public static String loadText(String path) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(ResourceManager.class.getResourceAsStream(path))))) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            System.err.println("Can't find resource: " + path);
            System.exit(-1);
        }

        return null;
    }
}
