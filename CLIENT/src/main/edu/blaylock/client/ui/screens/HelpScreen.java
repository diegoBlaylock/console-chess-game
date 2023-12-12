package edu.blaylock.client.ui.screens;

import edu.blaylock.client.Client;
import edu.blaylock.client.Main;
import edu.blaylock.client.ui.UIUtils;
import edu.blaylock.client.ui.components.base.Container;
import edu.blaylock.client.ui.components.base.Label;
import edu.blaylock.client.ui.components.base.Pane;

/**
 * Display all help options depending on ui state
 */
public class HelpScreen extends Pane {
    final static String LOGGED_OUT_HELP_TEXT =
            """
                    This help section can be always reached using Ctrl-H:
                    - CTRL-C : Exit
                    - Use arrows to navigate menus and RETURN to select.
                    - ESC : Return to previous screen
                                            
                    Menu items:
                    - LOGIN: login into your account
                    - REGISTER : Put in info as new user
                    - HELP : Find this page
                    - QUIT : Exit""";
    final static String LOGGED_IN_HELP_TEXT =
            """
                    This help section can be always reached using Ctrl-H:
                    - CTRL-C : Exit
                    - Use arrows to navigate menus and RETURN to select.
                    - ESC : Return to previous screen.
                                            
                    Menu items:
                    - JOIN GAME: Join a game with Game ID, input wanted color.
                    - OBSERVE GAME : Watch a game using Game ID.
                    - CREATE GAME : Create a game with a name. Returns ID of created Game.
                    - LIST GAME : Lists games in database. Use arrows to look switch pages and R to refresh.
                    - LOGOUT : logout and return to logout screen.""";
    final static String GAME_HELP_TEXT =
            """
                    This help section can be always reached using Ctrl-H:
                    - CTRL-C : Exit
                    - CTRL-R : Refresh Game Board
                    - CTRL-D : Unselect when selecting a piece
                    - ESC : Return to Login screen.
                                        
                    Game Notifications are shown at the top
                                            
                    Actions(Players):
                    - Make Move : Use arrows to select beginning piece
                                  and press enter to confirm. Then use
                                  arrows to select destination and press
                                  enter to make move.
                    - Resign : End the game immediately
                                        
                    Actions(Observers):
                    - Rotate : Rotate the board to view the game from both perspectives
                                        
                    Actions(All):
                    - Highlight Moves : Use arrows to select any piece and view
                                        Available moves. Ctrl-D to exit selection.
                    - Help : Bring up this screen
                    - Leave : leave the game, return to login""";


    public HelpScreen() {
        translate(-1, -1);
        size(-1, -1);
        Pane center = new Pane();
        center.size(60, 60);
        center.translate(-1, 5);
        addTitle(center);
        addText(center);

        addComponent(center);
    }

    public void addTitle(Container container) {
        Label title = new Label("Help Section");
        title.shrink();
        title.translate(0, 0);
        container.addComponent(title);
    }

    public void addText(Container container) {
        Label text = new Label(getHelpText(), UIUtils.Justify.LEFT);
        text.shrink();
        text.translate(2, 1);
        container.addComponent(text);
    }

    public static String getHelpText() {
        int state = Main.CLIENT.state();
        if ((state & Client.State.GAME) != 0) return GAME_HELP_TEXT;
        else if ((state & Client.State.LOGGED_IN) != 0) return LOGGED_IN_HELP_TEXT;
        else return LOGGED_OUT_HELP_TEXT;
    }


}
