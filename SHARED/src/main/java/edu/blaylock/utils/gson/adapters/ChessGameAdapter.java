package edu.blaylock.utils.gson.adapters;

import chess.ChessGame;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import edu.blaylock.chess.impl.ChessGameImpl;

import java.lang.reflect.Type;


public class ChessGameAdapter implements JsonDeserializer<ChessGame> {
    @Override
    public ChessGame deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        return context.deserialize(jsonElement, ChessGameImpl.class);
    }
}
