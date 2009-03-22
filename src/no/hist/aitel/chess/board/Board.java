/*
 * Board.java
 * 
 */

package no.hist.aitel.chess.board;

import no.hist.aitel.chess.position.Position;
import no.hist.aitel.chess.piece.Piece;
import no.hist.aitel.chess.piece.IllegalPieceException;

/**
 *
 * @author martin
 */

public class Board {

    final private int size = 64;
    private Piece[] board = new Piece[size];
    private Piece[] captured = new Piece[size];
    private int turn = 0;
    private Position p = new Position(this);
    private int capturedPos = -1;
    
    /**
     * Creates the board and makes it ready for a new game
     */
    public Board() {
        board = new BoardInit().getInitBoard();
    }

    /**
     * Get board
     * @return The current board
     */
    public Piece[] getBoard() { // Not needed?
        return board;
    }

    /**
     * Get piece
     * @param position
     * @return The piece in the given position
     */
    public Piece getPiece(int position) {
        return board[position];
    }

    /**
     * Get capturedPos
     * @param position
     * @return The position of the captured piece
     */
    public int getCapturedPos() {
        return capturedPos;
    }

    public void setCapturedPos(int capturedPos) {
        this.capturedPos = capturedPos;
    }

    /**
     * Move a piece from an old position to a new position
     * @param from
     * @param to
     */
    public void movePiece(int from, int to) {

        // Check if any of the positions are outside the board
        if ((from < 0 || from > 63) || (to < 0 || from > 63)) {
            throw new IllegalPieceException("Can't move pieces outside of the board.\n"
                    + "\nFrom: " + from
                    + "\nTo: " + to);
        }

        // Check if piece in 'from' position is empty
        if (getPiece(from).isEmpty()) {
            throw new IllegalPieceException("Can't move empty piece.\n" +
                    "\nFrom: " + from +
                    "\nTo: " + to);
        }
        
        // Check whos turn it is
        int color = getPiece(from).getColor();
        if (!isValidTurn(color)) {
            throw new IllegalTurnException("Color " + color + " is not allowed to move now.");
        }

        p.setPositions(from, to);
        p.verifyPositions();

        if (!getPiece(to).isEmpty()) {
            addCaptured(getPiece(to));
            capturedPos=to;
        }

        board[to] = getPiece(from);
        board[from] = new Piece(); // Empty piece
        switchTurn();
    }

    /**
     * Get the captured pieces
     * @return The captured pieces
     */
    public Piece[] getCaptured() {
        return captured;
    }

    /**
     * Adds a piece to the captured array
     * @param piece
     */
    private void addCaptured(Piece piece) {
        for (int i = 0; i < captured.length; i++) {
            if (captured[i] == null) {
                captured[i] = piece;
                break;
            }
        }
    }

    /**
     * Check whos turn it is
     * @param color
     * @return True if color can move and false otherwise
     */
    private boolean isValidTurn(int color) {
        return (color == turn);
    }

    /**
     * Switches turn
     */
    private void switchTurn() {
        turn ^= 1; // Bitwise flip between 0 and 1
    }

    /**
     * Produces a string representation of the chess board
     * @return Fancy ascii drawing of the board, colors and types
     */
    @Override
    public String toString() {
        String out = ""; 
        for (int i = 0; i < board.length; i++) {
            if (i % 8 == 0) {
                out = out + "\n";
            }
            int type = board[i].getType();
            int color = board[i].getColor();
            if (color == 0) {
                out += "W";
            } else if (color == 1) {
                out += "B";
            } else {
                out += "x";
            }
            if (type == -1) {
                out += "x ";
            } else {
                out += type + " ";
            }
        }
        // Reverse output
        String[] outArr = out.split("\n");
        out = "+-------------------------+\n";
        for (int i = outArr.length - 1; i > 0; i--) {
            out += "| " + outArr[i] + "|\n";
        }
        out += "+-------------------------+";
        return out;
    }
}
