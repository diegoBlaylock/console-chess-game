package edu.blaylock.server.database.fields;

import chess.ChessGame;
import edu.blaylock.utils.gson.GsonUtils;

import java.sql.JDBCType;

/**
 * A non-unique field that serializes from ChessGame to String and vice-versa
 */
public class GameField extends Field<ChessGame> {

    /**
     * Create new Field represenation with methods to serialize ChessGame from/to String
     *
     * @param name name of field
     */
    public GameField(String name) {
        super(name, JDBCType.VARCHAR, ColumnAttributes.builder().notNull().maxLength(512).build());
    }

    @Override
    public String serialize(ChessGame deserialized) {
        return GsonUtils.standard().toJson(deserialized);
    }

    @Override
    public ChessGame deserialize(Object serialized) {

        return GsonUtils.standard().fromJson((String) serialized, ChessGame.class);

    }
}
