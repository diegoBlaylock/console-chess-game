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
 * Register new user and login if succesful
 */
public class RegisterScreen extends Pane {
    TextField name;
    TextField email;
    TextField password;

    Label footer = new Footer();

    public RegisterScreen() {
        super();
        size(-1, -1);
        translate(0, 0);

        Pane pane = new Pane();
        pane.size(40, 32);
        pane.translate(-1, -1);

        addTitle(pane);
        addSelections(pane);
        addSubmitButton(pane);

        addComponent(footer);

        setFocusManager(focusManager);

        addComponent(pane);
    }

    ArrowFocusManager focusManager = new ArrowFocusManager(1, 1, 1, 1);

    void addTitle(Pane pane) {
        Label title = getTitle();
        pane.addComponent(title);
    }

    private static Label getTitle() {
        Label title = new Label("Register New User");
        title.shrink();
        title.offset(-1, 4);
        return title;
    }

    void addSelections(Container container) {
        name = UIUtils.getTextField("Username:", 6, false, container);
        email = UIUtils.getTextField("Email:", 7, false, container);
        password = UIUtils.getTextField("Password:", 8, true, container);

        focusManager.addComponent(0, 0, name);
        focusManager.addComponent(1, 0, email);
        focusManager.addComponent(2, 0, password);
    }

    void addSubmitButton(Container container) {
        Button button = new Button("Register", this::submit, UIUtils.Justify.CENTER);
        button.offset(-1, 10);
        button.size(42, 3);
        container.addComponent(button);
        focusManager.addComponent(3, 0, button);
    }

    void submit() {
        footer.setText("Registering User...");
        try {

            Main.SERVER.registerUser(name.getText(), password.getText(), email.getText());
            PaneManager.swapAllComponent(new LoggedInScreen());
        } catch (ServerException e) {
            footer.setText("❌ " + e.message());
        } catch (ConnectionException e) {
            footer.setText(UIUtils.CONNECTION_ERROR);
        }
        invalidate();
    }
}
