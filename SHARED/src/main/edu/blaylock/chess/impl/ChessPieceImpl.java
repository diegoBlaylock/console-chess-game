package edu.blaylock.chess.impl;

import chess.*;
import edu.blaylock.chess.rules.MoveValidator;

import java.util.Collection;
import java.util.HashSet;

public class ChessPieceImpl implements ChessPiece {

    final private ChessPiece.PieceType type;
    final private ChessGame.TeamColor teamColor;

    private int stepsTaken = 0;
    private int roundOfLastMove = 0;

    public ChessPieceImpl(ChessPiece.PieceType type, ChessGame.TeamColor teamColor) {
        this.type = type;
        this.teamColor = teamColor;
    }

    public ChessPieceImpl(ChessPiece.PieceType type, ChessGame.TeamColor teamColor, int startingSteps) {
        this.type = type;
        this.teamColor = teamColor;
        this.stepsTaken = startingSteps;
    }

    @Override
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    @Override
    public ChessPiece.PieceType getPieceType() {
        return type;
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> potentialMoves = new HashSet<ChessMove>();
        MoveValidator.addValidMoves(getTeamColor(), board, myPosition, potentialMoves);
        return potentialMoves;
    }

    @Override
    public int numMovesTaken() {
        return stepsTaken;
    }

    public void incrementMovesTaken() {
        ++stepsTaken;
    }

    @Override
    public int hashCode() {
        return 57 * teamColor.ordinal() + 91 * type.ordinal() + stepsTaken;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChessPiece piece) {
            return piece.getPieceType() == type
                    && piece.getTeamColor() == teamColor
                    && piece.numMovesTaken() == stepsTaken;
        }
        return false;
    }

    @Override
    public String toString() {
        String result =
                switch (getPieceType()) {
                    case KING -> "K";
                    case PAWN -> "P";
                    case QUEEN -> "Q";
                    case BISHOP -> "B";
                    case ROOK -> "R";
                    case KNIGHT -> "N";
                };
        String colorCode = (getTeamColor() == ChessGame.TeamColor.WHITE) ? "\033[97m" : "\033[95m";
        return colorCode + result + "\033[0m";

    }

    @Override
    public ChessPiece copy() {
        ChessPiece piece = new ChessPieceImpl(type, teamColor, stepsTaken);
        piece.updateTimeOfLastMove(getTimeOfLastMove());
        return piece;
    }

    @Override
    public int getTimeOfLastMove() {
        return roundOfLastMove;
    }

    @Override
    public void updateTimeOfLastMove(int round) {
        roundOfLastMove = round;
    }
}
