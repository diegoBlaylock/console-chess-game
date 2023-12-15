package edu.blaylock.utils.gson.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import webSocketMessages.serverMessages.ServerMessage;

import java.lang.reflect.Type;

/**
 * Will deserialize a ServerMessage based on the classes bound to the MessageType enum
 */
public class ServerMessageAdapter implements JsonDeserializer<ServerMessage> {
    @Override
    public ServerMessage deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        ServerMessage.ServerMessageType messageType = jsonDeserializationContext.deserialize(
                jsonElement.getAsJsonObject().get("serverMessageType"), ServerMessage.ServerMessageType.class);
        return jsonDeserializationContext.deserialize(jsonElement, messageType.messageClass());
    }
}
