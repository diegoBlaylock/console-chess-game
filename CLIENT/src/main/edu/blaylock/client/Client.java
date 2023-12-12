package edu.blaylock.client;

import edu.blaylock.client.ui.PaneManager;
import edu.blaylock.client.ui.components.base.Component;
import edu.blaylock.client.ui.components.base.Wrapper;
import edu.blaylock.client.ui.components.custom.EscapeWrapper;
import edu.blaylock.client.ui.screens.HelpScreen;
import edu.blaylock.client.ui.screens.game.GameScreen;
import edu.blaylock.client.ui.screens.loggedin.LoggedInScreen;
import edu.blaylock.client.ui.screens.loggedout.LoggedOutScreen;
import edu.blaylock.jni.flags.ConsoleMode;
import edu.blaylock.terminal.Terminal;
import edu.blaylock.terminal.events.KeyEvent;
import edu.blaylock.terminal.events.Record;
import edu.blaylock.terminal.events.listeners.KeyListener;

import java.util.logging.Level;

import static edu.blaylock.terminal.events.KeyCode.CTRL;
import static edu.blaylock.terminal.events.KeyCode.VK_H;

public class Client {

    /**
     * UI state meant to not be mutually exclusive. On the game help screen all three are true
     */
    public static class State {
        public static final int LOGGED_IN = 0b10;
        public static final int GAME = 0b1;
        public static final int HELP = 0b100;
    }

    private long tick = 0;
    private Throwable exitException;
    private final KeyListener helpListener = this::helpKeyHandler;

    /**
     * Used for animation (In this version, only for the erasing label
     *
     * @return current tick
     */
    public long tick() {
        return tick;
    }

    /**
     * Returns the UI state as a bit flag combination of LOGGED_IN, HELP, and GAME
     *
     * @return bit vector
     */
    public int state() {
        int out = 0;
        for (Component comp : PaneManager.getComponents()) {
            if (comp instanceof LoggedInScreen) out |= State.LOGGED_IN;
            else if (comp instanceof Wrapper wrapper) {
                if (wrapper.getComponent() instanceof HelpScreen) out |= State.HELP;
                else if (wrapper.getComponent() instanceof GameScreen) out |= State.GAME;
            }
        }

        return out;
    }

    public void runClient() {
        Terminal.dispatcher.start();
        Terminal.dispatcher.disable();
        onStartUp();
        Runtime.getRuntime().addShutdownHook(new Thread(this::onShutdown));
        tickLoop();
    }

    /**
     * Simple fixed timed loop consisting of dispatching input events, rendering, and sleeping
     */
    void tickLoop() {
        Terminal.dispatcher.enable();
        try {
            while (true) {
                long start = System.currentTimeMillis();
                Main.LOG.log(Level.INFO, "DISPATCHING");
                Terminal.dispatcher.dispatchEvents();
                Main.LOG.log(Level.INFO, "RENDERING");
                PaneManager.render();
                Main.LOG.log(Level.INFO, "SLEEPING");
                Thread.sleep(Math.max(0, 50 + start - System.currentTimeMillis()));
                tick++;
            }
        } catch (Exception e) {
            exitException = e;
        }
    }

    /**
     * Tasks for setting up
     */
    void onStartUp() {
        PaneManager.pushComponent(new LoggedOutScreen());
        int new_mode = ConsoleMode.setFlags(Terminal.getInstance().in.getConsoleMode(), ConsoleMode.DISABLE_NEWLINE_AUTO_RETURN, ConsoleMode.ENABLE_WINDOW_INPUT);
        new_mode = ConsoleMode.unsetFlags(new_mode, ConsoleMode.ENABLE_ECHO_INPUT);
        Terminal.getInstance().in.setConsoleMode(new_mode);
        Terminal.getInstance().out.setConsoleMode(ConsoleMode.setFlags(Terminal.getInstance().out.getConsoleMode(), ConsoleMode.ENABLE_VIRTUAL_TERMINAL_PROCESSING));
        Terminal.getInstance().out.print_flush("\u001b[?1049h");
        Terminal.getInstance().out.print_flush("\u001b[?25l");
        Terminal.dispatcher.addListener(Record.KEY_EVENT, helpListener);
    }

    /**
     * Task for closing Down (Registered by JVM shutdown hook)
     */
    void onShutdown() {
        System.out.flush();
        Terminal.getInstance().out.flush();
        Terminal.getInstance().out.print_flush("\u001b[?1049l");
        Terminal.getInstance().out.print_flush("\u001b[?25h");
        Terminal.getInstance().out.print_flush("\u001b[0m");
        Terminal.resetModes();
        if (exitException != null) {
            exitException.printStackTrace();
        }

    }

    /**
     * Saves the exception from another thread and exits. Prints out the exception
     *
     * @param e throwable
     */
    public void exception(Throwable e) {
        this.exitException = e;
        System.exit(-1);
    }


    void helpKeyHandler(KeyEvent event) {
        int state = state();
        if ((state & State.HELP) != 0 || event.keyDown) return;

        if (event.virtualKeyCode == VK_H && (event.controlKeyState & CTRL) != 0) {
            PaneManager.pushComponent(new EscapeWrapper(new HelpScreen()));
        }
    }

}
