package edu.blaylock.chess.rules;

import chess.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MoveValidator {

    private static final Map<ChessPiece.PieceType, PieceValidator> jumpTable = new HashMap<ChessPiece.PieceType, PieceValidator>();

    static {
        jumpTable.put(ChessPiece.PieceType.KING, MoveValidator::addValidKingMoves);
        jumpTable.put(ChessPiece.PieceType.QUEEN, MoveValidator::addValidQueenMoves);
        jumpTable.put(ChessPiece.PieceType.BISHOP, MoveValidator::addValidBishopMoves);
        jumpTable.put(ChessPiece.PieceType.PAWN, MoveValidator::addValidPawnMoves);
        jumpTable.put(ChessPiece.PieceType.ROOK, MoveValidator::addValidRookMoves);
        jumpTable.put(ChessPiece.PieceType.KNIGHT, MoveValidator::addValidKnightMoves);
    }

    public static void addValidMoves(ChessGame.TeamColor color, ChessBoard chessBoard, ChessPosition position,
                                     Collection<ChessMove> moves) {
        ChessPiece start = chessBoard.getPiece(position);
        if (start == null) return;

        jumpTable.get(start.getPieceType()).consume(color, chessBoard, position, moves);
    }

    private static void addValidKingMoves(ChessGame.TeamColor color, ChessBoard chessBoard, ChessPosition position,
                                          Collection<ChessMove> moves) {
        RuleUtils.addValidLine(moves, chessBoard, position, 0, 1, 1);
        RuleUtils.addValidLine(moves, chessBoard, position, 1, 1, 1);
        RuleUtils.addValidLine(moves, chessBoard, position, 1, 0, 1);
        RuleUtils.addValidLine(moves, chessBoard, position, 0, -1, 1);
        RuleUtils.addValidLine(moves, chessBoard, position, -1, -1, 1);
        RuleUtils.addValidLine(moves, chessBoard, position, -1, 0, 1);
        RuleUtils.addValidLine(moves, chessBoard, position, 1, -1, 1);
        RuleUtils.addValidLine(moves, chessBoard, position, -1, 1, 1);
    }

    private static void addValidQueenMoves(ChessGame.TeamColor color, ChessBoard chessBoard, ChessPosition position,
                                           Collection<ChessMove> moves) {
        RuleUtils.addValidLine(moves, chessBoard, position, 0, 1);
        RuleUtils.addValidLine(moves, chessBoard, position, 1, 1);
        RuleUtils.addValidLine(moves, chessBoard, position, 1, 0);
        RuleUtils.addValidLine(moves, chessBoard, position, 0, -1);
        RuleUtils.addValidLine(moves, chessBoard, position, -1, -1);
        RuleUtils.addValidLine(moves, chessBoard, position, -1, 0);
        RuleUtils.addValidLine(moves, chessBoard, position, 1, -1);
        RuleUtils.addValidLine(moves, chessBoard, position, -1, 1);
    }

    private static void addValidBishopMoves(ChessGame.TeamColor color, ChessBoard chessBoard, ChessPosition position,
                                            Collection<ChessMove> moves) {
        RuleUtils.addValidLine(moves, chessBoard, position, 1, 1);
        RuleUtils.addValidLine(moves, chessBoard, position, -1, -1);
        RuleUtils.addValidLine(moves, chessBoard, position, 1, -1);
        RuleUtils.addValidLine(moves, chessBoard, position, -1, 1);
    }

    private static void addValidPawnMoves(ChessGame.TeamColor color, ChessBoard chessBoard, ChessPosition position,
                                          Collection<ChessMove> moves) {
        ChessPiece start = chessBoard.getPiece(position);

        int changeY;
        int dist = 1;
        if (color == ChessGame.TeamColor.WHITE) {
            changeY = 1;
            if (start.numMovesTaken() == 0 && position.getRow() == 2) dist = 2;
        } else {
            changeY = -1;
            if (start.numMovesTaken() == 0 && position.getRow() == 7) dist = 2;
        }

        RuleUtils.addValidLine(moves, chessBoard, position, 0, changeY, dist, RuleUtils.PAWN_MOVE);
        RuleUtils.addValidLine(moves, chessBoard, position, -1, changeY, 1, RuleUtils.PAWN_ATTACK);
        RuleUtils.addValidLine(moves, chessBoard, position, 1, changeY, 1, RuleUtils.PAWN_ATTACK);
    }

    private static void addValidRookMoves(ChessGame.TeamColor color, ChessBoard chessBoard, ChessPosition position,
                                          Collection<ChessMove> moves) {
        RuleUtils.addValidLine(moves, chessBoard, position, 0, 1);
        RuleUtils.addValidLine(moves, chessBoard, position, 0, -1);
        RuleUtils.addValidLine(moves, chessBoard, position, 1, 0);
        RuleUtils.addValidLine(moves, chessBoard, position, -1, 0);
    }

    private static void addValidKnightMoves(ChessGame.TeamColor color, ChessBoard chessBoard, ChessPosition position,
                                            Collection<ChessMove> moves) {
        RuleUtils.addValidLine(moves, chessBoard, position, 1, 2, 1);
        RuleUtils.addValidLine(moves, chessBoard, position, -1, 2, 1);
        RuleUtils.addValidLine(moves, chessBoard, position, 1, -2, 1);
        RuleUtils.addValidLine(moves, chessBoard, position, -1, -2, 1);
        RuleUtils.addValidLine(moves, chessBoard, position, 2, 1, 1);
        RuleUtils.addValidLine(moves, chessBoard, position, -2, 1, 1);
        RuleUtils.addValidLine(moves, chessBoard, position, 2, -1, 1);
        RuleUtils.addValidLine(moves, chessBoard, position, -2, -1, 1);
    }

    private interface PieceValidator {
        void consume(ChessGame.TeamColor color, ChessBoard chessBoard, ChessPosition position,
                     Collection<ChessMove> moves);
    }
}
