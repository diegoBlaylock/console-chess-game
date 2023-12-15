package edu.blaylock.chess.impl;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

public class ChessMoveImpl implements ChessMove {
    ChessPosition beginning;
    ChessPosition ending;
    ChessPiece.PieceType promotionPiece;

    public ChessMoveImpl(ChessPosition beginning, ChessPosition ending, ChessPiece.PieceType promotionPiece) {
        this.beginning = beginning;
        this.ending = ending;
        this.promotionPiece = promotionPiece;
    }

    @Override
    public ChessPosition getStartPosition() {
        return beginning;
    }

    @Override
    public ChessPosition getEndPosition() {
        return ending;
    }

    @Override
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    @Override
    public int hashCode() {
        return 101 * beginning.hashCode() + ending.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ChessMove move) {
            return beginning.equals(move.getStartPosition()) && ending.equals(move.getEndPosition());
        }
        return false;
    }

    @Override
    public String toString() {
        return beginning.toString() + "->" + ending.toString() + ":" + this.promotionPiece;
    }
}
