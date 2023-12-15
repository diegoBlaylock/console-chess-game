package edu.blaylock.chess.rules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import edu.blaylock.chess.impl.ChessMoveImpl;

import java.util.Collection;

public class RuleUtils {

    public static final PiecePredicate STANDARD = (ChessPiece originator, ChessPiece destination)
            -> destination == null || originator.getTeamColor() != destination.getTeamColor();

    public static final PiecePredicate PAWN_ATTACK = (ChessPiece originator, ChessPiece destination)
            -> destination != null && originator.getTeamColor() != destination.getTeamColor();

    public static final PiecePredicate PAWN_MOVE = (ChessPiece originator, ChessPiece destination)
            -> destination == null;

    public static void addValidLine(Collection<ChessMove> position, ChessBoard chessBoard, ChessPosition start,
                                    int changeX, int changeY) {
        addValidLine(position, chessBoard, start, changeX, changeY, -1, STANDARD);
    }

    public static void addValidLine(Collection<ChessMove> position, ChessBoard chessBoard, ChessPosition start,
                                    int changeX, int changeY, int dist) {
        addValidLine(position, chessBoard, start, changeX, changeY, dist, STANDARD);
    }

    public static void addValidLine(Collection<ChessMove> position, ChessBoard chessBoard, ChessPosition start,
                                    int changeX, int changeY, int max_steps, PiecePredicate valid) {
        if (changeX == 0 && changeY == 0) return;

        boolean shouldStop = false;
        int numStepsTaken = 0;

        ChessPiece original = chessBoard.getPiece(start);
        ChessPosition destination = start;
        while (!shouldStop) {
            destination = destination.offset(changeY, changeX);
            if (destination == null) break;
            numStepsTaken += 1;
            ChessPiece test = chessBoard.getPiece(destination);

            if (valid.consume(original, test)) {
                ChessMove move = new ChessMoveImpl(start, destination, null);
                position.add(move);
            }
            if (test != null || (max_steps == numStepsTaken)) {
                shouldStop = true;
            }
        }
    }

    public static interface PiecePredicate {
        public boolean consume(ChessPiece originator, ChessPiece destination);
    }
}
