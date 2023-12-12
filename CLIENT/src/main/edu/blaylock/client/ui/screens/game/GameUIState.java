package edu.blaylock.client.ui.screens.game;

/**
 * Defined states for the UI.
 * <ul>
 *     <li>Turn: Display all options when it is your turn</li>
 *     <li>Not_Turn: Display options for player when it is not their turn</li>
 *     <li>Observe: Options of observers</li>
 *     <li>Ended: Game ended, only display options to leave</li>
 * </ul>
 */
public enum GameUIState {
    TURN,
    NOT_TURN,
    OBSERVE,
    ENDED
}
