package edu.blaylock.chess.rules.special;

import chess.*;
import edu.blaylock.chess.impl.ChessGameImpl;
import edu.blaylock.chess.impl.ChessMoveImpl;
import edu.blaylock.chess.impl.ChessPositionImpl;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Castling implements ISpecialRule {
    @Override
    public Collection<ChessMove> getValidMoves(ChessBoard board, ChessPosition position) {
        Set<ChessMove> result = new HashSet<ChessMove>();

        ChessPiece piece = board.getPiece(position);

        if (piece == null || piece.getPieceType() != ChessPiece.PieceType.KING
                || piece.numMovesTaken() > 0 || ChessGameImpl.isInCheck(piece.getTeamColor(), board)) return result;

        ChessPosition right = new ChessPositionImpl(position.getRow(), 8);
        ChessPosition left = new ChessPositionImpl(position.getRow(), 1);
        ChessPiece rookRight = board.getPiece(right);
        ChessPiece rookLeft = board.getPiece(left);

        if (rookRight != null && rookRight.getPieceType() == ChessPiece.PieceType.ROOK && rookRight.numMovesTaken() == 0) {
            if (areNoPiecesInBetween(board, position, right)) {
                result.add(new ChessMoveImpl(position, new ChessPositionImpl(position.getRow(), 7), null));
            }
        }

        if (rookLeft != null && rookLeft.getPieceType() == ChessPiece.PieceType.ROOK && rookLeft.numMovesTaken() == 0) {
            if (areNoPiecesInBetween(board, left, position)) {
                result.add(new ChessMoveImpl(position, new ChessPositionImpl(position.getRow(), 3), null));
            }
        }

        filterMoves(result, board, piece.getTeamColor());
        return result;
    }

    @Override
    public ChessPiece updateBoard(ChessBoard board, ChessPosition position) {
        int oldColumn = (position.getColumn() == 7) ? 8 : 1;
        int newColumn = (position.getColumn() == 7) ? 6 : 4;

        return board.movePiece(new ChessMoveImpl(
                new ChessPositionImpl(position.getRow(), oldColumn),
                new ChessPositionImpl(position.getRow(), newColumn),
                null));
    }

    private static boolean areNoPiecesInBetween(ChessBoard board, ChessPosition left, ChessPosition right) {
        if (left.getColumn() >= right.getColumn()) return false;
        ChessPosition track = left.offset(0, 1);
        while (!track.equals(right)) {
            if (board.getPiece(track) != null) {
                return false;
            }
            track = track.offset(0, 1);
        }

        return true;
    }

    private static void filterMoves(Collection<ChessMove> moves, ChessBoard chessBoard, ChessGame.TeamColor color) {

        Collection<ChessPosition> positionsInDanger = new HashSet<ChessPosition>();
        chessBoard.pieceLocationIterator(color.next()).forEach((chessPosition) -> {
            chessBoard.getPiece(chessPosition).pieceMoves(chessBoard, chessPosition).forEach(
                    (chessMove) -> positionsInDanger.add(chessMove.getEndPosition()));
        });

        Iterator<ChessMove> iter = moves.iterator();

        while (iter.hasNext()) {
            ChessMove move = iter.next();
            int shift = (move.getStartPosition().getColumn() < move.getEndPosition().getColumn()) ? 1 : -1;
            for (int i = move.getStartPosition().getColumn() + shift; i != move.getEndPosition().getColumn(); i += shift) {
                if (positionsInDanger.contains(new ChessPositionImpl(move.getStartPosition().getRow(), i))) {
                    iter.remove();
                    break;
                }
            }
        }
    }

}
