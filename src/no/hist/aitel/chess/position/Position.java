/*
 * Position.java
 *
 */

package no.hist.aitel.chess.position;

import java.io.Serializable;
import no.hist.aitel.chess.board.Board;
import no.hist.aitel.chess.piece.IllegalTypeException;
import no.hist.aitel.chess.piece.Piece;
import static no.hist.aitel.chess.piece.PieceConstants.*;

/**
 *
 * @author martin
 */

public class Position implements Serializable {

    /**
     * Board object to validate positions on
     */
    private Board board;

    /**
     * From and to position, and the difference between them
     */
    private int from, to, diff;

    /**
     * Boolean which controls enPassant
     */
    private boolean enPassant;


    /**
     * Creates a position object for a board which is used to validate moves
     * @param board
     */
    public Position(Board board) {
        this.board = board;
    }

    /**
     * Sets the from and to positions
     * @param from
     * @param to
     */
    public void setPositions(int from, int to) {
        this.from = from;
        this.to = to;
        this.diff = to - from;
    }

    /**
     * Check if enPassant is possible (pawn moved two fields forward from initial postiion)
     * @return True if possible and false otherwise
     */
    public boolean getEnPassant() {
        return enPassant;
    }

    /**
     * Verifies positions
     * @param simulated
     * @throws IllegalPositionException
     */
    public void verifyPositions(boolean simulated) throws IllegalPositionException {

        // Check if any of the positions are outside the board
        if ((from < 0 || from > 63) || (to < 0 || from > 63)) {
            throw new IllegalPositionException("Can't move pieces outside of the board.\n"
                    + "\nFrom: " + from
                    + "\nTo: " + to);
        }
        
        if (!simulated) {
            enPassant = false;
        }

        // Get destionation pieces
        Piece fromPiece = board.getPiece(from);
        Piece toPiece = board.getPiece(to);

        // Can't capture fromPiece of same color
        if (fromPiece.getColor() == toPiece.getColor()) {
            throw new IllegalPositionException("Can't capture piece of same color.\n" +
                    "Type: " + fromPiece.getType() +
                    "\nFrom: " + from +
                    "\nTo: " + to);
        }

        // Piece type
        int type = fromPiece.getType();

        // Direction
        int direction = getDirection(type);

        // Check if path is clear, not checking for type == 2 (Knight) since it can jump over pieces
        if (type != KNIGHT && !isValidPath(direction)) {
            throw new IllegalPositionException("A piece is blocking my path.\n" +
                    "Type: " + fromPiece.getType() +
                    "\nFrom: " + from +
                    "\nTo: " + to);
        }

        // Type specific rules
        switch (type) {
            case PAWN: {
                if (toPiece.isEmpty()) { // New position is empty, pawn can then only move forward
                    if (fromPiece.getColor() == 0) { // White fromPiece
                        if (from >= 8 && from <= 15) { // If the pawn is in its original position, it can move 1 or 2 fields forward
                            if (diff != 8 && diff != 16) {
                                throw new IllegalPositionException("Pawn can only move one or two fields forward when in initial position.\n" +
                                        "Type: " + fromPiece.getType() +
                                        "\nFrom: " + from +
                                        "\nTo: " + to);
                            }
                            if (!simulated && diff == 16) {
                                enPassant = true;
                            }
                        } else if (diff != 8) { // Pawn can always move 1 field forward
                            throw new IllegalPositionException("Pawn can only move one field forward when not in initial position.\n" +
                                    "Type: " + fromPiece.getType() +
                                    "\nFrom: " + from +
                                    "\nTo: " + to);
                        }
                    } else if (fromPiece.getColor() == 1) { // Black fromPiece
                        if (from >= 48 && from <= 55) { // Same as above
                            if (diff != -8 && diff != -16) {
                                throw new IllegalPositionException("Pawn can only move one or two fields forward when in initial position.\n" +
                                        "Type: " + fromPiece.getType() +
                                        "\nFrom: " + from +
                                        "\nTo: " + to);
                            }
                            if (!simulated && diff == -16) {
                                enPassant = true;
                            }
                        } else if (diff != -8) {
                            throw new IllegalPositionException("Pawn can only move one field forward when not in initial position.\n" +
                                    "Type: " + fromPiece.getType() +
                                    "\nFrom: " + from +
                                    "\nTo: " + to);
                        }
                    }
                } else {
                    if (fromPiece.getColor() == 0 && diff != 9 && diff != 7) {
                        throw new IllegalPositionException("Pawn can't move forward because field isn't empty.\n" +
                                "Type: " + fromPiece.getType() +
                                "\nFrom: " + from +
                                "\nTo: " + to);
                    } else if (fromPiece.getColor() == 1 && diff != -9 && diff != -7) {
                        throw new IllegalPositionException("Pawn can't move forward because field isn't empty.\n" +
                                "Type: " + fromPiece.getType() +
                                "\nFrom: " + from +
                                "\nTo: " + to);
                    }
                }
                break;
            }
            case BISHOP: {
                if ((diff % 7 == 0 || diff % 9 == 0) && getFieldColor(to) == getFieldColor(from)) {
                    break;
                } else {
                    throw new IllegalPositionException("Bishop can only move diagonally.\n" +
                            "Type: " + fromPiece.getType() +
                            "\nFrom: " + from +
                            "\nTo: " + to);
                }
            }
            case KNIGHT: {
                switch (diff) {
                    case -10:
                    case -17:
                    case -15:
                    case -6:
                    case 6:
                    case 10:
                    case 15:
                    case 17: {
                        break;
                    }
                    default: {
                        throw new IllegalPositionException("Knight can only move one field diagonally + one forward.\n" +
                                "Type: " + fromPiece.getType() +
                                "\nFrom: " + from +
                                "\nTo: " + to);
                    }
                }
                break;
            }
            case ROOK: {
                if (diff % 8 == 0 || getRank(from) == getRank(to)) {
                    break;
                } else {
                    throw new IllegalPositionException("Rook can only move forward, backward, left or right.\n" +
                            "Type: " + fromPiece.getType() +
                            "\nFrom: " + from +
                            "\nTo: " + to);
                }
            }
            case QUEEN: {
                if (diff % 7 == 0 || diff % 8 == 0 || diff % 9 == 0) {
                    break;
                } else if (getRank(from) == getRank(to)) {
                    break;
                } else {
                    throw new IllegalPositionException("Queen can't move one field diagonally + one forward.\n" +
                            "Type: " + fromPiece.getType() +
                            "\nFrom: " + from +
                            "\nTo: " + to);
                }
            }
            case KING: {
                switch (diff) {
                    case -1:
                    case -7:
                    case -8:
                    case -9:
                    case 1:
                    case 7:
                    case 8:
                    case 9: {
                        break;
                    }
                    default: {
                        throw new IllegalPositionException("King can only move one field in any direction.\n" +
                                "Type: " + fromPiece.getType() +
                                "\nFrom: " + from +
                                "\nTo: " + to);
                    }
                }
                break;
            }
            default: {
                throw new IllegalTypeException("Invalid type: " + type);
            }
        }
    }

    /**
     * Get the current direction for a type (not for Knight)
     * @param type
     * @return The direction or -1 if something bad happens
     */
    private int getDirection(int type) {
        int[] directions = getDirections(type);
        for (int direction : directions) {
            if (direction == 1) {
                return 1;
            } else if (diff % direction == 0) {
                return direction;
            }
        }
        return -1;
    }

    /**
     * Get rank from position
     * @param position
     * @return The rank
     */
    private int getRank(int position) {
        int j = 0;
        for (int i = 0; i <= position; i++) {
            if (i % 8 == 0) {
                j++;
            }
        }
        return j;
    }


    /**
     * Get possible directions for a type
     * @param type
     * @return The possible directions for the type or null if something bad happens
     */
    private int[] getDirections(int type) {
        switch (type) {
            case PAWN: {
                return new int[] {7, 8, 9, 16};
            }
            case BISHOP: {
                return new int[] {7, 9};
            }
            case KNIGHT: {
                return new int[] {6, 10, 15, 17};
            }
            case ROOK: {
                return new int[] {8, 1}; // Not including 1 as n % 1 == 0 and that causes an invalid
                                      // warning to be displayed
            }
            case QUEEN:
            case KING: {
                return new int[] {7, 8, 9, 1}; // Not including 1 as n % 1 == 0 and that causes an
                                            // invalid warning to be displayed
            }
            default: {
                throw new IllegalTypeException("Invalid type: " + type);
            }
        }
    }

    /**
     * Checks if path in a direction is clear
     * @param direction
     * @return True if the path is clear and false otherwise
     */
    private boolean isValidPath(int direction) {
        // Could not find a vertical or horizontal direction, but rank is still different
        if (direction == -1) {
            if (getRank(from) != getRank(to)) {
                return true;
            } else {
                return false;
            }
        }
        if (to < from) { // Black moves in a negative direction
            for (int position = from - direction; position > to; position -= direction) {
                if (!board.getPiece(position).isEmpty()) {
                    return false;
                }
            }
        } else { // White moves in a positive direction
            for (int position = from + direction; position < to; position += direction) {
                if (!board.getPiece(position).isEmpty()) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Check if pawn can be promoted
     * @return True if pawn can be promoted
     * @deprecated
     */
    @Deprecated
    public boolean isPromotion() {
        Piece piece = board.getPiece(from);
        if (piece.getType() == PAWN) {
            if (piece.getColor() == WHITE && to >= 48 && to <= 63) {
                return true;
            } else if (piece.getColor() == BLACK && to >= 0 && to <= 7) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if en passant is possible
     * @return True if en passant is possible and false otherwise
     */
    public boolean isEnPassant() {
        Piece fromPiece = board.getPiece(from);
        Piece toPiece = board.getPiece(to);
        if (fromPiece.getType() == PAWN && toPiece.isEmpty()) {
            if (fromPiece.getColor() == WHITE) {
                if (to == from + 9 || to == from + 7) {
                    Piece blackPawn = board.getPiece(to - 8);
                    if (enPassant && blackPawn.getColor() == BLACK) {
                        return true;
                    }
                }
            } else if (fromPiece.getColor() == BLACK) {
                if (to == from - 9 || to == from - 7) {
                    Piece whitePawn = board.getPiece(to + 8);
                    if (enPassant && whitePawn.getColor() == WHITE) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Check if we're castling
     * @return True if we're castling and false otherwise
     */
    public boolean isCastling() {
        Piece piece = board.getPiece(from);
        int rookFrom;
        if (piece.getType() == KING && !piece.isMoved()) {
            if (piece.getColor() == WHITE) {
                if (to == 6 && isEmptyRange(5, 6)) {
                    rookFrom = 7;
                } else if (to == 2 && isEmptyRange(1, 3)) {
                    rookFrom = 0;
                } else {
                    return false;
                }
                Piece rook;
                if (rookFrom == 0 || rookFrom == 7) {
                    rook = board.getPiece(rookFrom);
                } else {
                    return false;
                }
                if (rook.getColor() == WHITE && rook.getType() == ROOK && !rook.isMoved()) {
                    return true;
                }
            } else if (piece.getColor() == BLACK) {
                if (to == 62 && isEmptyRange(61, 62)) {
                    rookFrom = 63;
                } else if (to == 58 && isEmptyRange(57, 59)) {
                    rookFrom = 56;
                } else {
                    return false;
                }
                Piece rook;
                if (rookFrom == 56 || rookFrom == 63) {
                    rook = board.getPiece(rookFrom);
                } else {
                    return false;
                }
                if (rook.getColor() == BLACK && rook.getType() == ROOK && !rook.isMoved()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get the color of a field
     * @param position
     * @return Integer representing the color
     */
    private int getFieldColor(int position) {
        int rank = getRank(position);
        if (rank % 2 == 0) {
            if (position % 2 == 0) {
                return WHITE;
            } else {
                return BLACK;
            }
        } else {
            if (position % 2 == 0) {
                return BLACK;
            } else {
                return WHITE;
            }
        }
    }

    /**
     * Check if all positions between (and including) two positions is empty
     * @param from
     * @param to
     * @return True if range is empty and false otherwise
     */
    private boolean isEmptyRange(int from, int to) {
        if (to < from) {
            for (int i = from; i >= to; i--) {
                if (!board.getPiece(i).isEmpty()) {
                    return false;
                }
            }
        } else {
            for (int i = from; i <= to; i++) {
                if (!board.getPiece(i).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * String representation of object
     * @return Values of object variables
     */
    @Override
    public String toString() {
        String out = "From: " + from + "\nTo: " + to + "\nDiff: " + diff +
                "\nenPassant: " + enPassant;
        return out;
    }

}
