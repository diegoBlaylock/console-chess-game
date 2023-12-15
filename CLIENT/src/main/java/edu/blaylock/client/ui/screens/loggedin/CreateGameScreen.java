package edu.blaylock.client.ui.screens.loggedin;

import edu.blaylock.client.Main;
import edu.blaylock.client.facade.exceptions.ConnectionException;
import edu.blaylock.client.facade.exceptions.ServerException;
import edu.blaylock.client.facade.responses.CreateGameResponse;
import edu.blaylock.client.ui.PaneManager;
import edu.blaylock.client.ui.UIUtils;
import edu.blaylock.client.ui.components.base.*;
import edu.blaylock.client.ui.components.custom.EscapeWrapper;
import edu.blaylock.client.ui.components.custom.Footer;
import edu.blaylock.client.ui.components.focusmanagers.ArrowFocusManager;

/**
 * Inputs to call create game on remote service
 */
public class CreateGameScreen extends Pane {
    TextField gameName;

    Pane createPane;

    Label footer = new Footer();

    public CreateGameScreen() {
        super();
        size(-1, -1);
        translate(0, 0);

        createPane = new Pane();
        createPane.size(40, 6);
        createPane.translate(-1, -1);

        addTitle(createPane);
        addSelections(createPane);
        addSubmitButton(createPane);

        addComponent(footer);

        setFocusManager(focusManager);
        addComponent(createPane);
    }

    ArrowFocusManager focusManager = new ArrowFocusManager(1, 1);

    void addTitle(Pane pane) {
        Label title = getTitle();
        pane.addComponent(title);
    }

    private static Label getTitle() {
        Label title = new Label("Create New Game");
        title.shrink();
        title.offset(-1, 4);
        return title;
    }

    void addSelections(Container container) {
        gameName = UIUtils.getTextField("Game Name: ", 6, false, container);

        focusManager.addComponent(0, 0, gameName);
    }

    void addSubmitButton(Container container) {
        Button button = new Button("Create", this::submit, UIUtils.Justify.CENTER);
        button.offset(-1, 10);
        button.size(42, 3);
        container.addComponent(button);
        focusManager.addComponent(1, 0, button);
    }

    void submit() {
        footer.setText("Creating Game...");
        try {
            footer.setText("Game Created! Use join game to join with this ID");

            CreateGameResponse response = Main.SERVER.createGame(gameName.getText());
            PaneManager.swapLastComponent(new EscapeWrapper(new GameIdResultScreen(response.gameID())));
        } catch (ServerException e) {
            footer.setText("❌ " + e.message());
        } catch (ConnectionException e) {
            footer.setText(UIUtils.CONNECTION_ERROR);
        }
        invalidate();
    }
}
