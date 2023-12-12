package edu.blaylock.chess.impl;

import chess.*;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

public class ChessBoardImpl implements ChessBoard {

    private final ChessPiece[][] pieces = new ChessPiece[8][8];

    private final transient Set<ChessPosition> allWhites = new HashSet<>();
    private final transient Set<ChessPosition> allBlacks = new HashSet<>();

    private transient ChessPosition whiteKing = null;
    private transient ChessPosition blackKing = null;

    private int round = 0;

    @Override
    public void addPiece(ChessPosition position, ChessPiece piece) {
        pieces[position.getRow() - 1][position.getColumn() - 1] = piece;

        if (piece == null) return;

        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (piece.getPieceType() == ChessPiece.PieceType.KING) whiteKing = position;
            allWhites.add(position);
        } else if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            if (piece.getPieceType() == ChessPiece.PieceType.KING) blackKing = position;
            allBlacks.add(position);
        }
    }

    @Override
    public ChessPiece getPiece(ChessPosition position) {
        return pieces[position.getRow() - 1][position.getColumn() - 1];
    }

    @Override
    public void resetBoard() {
        for (int i = 0; i < 8; i++) Arrays.fill(pieces[i], null);

        whiteKing = null;
        blackKing = null;

        allBlacks.clear();
        allWhites.clear();

        ChessBoard defaultBoard = ChessBoardFactory.defaultChessBoard();

        for (ChessPosition position : defaultBoard.pieceLocationIterator(ChessGame.TeamColor.WHITE)) {
            addPiece(position, defaultBoard.getPiece(position).copy());
        }

        for (ChessPosition position : defaultBoard.pieceLocationIterator(ChessGame.TeamColor.BLACK)) {
            addPiece(position, defaultBoard.getPiece(position).copy());
        }

    }

    @Override
    public void reloadBoard() {
        for (int i = 0; i < pieces.length; i++) {
            for (int j = 0; j < pieces[0].length; j++) {
                addPiece(new ChessPositionImpl(i, j), pieces[i][j]);
            }
        }
    }

    public void deletePieceAt(ChessPosition position) {
        allBlacks.remove(position);
        allWhites.remove(position);
        addPiece(position, null);
    }

    @Override
    public ChessPiece movePiece(ChessMove move) {
        ChessPiece start = getPiece(move.getStartPosition());
        ChessPiece end = getPiece(move.getEndPosition());

        if (start == null) {
            return null;
        }

        deletePieceAt(move.getStartPosition());
        deletePieceAt(move.getEndPosition());

        start.incrementMovesTaken();

        if (start.getPieceType() == ChessPiece.PieceType.PAWN) {
            if (start.getTeamColor() == ChessGame.TeamColor.WHITE && move.getEndPosition().getRow() == 8
                    || start.getTeamColor() == ChessGame.TeamColor.BLACK && move.getEndPosition().getRow() == 1) {
                start = new ChessPieceImpl(move.getPromotionPiece(), start.getTeamColor(), start.numMovesTaken());
            }
        }
        start.updateTimeOfLastMove(round);
        addPiece(move.getEndPosition(), start);
        return end;
    }

    @Override
    public Iterable<ChessPosition> pieceLocationIterator(ChessGame.TeamColor color) {
        return (color == ChessGame.TeamColor.WHITE) ? allWhites : allBlacks;
    }

    @Override
    public ChessPosition getKingLocation(ChessGame.TeamColor color) {
        return (color == ChessGame.TeamColor.WHITE) ? whiteKing : blackKing;
    }

    @Override
    public ChessBoard copy() {
        ChessBoard copy = new ChessBoardImpl();
        for (ChessPosition position : allWhites) {
            copy.addPiece(position, getPiece(position).copy());
        }

        for (ChessPosition position : allBlacks) {
            copy.addPiece(position, getPiece(position).copy());
        }

        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ChessBoardImpl board) {

            return board.round == round && Arrays.deepEquals(pieces, board.pieces);

        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder build = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            build.append(i + 1).append(" |");
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = getPiece(new ChessPositionImpl(i + 1, j + 1));

                if (piece == null) build.append(' ');
                else build.append(piece.toString());
                build.append("|");
            }
            build.append('\n');
        }
        build.append("   a b c d e f g h\n");

        return build.toString();
    }

    @Override
    public void updateRound(int round) {
        this.round = round;
    }

    @Override
    public int getRound() {
        return round;
    }

    /**
     * See ChessBoardFactory buildFromBytes
     *
     * @return Base64 byte array
     */
    @Override
    public String serialize() {
        ByteBuffer buffer = ByteBuffer.allocate(322);

        buffer.putShort((short) round);

        int numNull = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = pieces[i][j];

                if (piece == null) { // COUNT UP NULLs
                    numNull++;
                    continue;
                }

                if (numNull > 0) { // PUT IN -1 THEN NUMBER NULL
                    buffer.put((byte) -1);
                    buffer.put((byte) numNull);
                    numNull = 0;
                }

                serializePiece(piece, buffer);
            }
        }

        if (numNull > 0) { // END CONDITION
            buffer.put((byte) -1);
            buffer.put((byte) -1);
        }

        byte[] result = new byte[buffer.position()];
        buffer.flip();
        buffer.get(result);

        return Base64.getEncoder().encodeToString(result);
    }

    /**
     * See ChessBoardFactory
     *
     * @param piece piece to serialize
     * @param store ByteBuffer in which to store
     */
    private static void serializePiece(ChessPiece piece, ByteBuffer store) {
        store.put((byte) (piece.getTeamColor().ordinal() | (piece.getPieceType().ordinal() << 1)));
        store.putShort((short) piece.numMovesTaken());
        store.putShort((short) piece.getTimeOfLastMove());
    }
}