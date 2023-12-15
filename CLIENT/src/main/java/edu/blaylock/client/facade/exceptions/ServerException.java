package edu.blaylock.client.facade.exceptions;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

/**
 * Holds Http response for a non 200 statusCode
 */
public class ServerException extends Exception {

    int statusCode;
    String message;

    public ServerException(HttpURLConnection connection) throws IOException {
        super();
        this.statusCode = connection.getResponseCode();
        try (InputStream respBody = connection.getErrorStream()) {
            InputStreamReader inputStreamReader = new InputStreamReader(respBody);
            ErrorMessage message = new Gson().fromJson(inputStreamReader, ErrorMessage.class);
            this.message = message.message();
        }
    }

    public int statusCode() {
        return statusCode;
    }

    public String message() {
        return message;
    }

    private record ErrorMessage(String message) {
    }
}
