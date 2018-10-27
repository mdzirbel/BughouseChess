

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.Arrays;
import java.util.ArrayList;

public class ChessBoard {
    ChessPiece[][] board = new ChessPiece[8][8]; // Row then Column
    ArrayList<ChessPiece> whiteReserve = new ArrayList<>();
    ArrayList<ChessPiece> blackReserve = new ArrayList<>();
    Pair<Integer, Integer> currentlySelectedBoard = new MutablePair<>(-1, -1);
    Pair<String, Integer> currentlySelectedReserve = new MutablePair<>("", -1);

    public ChessBoard(String colorNear) {
        currentlySelectedBoard = null;
        currentlySelectedReserve = null;
        ChessPiece[] col1 = {new ChessPiece("b", "r"), new ChessPiece("b", "p"), null, null, null, null, new ChessPiece("w", "p"), new ChessPiece("w", "r")};
        ChessPiece[] col2 = {new ChessPiece("b", "kn"), new ChessPiece("b", "p"), null, null, null, null, new ChessPiece("w", "p"), new ChessPiece("w", "kn")};
        ChessPiece[] col3 = {new ChessPiece("b", "b"), new ChessPiece("b", "p"), null, null, null, null, new ChessPiece("w", "p"), new ChessPiece("w", "b")};
        ChessPiece[] col4 = {new ChessPiece("b", "q"), new ChessPiece("b", "p"), null, null, null, null, new ChessPiece("w", "p"), new ChessPiece("w", "q")};
        ChessPiece[] col5 = {new ChessPiece("b", "k"), new ChessPiece("b", "p"), null, null, null, null, new ChessPiece("w", "p"), new ChessPiece("w", "k")};
        ChessPiece[] col6 = {new ChessPiece("b", "b"), new ChessPiece("b", "p"), null, null, null, null, new ChessPiece("w", "p"), new ChessPiece("w", "b")};
        ChessPiece[] col7 = {new ChessPiece("b", "kn"), new ChessPiece("b", "p"), null, null, null, null, new ChessPiece("w", "p"), new ChessPiece("w", "kn")};
        ChessPiece[] col8 = {new ChessPiece("b", "r"), new ChessPiece("b", "p"), null, null, null, null, new ChessPiece("w", "p"), new ChessPiece("w", "r")};
        board[0] = col1; board[1] = col2; board[2] = col3; board[3] = col4; board[4] = col5; board[5] = col6; board[6] = col7; board[7] = col8;
        if (colorNear.equals("b") || colorNear.equals("black")) {
            flipBoard();
        }
    }

    public void flipBoard() {
        for (ChessPiece[] col : board) {
            ArrayUtils.reverse(col);
        }
    }
    public void clickOnBoard(int x, int y) {
        Pair<Integer, Integer> attemptedMove = new MutablePair<>(x, y);
        currentlySelectedReserve = null;
        if (currentlySelectedBoard==null) { // Selecting a piece
            currentlySelectedBoard = new MutablePair<>(x, y);
        }
        else if (attemptedMove.equals(currentlySelectedBoard)) { // Clicks on selected piece (deselects piece)
            currentlySelectedBoard = null;
        }
        else { // Has piece selected, attempting a move
            Pair<Integer, Integer>[] allowedMoves = getAllowedMoves();
            if (Arrays.asList(allowedMoves).contains(attemptedMove)) { // If you can move there
                // Move there
                board[x][y] = board[currentlySelectedBoard.getLeft()][currentlySelectedBoard.getRight()]; // Put your piece there
                board[currentlySelectedBoard.getLeft()][currentlySelectedBoard.getRight()] = null; // Make the space you moved out of empty
            }
        }
    }
    public void clickOnReserve() {
        currentlySelectedBoard = null;

    }

    public ChessPiece[][] getBoard() {
        return board;
    }
    public ArrayList<ChessPiece> getWhiteReserve() {
        return whiteReserve;
    }
    public ArrayList<ChessPiece> getBlackReserve() {
        return blackReserve;
    }

    private void deselect() {
        currentlySelectedBoard = null;
        currentlySelectedReserve = null;
    }

    private Pair<Integer, Integer>[] getAllowedMoves() {

        String selectedPieceType = board[currentlySelectedBoard.getLeft()][currentlySelectedBoard.getRight()].getPieceType();
    }

}
