package edu.blaylock.chess.rules.special;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;

public interface ISpecialRule {

    Collection<ChessMove> getValidMoves(ChessBoard board, ChessPosition position);

    ChessPiece updateBoard(ChessBoard board, ChessPosition position);
}
