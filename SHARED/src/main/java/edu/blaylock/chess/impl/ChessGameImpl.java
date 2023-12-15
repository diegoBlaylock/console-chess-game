package edu.blaylock.chess.impl;

import chess.*;
import edu.blaylock.chess.rules.special.ISpecialRule;
import edu.blaylock.chess.rules.special.SpecialRulesManager;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class ChessGameImpl implements ChessGame {
    TeamColor currentTeam = TeamColor.WHITE;
    ChessBoard chessBoard;

    int round = 0;

    @Override
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    @Override
    public void setTeamTurn(TeamColor team) {
        currentTeam = team;
    }

    public Collection<ChessMove> filterMoves(ChessPosition startPosition, Collection<ChessMove> validMoves) {
        ChessBoard testBoard;
        ChessPiece startPiece = chessBoard.getPiece(startPosition);

        Iterator<ChessMove> iter = validMoves.iterator();
        while (iter.hasNext()) {
            ChessMove move = iter.next();
            testBoard = chessBoard.copy();
            testBoard.movePiece(move);
            if (testBoard.getPiece(move.getEndPosition()).getPieceType() == null) {
                ChessPiece queen = new ChessPieceImpl(ChessPiece.PieceType.QUEEN, startPiece.getTeamColor());
                testBoard.addPiece(move.getEndPosition(), queen);
            }

            if (ChessGameImpl.isInCheck(startPiece.getTeamColor(), testBoard)) {
                iter.remove();
            }
        }

        return validMoves;
    }

    @Override
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (startPosition == null || chessBoard.getPiece(startPosition) == null) return Collections.emptySet();
        Collection<ChessMove> validMoves = chessBoard.getPiece(startPosition).pieceMoves(chessBoard, startPosition);
        SpecialRulesManager.addValidRules(chessBoard, startPosition, validMoves);
        return filterMoves(startPosition, validMoves);
    }


    @Override
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece startPiece = chessBoard.getPiece(move.getStartPosition());

        if (startPiece == null || startPiece.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("Incorrect starting position");
        }

        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (!validMoves.contains(move) ||
                move.getEndPosition().equals(chessBoard.getKingLocation(getTeamTurn().next()))) {
            throw new InvalidMoveException("Invalid move chosen for piece");
        }
        ISpecialRule specialRule = SpecialRulesManager.getPertinentRule(chessBoard, move);

        ChessPiece killed = chessBoard.movePiece(move);

        if (specialRule != null) {
            specialRule.updateBoard(chessBoard, move.getEndPosition());
        }

        setTeamTurn(TeamColor.getNext(getTeamTurn()));
        round++;
        chessBoard.updateRound(round);
    }

    @Override
    public boolean shouldPromotionOccur(ChessMove move) {
        ChessPiece piece = getBoard().getPiece(move.getStartPosition());
        if (piece == null || piece.getPieceType() != ChessPiece.PieceType.PAWN) return false;

        if (piece.getTeamColor() == TeamColor.BLACK)
            return move.getEndPosition().getRow() == 1;
        else
            return move.getEndPosition().getRow() == 8;
    }

    @Override
    public boolean isInCheck(TeamColor teamColor) {

        return ChessGameImpl.isInCheck(teamColor, chessBoard);
    }

    public static boolean isInCheck(TeamColor teamColor, ChessBoard board) {

        ChessPosition king_location = board.getKingLocation(teamColor);

        for (ChessPosition location : board.pieceLocationIterator(teamColor.next())) {
            ChessPiece piece = board.getPiece(location);

            for (ChessMove move : piece.pieceMoves(board, location)) {
                if (move.getEndPosition().equals(king_location)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInStalemate(teamColor) && isInCheck(teamColor);
    }

    @Override
    public boolean isInStalemate(TeamColor teamColor) {
        for (ChessPosition position : chessBoard.pieceLocationIterator(teamColor)) {
            if (!validMoves(position).isEmpty()) return false;
        }

        return true;
    }

    @Override
    public void setBoard(ChessBoard board) {
        chessBoard = board;
    }

    @Override
    public ChessBoard getBoard() {
        return chessBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ChessGameImpl game) {

            return game.round == round && ((chessBoard != null && chessBoard.equals(game.chessBoard)) || game.chessBoard == null);

        }
        return false;
    }

}
