package edu.blaylock.client.ui.screens.loggedin;

import edu.blaylock.client.Main;
import edu.blaylock.client.facade.exceptions.ConnectionException;
import edu.blaylock.client.facade.exceptions.ServerException;
import edu.blaylock.client.ui.PaneManager;
import edu.blaylock.client.ui.UIUtils;
import edu.blaylock.client.ui.components.base.Label;
import edu.blaylock.client.ui.components.base.Pane;
import edu.blaylock.client.ui.components.custom.EscapeWrapper;
import edu.blaylock.client.ui.components.custom.Footer;
import edu.blaylock.client.ui.components.custom.Selector;
import edu.blaylock.client.ui.screens.HelpScreen;
import edu.blaylock.client.ui.screens.loggedout.LoggedOutScreen;
import edu.blaylock.utils.Status;

/**
 * Manages all actions when someone is logged in.
 */
public class LoggedInScreen extends Pane {
    private final Footer footer = new Footer();

    public LoggedInScreen() {
        super();
        resize(-1, -1);
        addTitle();
        addSelections();
        addComponent(footer);
    }

    private void addTitle() {
        Label title = getTitle();
        addComponent(title);
    }

    private static Label getTitle() {
        Label title = new Label(
                """
                         ______     __  __     ______     ______     ______
                        /\\  ___\\   /\\ \\_\\ \\   /\\  ___\\   /\\  ___\\   /\\  ___\\
                        \\ \\ \\____  \\ \\  __ \\  \\ \\  __\\   \\ \\___  \\  \\ \\___  \\
                         \\ \\_____\\  \\ \\_\\ \\_\\  \\ \\_____\\  \\/\\_____\\  \\/\\_____\\
                          \\/_____/   \\/_/\\/_/   \\/_____/   \\/_____/   \\/_____/"""
        );

        title.shrink();
        title.offset(-1, 0);
        return title;
    }

    private void addSelections() {
        Selector selector = new Selector(UIUtils.Justify.CENTER);
        selector.resize(30, 18);
        selector.offset(-1, -1);
        selector.addSelection("Join Game", () -> PaneManager.pushComponent(new EscapeWrapper(new JoinGameScreen())));
        selector.addSelection("Observe Game", () -> PaneManager.pushComponent(new EscapeWrapper(new ObserverGameScreen())));
        selector.addSelection("List Games", () -> PaneManager.pushComponent(new EscapeWrapper(new ListGamesScreen())));
        selector.addSelection("Create Game", () -> PaneManager.pushComponent(new EscapeWrapper(new CreateGameScreen())));
        selector.addSelection("Help", () -> PaneManager.pushComponent(new EscapeWrapper(new HelpScreen())));
        selector.addSelection("Logout", () -> {
            try {
                Main.SERVER.logout();
                logout();
            } catch (ServerException e) {
                if (e.statusCode() == Status.UNAUTHORIZED) {
                    logout();
                } else {
                    footer.setText(e.message());
                }
            } catch (ConnectionException e) {
                footer.setText(UIUtils.CONNECTION_ERROR);
            }
        });

        addComponent(selector);
    }

    private void logout() {
        PaneManager.swapAllComponent(new LoggedOutScreen());
    }
}
