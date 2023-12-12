package edu.blaylock.chess.impl;

import chess.ChessPosition;

public class ChessPositionImpl implements ChessPosition {

    final private int rowIndex;
    final private int colIndex;

    public ChessPositionImpl(int row, int col) {
        rowIndex = row;
        colIndex = col;
    }

    @Override
    public int getRow() {
        return rowIndex;
    }

    @Override
    public int getColumn() {
        return colIndex;
    }

    @Override
    public ChessPosition offset(int row, int col) {
        int final_row = rowIndex + row;
        int final_col = colIndex + col;

        if (final_row < 1 || final_row > 8 || final_col < 1 || final_col > 8) {
            return null;
        }

        return new ChessPositionImpl(final_row, final_col);
    }

    @Override
    public int hashCode() {
        return rowIndex * 11 + colIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChessPosition position) {
            return rowIndex == position.getRow() && colIndex == position.getColumn();
        }
        return false;
    }

    @Override
    public String toString() {
        return String.valueOf(rowIndex) + String.valueOf((char) ('A' + colIndex - 1));
    }
}
