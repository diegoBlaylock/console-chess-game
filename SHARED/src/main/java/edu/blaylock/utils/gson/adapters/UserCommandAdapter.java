package edu.blaylock.utils.gson.adapters;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import webSocketMessages.userCommands.UserGameCommand;

import java.lang.reflect.Type;

/**
 * Will deserialize a UserCommand based on the classes bound to the CommandType enum
 */
public class UserCommandAdapter implements JsonDeserializer<UserGameCommand> {
    @Override
    public UserGameCommand deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        UserGameCommand.CommandType commandType = jsonDeserializationContext.deserialize(
                jsonElement.getAsJsonObject().get("commandType"), UserGameCommand.CommandType.class);
        return jsonDeserializationContext.deserialize(jsonElement, commandType.commandClass());
    }
}
