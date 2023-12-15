package edu.blaylock.utils.gson.adapters;

import chess.ChessMove;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import edu.blaylock.chess.impl.ChessMoveImpl;

import java.lang.reflect.Type;

public class ChessMoveAdapter implements JsonDeserializer<ChessMove> {
    @Override
    public ChessMove deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        return context.deserialize(jsonElement, ChessMoveImpl.class);
    }
}
