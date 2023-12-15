package edu.blaylock.client.ui.screens.loggedin;

import edu.blaylock.client.Main;
import edu.blaylock.client.facade.exceptions.ConnectionException;
import edu.blaylock.client.facade.exceptions.ServerException;
import edu.blaylock.client.facade.responses.ListGamesResponse;
import edu.blaylock.client.ui.UIUtils;
import edu.blaylock.client.ui.components.base.Label;
import edu.blaylock.client.ui.components.base.Pane;
import edu.blaylock.client.ui.components.custom.Footer;
import edu.blaylock.terminal.Terminal;
import edu.blaylock.terminal.events.KeyEvent;
import edu.blaylock.terminal.events.Record;
import edu.blaylock.terminal.events.listeners.KeyListener;

import java.util.Arrays;
import java.util.Comparator;

import static edu.blaylock.terminal.events.KeyCode.*;

/**
 * Displays all the games given by the server to the user in a table.
 * Will page them in groups of PAGE_SIZE and page can be selected by arrow keys
 */
public class ListGamesScreen extends Pane {
    static final int PAGE_SIZE = 20;
    private final Footer info = new Footer("Use left and right keys to change page. Press R to refresh view...");
    private Label gameLabel;
    private Label page;
    private ListGamesResponse.GameDescription[] games = null;
    private final KeyListener listener = this::handleKeyEvent;
    private int currentPage = 0;

    public ListGamesScreen() {
        super();
        size(-1, -1);
        translate(0, 0);

        setupTable();
        Terminal.dispatcher.addListener(Record.KEY_EVENT, listener);
        grabInfo();
    }

    private void grabInfo() {
        try {
            games = Main.SERVER.listGames().games();
            Arrays.sort(games, Comparator.comparingInt(ListGamesResponse.GameDescription::gameID));
            currentPage = 0;
            populateTable();

        } catch (ServerException e) {
            info.setText(e.message());
        } catch (ConnectionException e) {
            info.setText(UIUtils.CONNECTION_ERROR);
        }
    }

    private void setupTable() {
        addComponent(info);

        Pane center = new Pane();
        center.size(69, PAGE_SIZE + 5);
        center.translate(-1, -1);
        String lines = "─".repeat(69);
        Label header = new Label(lines + "\n Game ID |   White Username   |   Black Username   |    Game Name    \n" + lines);
        header.size(-1, 3);
        header.translate(0, 0);
        page = new Label("Page");
        page.shrink();
        page.translate(Integer.MIN_VALUE, Integer.MIN_VALUE);
        gameLabel = new Label("");
        gameLabel.size(69, PAGE_SIZE);
        gameLabel.translate(0, 3);
        center.addComponent(header);
        center.addComponent(gameLabel);
        center.addComponent(page);
        addComponent(center);
    }

    private void populateTable() {
        StringBuilder builder = new StringBuilder();
        for (int i = PAGE_SIZE * currentPage; i < PAGE_SIZE * (currentPage + 1) && i < games.length; i++) {
            builder.append(String.format("%9d|%20s|%20s|%17s\n",
                    games[i].gameID(),
                    (games[i].whiteUsername() == null) ? "" : games[i].whiteUsername(),
                    (games[i].blackUsername() == null) ? "" : games[i].blackUsername(),
                    games[i].gameName()));
        }

        gameLabel.setText(builder.toString());

        page.setText(String.format("%s\nPage %d/%d", "─".repeat(69), currentPage + 1, games.length / PAGE_SIZE + 1));
        page.shrink();
    }

    private void handleKeyEvent(KeyEvent keyEvent) {
        if (!getFocus() || keyEvent.keyDown) return;

        if (keyEvent.virtualKeyCode == VK_R) {
            grabInfo();
        } else if (keyEvent.virtualKeyCode == VK_LEFT) {
            if (currentPage > 0) {
                currentPage--;
                populateTable();
            }
        } else if (keyEvent.virtualKeyCode == VK_RIGHT) {
            if (currentPage < games.length / PAGE_SIZE) {
                currentPage++;
                populateTable();
            }
        }
    }
}
