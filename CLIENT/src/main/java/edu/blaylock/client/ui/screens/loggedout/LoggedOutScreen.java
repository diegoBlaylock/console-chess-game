package edu.blaylock.client.ui.screens.loggedout;

import edu.blaylock.client.ui.PaneManager;
import edu.blaylock.client.ui.UIUtils;
import edu.blaylock.client.ui.components.base.Label;
import edu.blaylock.client.ui.components.base.Pane;
import edu.blaylock.client.ui.components.custom.EscapeWrapper;
import edu.blaylock.client.ui.components.custom.Footer;
import edu.blaylock.client.ui.components.custom.Selector;
import edu.blaylock.client.ui.screens.HelpScreen;

/**
 * Display options for loggedout users
 */
public class LoggedOutScreen extends Pane {

    public LoggedOutScreen() {
        super();
        resize(-1, -1);
        addTitle();
        addSelections();
        Footer footer = new Footer();
        addComponent(footer);
    }

    void addTitle() {
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

    void addSelections() {
        Selector selector = new Selector(UIUtils.Justify.CENTER);
        selector.resize(30, 12);
        selector.offset(-1, 10);
        selector.addSelection("Login", () -> PaneManager.pushComponent(new EscapeWrapper(new LoginScreen())));
        selector.addSelection("Register", () -> PaneManager.pushComponent(new EscapeWrapper(new RegisterScreen())));
        selector.addSelection("Help", () -> PaneManager.pushComponent(new EscapeWrapper(new HelpScreen())));
        selector.addSelection("Quit", () -> System.exit(-1));

        addComponent(selector);
    }
}
