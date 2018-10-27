import android.util.Pair;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;

public class ChessBoard {
    ChessPiece[][] board = new ChessPiece[8][8]; // Row then Column
    ArrayList<ChessPiece> whiteReserve = new ArrayList<>();
    ArrayList<ChessPiece> blackReserve = new ArrayList<>();
    Pair<Integer, Integer> currentlySelectedBoard;
    Pair<String, Integer> currentlySelectedReserve;

    public ChessBoard(String colorNear) {
        ChessPiece[] col1 = {new ChessPiece("b", "r"), new ChessPiece("b", "p"), null, null, null, null, new ChessPiece("w", "p"), new ChessPiece("w", "r")};
        ChessPiece[] col2 = {new ChessPiece("b", "kn"), new ChessPiece("b", "p"), null, null, null, null, new ChessPiece("w", "p"), new ChessPiece("w", "kn")};
        ChessPiece[] col3 = {new ChessPiece("b", "b"), new ChessPiece("b", "p"), null, null, null, null, new ChessPiece("w", "p"), new ChessPiece("w", "b")};
        ChessPiece[] col4 = {new ChessPiece("b", "q"), new ChessPiece("b", "p"), null, null, null, null, new ChessPiece("w", "p"), new ChessPiece("w", "q")};
        ChessPiece[] col5 = {new ChessPiece("b", "k"), new ChessPiece("b", "p"), null, null, null, null, new ChessPiece("w", "p"), new ChessPiece("w", "k")};
        ChessPiece[] col6 = {new ChessPiece("b", "b"), new ChessPiece("b", "p"), null, null, null, null, new ChessPiece("w", "p"), new ChessPiece("w", "b")};
        ChessPiece[] col7 = {new ChessPiece("b", "kn"), new ChessPiece("b", "p"), null, null, null, null, new ChessPiece("w", "p"), new ChessPiece("w", "kn")};
        ChessPiece[] col8 = {new ChessPiece("b", "r"), new ChessPiece("b", "p"), null, null, null, null, new ChessPiece("w", "p"), new ChessPiece("w", "r")};
        board[0] = col1;
        board[1] = col2;
        board[2] = col3;
        board[3] = col4;
        board[4] = col5;
        board[5] = col6;
        board[6] = col7;
        board[7] = col8;
        if (colorNear.equals("b") || colorNear.equals("black")) {
            flipBoard();
        }
    }

    public void flipBoard() {
        for (ChessPiece[] col : board) {
            ArrayUtils.reverse(col);
        }
    }

    private ChessPiece getPiece(int x, int y){
        return board[x][y];
    }

    public void clickOnBoard(int x, int y) {

    }
    public void clickOnReserve() {

    }

    public ChessPiece[][] getBoard() {
        return board;
    }

}
