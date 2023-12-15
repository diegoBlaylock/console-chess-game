package edu.blaylock.utils.gson.adapters;

import chess.ChessPosition;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import edu.blaylock.chess.impl.ChessPositionImpl;

import java.lang.reflect.Type;

public class ChessPositionAdapter implements JsonDeserializer<ChessPosition> {
    @Override
    public ChessPosition deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        return context.deserialize(jsonElement, ChessPositionImpl.class);
    }

}
