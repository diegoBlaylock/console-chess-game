package edu.blaylock.client.ui.screens.loggedin;

import edu.blaylock.client.ui.UIUtils;
import edu.blaylock.client.ui.components.base.Label;
import edu.blaylock.client.ui.components.base.Pane;
import edu.blaylock.client.ui.components.custom.Footer;

/**
 * Used after a game is created to display the created game id
 */
public class GameIdResultScreen extends Pane {
    Pane responsePane;

    Label footer = new Footer("Game Created! Use join game to join with this ID");

    public GameIdResultScreen(int gameID) {
        super();
        size(-1, -1);
        translate(0, 0);

        responsePane = new Pane();
        responsePane.size(40, 20);
        responsePane.translate(-1, -1);

        Label title = new Label("Game Created.");
        Label gameIDLabel = new Label(String.format("Assigned Game ID: %d", gameID), UIUtils.Justify.CENTER);
        title.translate(-1, 0);
        gameIDLabel.translate(-1, 2);
        title.shrink();
        gameIDLabel.size(-1, 1);

        addComponent(footer);
        responsePane.addComponent(title);
        responsePane.addComponent(gameIDLabel);
        addComponent(responsePane);
    }
}
