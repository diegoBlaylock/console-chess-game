package edu.blaylock.utils.gson.adapters;

import chess.ChessBoard;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import edu.blaylock.chess.impl.ChessBoardFactory;

import java.io.IOException;

/**
 * Note for the TAs: You may be wondering how this is used, the MySqlDatabase relies on a table spec. If you
 * Look at GameSpec, it will list the columns as fields, the last being GameField. This class contains the way
 * to serialize and deserialize the ChessGame which uses this adapter for the board.
 */
public class ChessBoardAdapter extends TypeAdapter<ChessBoard> {

    @Override
    public void write(JsonWriter jsonWriter, ChessBoard chessBoard) throws IOException {
        if (chessBoard == null) jsonWriter.nullValue();
        else jsonWriter.value(chessBoard.serialize());
    }

    @Override
    public ChessBoard read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) return null;
        String data = jsonReader.nextString();
        return ChessBoardFactory.buildFromBytes(data);
    }
}
