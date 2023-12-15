package edu.blaylock.client.ui.screens.loggedout;

import edu.blaylock.client.Main;
import edu.blaylock.client.facade.exceptions.ConnectionException;
import edu.blaylock.client.facade.exceptions.ServerException;
import edu.blaylock.client.ui.PaneManager;
import edu.blaylock.client.ui.UIUtils;
import edu.blaylock.client.ui.components.base.*;
import edu.blaylock.client.ui.components.custom.Footer;
import edu.blaylock.client.ui.components.focusmanagers.ArrowFocusManager;
import edu.blaylock.client.ui.screens.loggedin.LoggedInScreen;

/**
 * Input to login and will switch to Loggedin if successful
 */
public class LoginScreen extends Pane {
    TextField name;
    TextField password;
    Label footer = new Footer();

    public LoginScreen() {
        super();
        size(-1, -1);

        Pane pane = new Pane();
        pane.size(40, 32);
        pane.translate(-1, -1);

        addTitle(pane);
        addSelections(pane);
        addSubmitButton(pane);

        addComponent(footer);
        addComponent(pane);

        setFocusManager(focusManager);

    }

    ArrowFocusManager focusManager = new ArrowFocusManager(1, 1, 1);

    void addTitle(Pane pane) {
        Label title = getTitle();
        pane.addComponent(title);
    }

    private static Label getTitle() {
        Label title = new Label("Login");
        title.shrink();
        title.offset(-1, 4);
        return title;
    }

    void addSelections(Container container) {
        name = UIUtils.getTextField("Username:", 6, false, container);
        password = UIUtils.getTextField("Password:", 7, true, container);

        focusManager.addComponent(0, 0, name);
        focusManager.addComponent(1, 0, password);
    }

    void addSubmitButton(Container container) {
        Button button = new Button("Login", this::submit, UIUtils.Justify.CENTER);
        button.offset(-1, 9);
        button.size(42, 3);
        container.addComponent(button);
        focusManager.addComponent(2, 0, button);
    }

    void submit() {
        footer.setText("Logging in...");
        try {
            Main.SERVER.login(name.getText(), password.getText());
            PaneManager.swapAllComponent(new LoggedInScreen());
        } catch (ServerException e) {
            footer.setText("❌ " + e.message());
        } catch (ConnectionException e) {
            footer.setText(UIUtils.CONNECTION_ERROR);
        }
        invalidate();

    }

}
