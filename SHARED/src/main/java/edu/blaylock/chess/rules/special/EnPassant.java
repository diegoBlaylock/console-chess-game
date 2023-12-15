package edu.blaylock.chess.rules.special;

import chess.*;
import edu.blaylock.chess.impl.ChessMoveImpl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class EnPassant implements ISpecialRule {
    @Override
    public Collection<ChessMove> getValidMoves(ChessBoard board, ChessPosition position) {
        Set<ChessMove> moves = new HashSet<ChessMove>();
        ChessPiece start = board.getPiece(position);
        if (start == null || start.getPieceType() != ChessPiece.PieceType.PAWN) {
            return moves;
        }

        int row_change = (start.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;

        ChessPosition left = position.offset(0, -1);
        ChessPosition right = position.offset(0, 1);

        ChessPiece test;
        if (right != null) {
            if ((test = board.getPiece(right)) != null && test.getTeamColor() != start.getTeamColor()
                    && test.getPieceType() == ChessPiece.PieceType.PAWN && hasPawnJustDoubleMoved(board, right)) {
                moves.add(new ChessMoveImpl(position, right.offset(row_change, 0), null));
            }
        }
        if (left != null) {
            if ((test = board.getPiece(left)) != null && test.getTeamColor() != start.getTeamColor()
                    && test.getPieceType() == ChessPiece.PieceType.PAWN && hasPawnJustDoubleMoved(board, left)) {
                moves.add(new ChessMoveImpl(position, left.offset(row_change, 0), null));
            }
        }

        return moves;
    }

    @Override
    public ChessPiece updateBoard(ChessBoard board, ChessPosition position) {
        ChessPiece start = board.getPiece(position);
        int row_change = (start.getTeamColor() == ChessGame.TeamColor.WHITE) ? -1 : 1;
        ChessPosition target = position.offset(row_change, 0);
        ChessPiece piece = board.getPiece(target);
        board.deletePieceAt(target);
        return piece;
    }

    private static boolean hasPawnJustDoubleMoved(ChessBoard board, ChessPosition position) {
        ChessPiece piece = board.getPiece(position);
        if (board.getRound() - 1 != piece.getTimeOfLastMove() || piece.numMovesTaken() != 1) return false;

        int row_check = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 4 : 5;
        return position.getRow() == row_check;
    }
}
