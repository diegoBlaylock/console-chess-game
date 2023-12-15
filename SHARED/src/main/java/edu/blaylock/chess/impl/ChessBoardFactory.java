package edu.blaylock.chess.impl;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import edu.blaylock.misc.ResourceManager;

import java.nio.ByteBuffer;
import java.util.Base64;

public class ChessBoardFactory {
    protected static final ChessBoard DEFAULT_CHESS_BOARD = buildFromString(ResourceManager.DEFAULT_BOARD_CONFIG);

    public static ChessBoard defaultChessBoard() {

        return DEFAULT_CHESS_BOARD;
    }

    public static ChessBoard buildFromString(String string) {
        ChessBoard result = new ChessBoardImpl();

        int row = 0, col = 0;

        for (char c : string.toCharArray()) {
            if (c == '\n') {
                row++;
                col = 0;
                continue;
            }

            ChessGame.TeamColor color = Character.isLowerCase(c) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            ChessPiece.PieceType type;

            type = switch (Character.toLowerCase(c)) {
                case 'k' -> ChessPiece.PieceType.KING;
                case 'q' -> ChessPiece.PieceType.QUEEN;
                case 'b' -> ChessPiece.PieceType.BISHOP;
                case 'n' -> ChessPiece.PieceType.KNIGHT;
                case 'r' -> ChessPiece.PieceType.ROOK;
                case 'p' -> ChessPiece.PieceType.PAWN;
                default -> null;
            };

            if (type != null) {
                result.addPiece(new ChessPositionImpl(8 - row, col + 1), new ChessPieceImpl(type, color));
            }

            col++;
        }

        return result;
    }

    /**
     * Expected Bytes:<br>
     * next 2 bytes - current round<br>
     * Read until 64 pieces read:<br
     * &emsp;if -1 read <br>
     * &emsp;&emsp;if next byte -1 finish board<br>
     * &emsp;&emsp;else skip next byte number of columns<br>
     * &emsp;else read piece<br>
     *
     * @param string String encoded in Base64
     * @return Chessboard
     */
    public static ChessBoard buildFromBytes(String string) {
        ByteBuffer buffer = ByteBuffer.wrap(Base64.getDecoder().decode(string));

        ChessBoardImpl board = new ChessBoardImpl();
        board.updateRound(buffer.getShort());

        int row = 0;
        int col = 0;
        while (row < 8 && buffer.hasRemaining()) {
            byte next = buffer.get();

            if (next == -1) {
                byte numNull = buffer.get();
                if (numNull == -1) break;
                assert numNull > 0 : "Problem deserializing Board";
                col += numNull;
                row += col / 8;
                col %= 8;
            } else {
                ChessPieceImpl piece = deserializeChessPiece(next, buffer.getShort(), buffer.getShort());
                board.addPiece(new ChessPositionImpl(row + 1, col + 1), piece);

                col++;
                if (col >= 8) {
                    col = 0;
                    row++;
                }
            }
        }

        return board;
    }

    /**
     * Return ChessPiece from data
     *
     * @param next       byte, first bit determines color, subsequent bits determine type. Both based on ordinal of enums
     * @param movesTaken How many moves where taken by piece
     * @param lastRound  The number of last round this piece was used
     * @return Populated Chesspiece
     */
    private static ChessPieceImpl deserializeChessPiece(byte next, short movesTaken, short lastRound) {
        int color_index = 0x1 & next;
        int type_index = next >>> 1;
        ChessGame.TeamColor color = ChessGame.TeamColor.values()[color_index];
        ChessPiece.PieceType type = ChessPiece.PieceType.values()[type_index];

        ChessPieceImpl piece = new ChessPieceImpl(type, color, movesTaken);
        piece.updateTimeOfLastMove(lastRound);
        return piece;
    }
}
