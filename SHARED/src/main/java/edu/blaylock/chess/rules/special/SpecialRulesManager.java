package edu.blaylock.chess.rules.special;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class SpecialRulesManager {

    private static final Collection<ISpecialRule> rules = new ArrayList<ISpecialRule>();

    static {
        rules.add(new EnPassant());
        rules.add(new Castling());
    }

    public static void addValidRules(ChessBoard chessBoard, ChessPosition position, Collection<ChessMove> collection) {
        for (ISpecialRule rule : rules) {
            collection.addAll(rule.getValidMoves(chessBoard, position));
        }
    }

    public static ISpecialRule getPertinentRule(ChessBoard chessBoard, ChessMove chessMove) {
        for (ISpecialRule rule : rules) {
            Collection<ChessMove> validMoves = rule.getValidMoves(chessBoard, chessMove.getStartPosition());

            if (validMoves.contains(chessMove)) return rule;
        }
        return null;
    }
}
