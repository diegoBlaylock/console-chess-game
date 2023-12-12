package edu.blaylock.client.ui.screens.game;

import edu.blaylock.client.ui.UIUtils;
import edu.blaylock.client.ui.components.base.Pane;
import edu.blaylock.client.ui.components.custom.Selector;
import edu.blaylock.client.ui.components.focusmanagers.FocusManager;

/**
 * Options to display, this basically holds 4 different selectors for each state and will display and hide the others
 * dependent on state. A list of 6 callbacks for the different actions available are used in the selectors.
 * Lock the options pane (doesn't register keypresses any more) if doing something else
 */
public class OptionsPane extends Pane {
    Selector myTurn = new Selector(UIUtils.Justify.CENTER);
    Selector notTurn = new Selector(UIUtils.Justify.CENTER);
    Selector observer = new Selector(UIUtils.Justify.CENTER);
    Selector ended = new Selector(UIUtils.Justify.CENTER);

    public OptionsPane(Runnable resign, Runnable makeMove, Runnable highlightMove, Runnable rotate, Runnable help, Runnable leave) {
        disable(myTurn);
        disable(notTurn);
        disable(observer);
        disable(ended);

        myTurn.translate(-1, -1);
        myTurn.size(-1, -1);

        notTurn.translate(-1, -1);
        notTurn.size(-1, -1);

        observer.translate(-1, -1);
        observer.size(-1, -1);

        ended.translate(-1, -1);
        ended.size(-1, -1);

        setFocusManager(new FocusManager() {
            @Override
            public void invalidate() {
            }
        });

        myTurn.addSelection("Make Move", makeMove);
        myTurn.addSelection("Highlight Moves", highlightMove);
        myTurn.addSelection("Resign", resign);
        myTurn.addSelection("Help", help);
        myTurn.addSelection("Leave", leave);

        notTurn.addSelection("Highlight Moves", highlightMove);
        notTurn.addSelection("Resign", resign);
        notTurn.addSelection("Help", help);
        notTurn.addSelection("Leave", leave);

        observer.addSelection("Highlight Moves", highlightMove);
        observer.addSelection("Rotate", rotate);
        observer.addSelection("Leave", leave);
        observer.addSelection("Help", help);

        ended.addSelection("Help", help);
        ended.addSelection("Leave", leave);

        addComponent(myTurn);
        addComponent(notTurn);
        addComponent(observer);
        addComponent(ended);
    }

    /**
     * Update the set of options
     *
     * @param state state
     */
    public void setState(GameUIState state) {
        disable(myTurn);
        disable(notTurn);
        disable(observer);
        disable(ended);

        switch (state) {
            case TURN -> enable(myTurn);
            case NOT_TURN -> enable(notTurn);
            case OBSERVE -> enable(observer);
            case ENDED -> enable(ended);
        }
    }

    /**
     * Block all keypresses to the options
     */
    public void lock() {
        myTurn.setLocked(true);
        notTurn.setLocked(true);
        observer.setLocked(true);
        ended.setLocked(true);
    }

    /**
     * Allow all keypresses to the options
     */
    public void unlock() {
        myTurn.setLocked(false);
        notTurn.setLocked(false);
        observer.setLocked(false);
        ended.setLocked(false);
    }

    private void disable(Selector selector) {
        selector.setFocus(false);
        selector.setVisible(false);
    }

    private void enable(Selector selector) {
        selector.setFocus(true);
        selector.setVisible(true);
    }


}
