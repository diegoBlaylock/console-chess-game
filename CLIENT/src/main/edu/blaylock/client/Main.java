package edu.blaylock.client;

import edu.blaylock.client.facade.ServerFacade;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main {

    public static final String SERVER_ADDRESS = "http://localhost:8080";
    public static final String SERVER_WS_ADDRESS = "ws://localhost:8080/connect";

    public static final Client CLIENT = new Client();
    public static final ServerFacade SERVER = initFacade();
    public static final Logger LOG;

    static {
        LogManager.getLogManager().reset();
        LOG = Logger.getLogger(Main.class.getName());
        try {
            LOG.addHandler(new FileHandler("out.log"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        // Load a custom c library for winapi for the Terminal.jar file
        System.load("C:\\Users\\diego\\Desktop\\CHESS\\resources\\temp_c.dll");
        CLIENT.runClient();
    }

    static ServerFacade initFacade() {
        try {
            return new ServerFacade(SERVER_ADDRESS);
        } catch (URISyntaxException e) {

            System.err.println("FAILED TO CONNECT TO ADDRESS");
            System.exit(-1);
        }
        return null;
    }
}
