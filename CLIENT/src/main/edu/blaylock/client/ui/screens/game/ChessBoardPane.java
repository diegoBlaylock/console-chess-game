package edu.blaylock.client.ui.screens.game;

import chess.*;
import edu.blaylock.chess.impl.ChessPositionImpl;
import edu.blaylock.client.ui.Color;
import edu.blaylock.client.ui.Graphics;
import edu.blaylock.client.ui.GraphicsOptions;
import edu.blaylock.client.ui.components.base.Component;

import java.util.Collection;
import java.util.HashSet;

/**
 * Display Chessboard. Allows for multiple selections (select and addSelection) as well as highlighting cells. Can be
 * flipped.
 */
public class ChessBoardPane extends Component {
    private static final Color[] BOARD_COLORS = new Color[]{new Color(232, 221, 177), new Color(170, 141, 94)};
    private static final Color[] PIECE_COLORS = new Color[]{new Color(0, 0, 0), new Color(106, 81, 219),};
    private static final Color[] BORDER_COLORS = new Color[]{new Color(82, 65, 38), new Color(255, 255, 255)};
    private static final Color[] HIGHLIGHT_COLORS = new Color[]{new Color(191, 255, 236), new Color(136, 219, 195), new Color(94, 224, 122)};
    private static final String COLUMNS = "abcdefgh";
    private static final String INDICES = "87654321";
    private static final String tiles = "    ████    ████    ████    ████";
    private static final String betwixt = "▄▄▄▄▀▀▀▀▄▄▄▄▀▀▀▀▄▄▄▄▀▀▀▀▄▄▄▄▀▀▀▀";

    private ChessBoard board;
    private boolean flipped;

    private final Collection<ChessPosition> highlights = new HashSet<>();
    private final Collection<ChessPosition> selections = new HashSet<>();

    public ChessBoardPane(ChessBoard board, boolean flipped) {
        resize(38, 20);
        this.board = board;
        this.flipped = flipped;
    }

    /**
     * Set a new chessboard to display. Will invalidate this component
     *
     * @param board new board
     */
    public void setBoard(ChessBoard board) {
        synchronized (PAINT_LOCK) {
            this.board = board;
            this.invalidate();
        }
    }

    /**
     * Flip the view (rotate 180)
     */
    public void flip() {
        synchronized (PAINT_LOCK) {
            flipped = !flipped;
            invalidate();
        }
    }


    /**
     * Clear selection and set to the position
     *
     * @param position new selection
     */
    public void select(ChessPosition position) {
        synchronized (PAINT_LOCK) {
            selections.clear();
            if (position != null) selections.add(position);
        }
    }

    /**
     * Add a selection to the previous selections
     *
     * @param position selection to add
     */
    public void addSelection(ChessPosition position) {
        synchronized (PAINT_LOCK) {
            selections.add(position);
        }
    }

    /**
     * If empty, clear all highlights, otherwise set selection to starting position of the first move and highlight
     * all end positions.
     *
     * @param moves moves to highlight
     */
    public void highlightMoves(Collection<ChessMove> moves) {
        synchronized (PAINT_LOCK) {
            highlights.clear();
            if (!moves.isEmpty()) {
                selections.clear();
                highlights.addAll(moves.stream().map(ChessMove::getEndPosition).toList());
                selections.add(moves.iterator().next().getStartPosition());
            }
        }
    }

    /**
     * paint the board, highlights, and then the pieces
     *
     * @param graphics Graphics object to use
     */
    @Override
    public void paintComponent(Graphics graphics) {
        paintBoard(graphics);
        paintHighlights(graphics);
        paintPieces(graphics);
        graphics.reset();
    }

    private void paintBoard(Graphics graphics) {
        String column = (flipped) ? reverse(COLUMNS) : COLUMNS;
        String indices = (flipped) ? reverse(INDICES) : INDICES;

        column = String.format(" %c  ".repeat(8), (Object[]) column.chars().mapToObj(c -> (char) c).toArray(Character[]::new));
        indices = String.format(" %c \n   \n".repeat(8), (Object[]) indices.chars().mapToObj(c -> (char) c).toArray(Character[]::new));
        indices = "   \n   \n" + indices + "   ";

        graphics.setBackground(BORDER_COLORS[0]);
        graphics.setForeground(BORDER_COLORS[1]);
        graphics.drawString(3, 0, column);
        graphics.drawString(3, 18, column);
        graphics.drawString(0, 0, indices);
        graphics.drawString(35, 0, indices);

        for (int i = 0; i < 8; i++) {
            graphics.setForeground(BOARD_COLORS[i % 2]);
            graphics.drawString(i * 4 + 3, 1, "▄▄▄▄");
            graphics.setForeground(BOARD_COLORS[(i + 1) % 2]);
            graphics.drawString(i * 4 + 3, 17, "▀▀▀▀");
        }

        for (int i = 0; i < 8; i++) {
            graphics.setForeground(BOARD_COLORS[(i + 1) % 2]);
            graphics.setBackground(BOARD_COLORS[i % 2]);
            //graphics.setAttributes(BOARD_COLORS[i % 2], BOARD_COLORS[(i + 1) % 2 + 2]);
            graphics.drawString(3, 2 + 2 * i, tiles);
            if (i < 7) graphics.drawString(3, 3 + 2 * i, betwixt);
        }
    }

    private void paintHighlights(Graphics graphics) {
        for (ChessPosition position : highlights) {
            fillCell(graphics, position);
        }

        for (ChessPosition position : selections) {
            drawCell(graphics, new Color(255, 255, 255), position);
        }
    }

    /**
     * Paint all pieces
     *
     * @param graphics graphics
     */
    private void paintPieces(Graphics graphics) {
        graphics.setAttributes(GraphicsOptions.BOLD);
        graphics.setForeground(PIECE_COLORS[0]);
        for (ChessPosition position : board.pieceLocationIterator(ChessGame.TeamColor.BLACK)) {
            graphics.setBackground(getColor(position.getRow(), position.getColumn()));
            ChessPosition updatedPosition = reverseIfFlipped(position);
            graphics.drawString(4 * updatedPosition.getColumn(), 2 * (9 - updatedPosition.getRow()), getPiece(board.getPiece(position).getPieceType()));
        }
        graphics.setForeground(PIECE_COLORS[1]);
        for (ChessPosition position : board.pieceLocationIterator(ChessGame.TeamColor.WHITE)) {
            graphics.setBackground(getColor(position.getRow(), position.getColumn()));
            ChessPosition updatedPosition = reverseIfFlipped(position);
            graphics.drawString(4 * updatedPosition.getColumn(), 2 * (9 - updatedPosition.getRow()), getPiece(board.getPiece(position).getPieceType()));
        }
        graphics.setAttributes(GraphicsOptions.NORMAL_INTENSITY);
    }

    /**
     * draw a border around a cell
     *
     * @param graphics graphics
     * @param color    color
     * @param position position
     */
    private void drawCell(Graphics graphics, Color color, ChessPosition position) {
        ChessPosition updatedPosition = reverseIfFlipped(position);
        graphics.setBackground(getColor(position.getRow() + (flipped ? -1 : 1), position.getColumn()));
        graphics.setForeground(color);
        graphics.drawString(4 * updatedPosition.getColumn() - 1, 2 * (9 - updatedPosition.getRow()) - 1, "▄▄▄▄");
        graphics.setBackground(getColor(position.getRow(), position.getColumn()));
        graphics.drawString(4 * updatedPosition.getColumn() - 1, 2 * (9 - updatedPosition.getRow()), "█  █");
        graphics.setBackground(getColor(position.getRow() - (flipped ? -1 : 1), position.getColumn()));
        graphics.drawString(4 * updatedPosition.getColumn() - 1, 2 * (9 - updatedPosition.getRow()) + 1, "▀▀▀▀");
    }

    /**
     * Fill a cell completly with the appropriate color (cf. getColor)
     *
     * @param graphics graphics
     * @param position position
     */
    private void fillCell(Graphics graphics, ChessPosition position) {
        ChessPosition updatedPosition = reverseIfFlipped(position);
        graphics.setBackground(getColor(position.getRow() + (flipped ? -1 : 1), position.getColumn()));
        graphics.setForeground(getColor(position.getRow(), position.getColumn()));
        graphics.drawString(4 * updatedPosition.getColumn() - 1, 2 * (9 - updatedPosition.getRow()) - 1, "▄▄▄▄");
        graphics.drawString(4 * updatedPosition.getColumn() - 1, 2 * (9 - updatedPosition.getRow()), "████");
        graphics.setBackground(getColor(position.getRow() - (flipped ? -1 : 1), position.getColumn()));
        graphics.drawString(4 * updatedPosition.getColumn() - 1, 2 * (9 - updatedPosition.getRow()) + 1, "▀▀▀▀");
    }


    /**
     * Simple reverse utility
     *
     * @param string string
     * @return reversed string
     */
    private String reverse(String string) {
        char[] result = new char[string.length()];

        for (int i = string.length() - 1; i >= 0; i--) {
            result[i] = string.charAt(7 - i);
        }

        return new String(result);
    }

    /**
     * Will reverse a position to match how the board is flipped
     *
     * @param position position
     * @return flipped/standard position
     */
    private ChessPosition reverseIfFlipped(ChessPosition position) {
        if (!flipped) return position;

        return new ChessPositionImpl(9 - position.getRow(), 9 - position.getColumn());
    }

    /**
     * Return the Representation of a piece
     *
     * @param piece piece type
     * @return piece representation
     */
    String getPiece(ChessPiece.PieceType piece) {
        return switch (piece) {
            case KING -> "K";
            case PAWN -> "P";
            case ROOK -> "R";
            case KNIGHT -> "N";
            case BISHOP -> "B";
            case QUEEN -> "Q";
        };
    }

    /**
     * Returns the color of a cell depending on if it is at the border, one of the inner cells, or highlighted.
     *
     * @param row row
     * @param col col
     * @return Color
     */
    private Color getColor(int row, int col) {
        ChessPosition position = new ChessPositionImpl(row, col);
        if (position.getRow() <= 0 || position.getRow() > 8 || position.getColumn() <= 0 || position.getColumn() > 8) {
            return BORDER_COLORS[0];
        } else if (highlights.contains(position)) {
            return HIGHLIGHT_COLORS[(position.getRow() + position.getColumn() + 1) % 2];
        } else {
            return BOARD_COLORS[(row + col + 1) % 2];
        }
    }
}
