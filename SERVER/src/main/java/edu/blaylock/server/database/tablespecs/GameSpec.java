package edu.blaylock.server.database.tablespecs;

import chess.ChessGame;
import edu.blaylock.chess.GameState;
import edu.blaylock.server.database.fields.*;
import edu.blaylock.server.database.models.Game;

/**
 * Info on the Game table in database (name and formattings)
 * Converts from database types to java model
 */
public class GameSpec extends TableSpec<Game> {
    /**
     * Name of table in database
     */
    static final String TABLE_NAME = "blaylock_game";

    /**
     * Fields in table (name, type, attributes)
     */
    static Field<?>[] SIGNATURE = new Field<?>[]{
            new IntField("gameID", ColumnAttributes.builder().primary().autoIncrement().build()),
            new VarCharField("whiteUsername", 42),
            new VarCharField("blackUsername", 42),
            new VarCharField("gameName", ColumnAttributes.builder().maxLength(42).notNull().build()),
            new GameField("game"),
            new EnumField<>("state", GameState.class)
    };

    public GameSpec() {
        super(TABLE_NAME, Game.class);
    }

    @Override
    public Field<?>[] getSignature() {
        return SIGNATURE;
    }

    @Override
    public Object[] convertModelToArray(Game model) {
        return new Object[]{model.gameID(), model.whiteUsername(),
                model.blackUsername(), model.gameName(), model.game(), model.state()};
    }

    @Override
    public Game convertArrayToModel(Object[] fieldValues) {
        return new Game((Integer) fieldValues[0], (String) fieldValues[1], (String) fieldValues[2],
                (String) fieldValues[3], (ChessGame) fieldValues[4], (GameState) fieldValues[5]);
    }
}
