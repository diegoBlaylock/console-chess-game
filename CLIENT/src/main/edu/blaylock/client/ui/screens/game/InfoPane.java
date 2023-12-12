package edu.blaylock.client.ui.screens.game;

import chess.ChessGame;
import edu.blaylock.chess.GameState;
import edu.blaylock.client.ui.components.base.Label;
import edu.blaylock.client.ui.components.base.Pane;

/**
 * Contains all the labels to display info to the screen
 * <ul>
 *     <li>Name: name of Game</li>
 *     <li>State: state game is in</li>
 *     <li>Round: current round in game</li>
 *     <li>Turn: whose turn is it</li>
 *     <li>Color: Color of client</li>
 *     <li>Selection: selected piece (only if selector is enable)</li>
 * </ul>
 */
class InfoPane extends Pane {
    private final Label nameLabel;
    private final Label stateLabel;
    private final Label roundLabel;
    private final Label turnLabel;

    private final Label colorLabel;
    private final Label selectLabel;

    public InfoPane() {
        nameLabel = makeLabel(0);
        colorLabel = makeLabel(2);
        turnLabel = makeLabel(3);
        roundLabel = makeLabel(4);
        stateLabel = makeLabel(5);
        selectLabel = makeLabel(Integer.MIN_VALUE);

        this.addComponent(nameLabel);
        this.addComponent(colorLabel);
        this.addComponent(stateLabel);
        this.addComponent(roundLabel);
        this.addComponent(turnLabel);
        this.addComponent(selectLabel);
    }

    /**
     * Update State shown to player
     *
     * @param state state
     */
    public void setState(GameState state) {
        stateLabel.setText(String.format("State: %s", state));
    }

    /**
     * Update game name shown to player
     *
     * @param name name
     */
    public void setName(String name) {
        nameLabel.setText(name);
    }

    /**
     * Update round shown to player
     *
     * @param round round
     */
    public void setRound(int round) {
        roundLabel.setText(String.format("Round: %d", round + 1));
    }

    /**
     * update turn
     *
     * @param color color
     */
    public void setTurn(ChessGame.TeamColor color) {
        turnLabel.setText(String.format("Turn: %s", color));
    }

    /**
     * Update Color of client
     *
     * @param color color
     */
    public void setColor(String color) {
        colorLabel.setText(String.format("Role: %s", color));
    }

    /**
     * If null passed, nothing is shown, else show passed Selection
     *
     * @param string Selection
     */
    public void setSelection(String string) {
        if (string == null) selectLabel.setText("");
        else selectLabel.setText(String.format("Selection: %s", string));
    }

    private Label makeLabel(int y) {
        Label temp = new Label("");
        temp.translate(0, y);
        temp.size(-1, 1);

        return temp;
    }
}
