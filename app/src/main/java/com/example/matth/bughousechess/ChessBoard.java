package com.example.matth.bughousechess;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.Arrays;
import java.util.HashMap;

public class ChessBoard {
    ChessPiece[][] board = new ChessPiece[8][8]; // Row then Column
    HashMap<ChessPiece, Integer> whiteReserve = new HashMap<>();
    HashMap<ChessPiece, Integer> blackReserve = new HashMap<>();
    Pair<Integer, Integer> currentlySelectedBoard = new MutablePair<>(-1, -1);
    Pair<String, Integer> currentlySelectedReserve = new MutablePair<>("", -1);

    public ChessBoard(String colorNear) {
        //TODO - handle en passant and castling
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
        whiteReserve.put(new ChessPiece("w", "p"), 0);
        whiteReserve.put(new ChessPiece("w", "kn"), 0);
        whiteReserve.put(new ChessPiece("w", "b"), 0);
        whiteReserve.put(new ChessPiece("w", "r"), 0);
        whiteReserve.put(new ChessPiece("w", "q"), 0);

        blackReserve.put(new ChessPiece("b", "p"), 0);
        blackReserve.put(new ChessPiece("b", "kn"), 0);
        blackReserve.put(new ChessPiece("b", "b"), 0);
        blackReserve.put(new ChessPiece("b", "r"), 0);
        blackReserve.put(new ChessPiece("b", "q"), 0);
    }

    public void flipBoard() {
        for (ChessPiece[] col : board) {
            ArrayUtils.reverse(col);
        }
    }
    public void clickOnBoard(int x, int y) {
        Pair<Integer, Integer> attemptedMove = new MutablePair<>(x, y);
        if (currentlySelectedBoard==null) {
            if (currentlySelectedReserve==null) {
                currentlySelectedBoard = new MutablePair<>(x, y); // Selecting a piece
            }
            else { // Placing a piece from reserve

            }
        }
        else { // Has piece selected, attempting a move
            Pair<Integer, Integer>[] allowedMoves = getAllowedMoves();
            if (Arrays.asList(allowedMoves).contains(attemptedMove)) { // If you can move there
                // Check if you are taking an opponent's piece
                if (getTileFromPair(attemptedMove)!=null) { // If it's not null it's an opponent's piece
                    // sendReserve(getTileFromPair(attemptedMove)); // TODO add this in once Max makes it
                }
                board[x][y] = board[currentlySelectedBoard.getLeft()][currentlySelectedBoard.getRight()]; // Put your piece there
                board[currentlySelectedBoard.getLeft()][currentlySelectedBoard.getRight()] = null; // Make the space you moved out of empty
            }
            else {
                currentlySelectedBoard = null;
            }
        }
    }
    public void clickOnReserve() {
        currentlySelectedBoard = null;

    }

    public ChessPiece[][] getBoard() {
        return board;
    }
    public HashMap<ChessPiece, Integer> getWhiteReserve() {
        return whiteReserve;
    }
    public HashMap<ChessPiece, Integer> getBlackReserve() {
        return blackReserve;
    }
    public void recieveReserve(String team, String type) {
        team = HelperFunctions.unAbbrevTeam(team);
        if (team.equals("white")) {
            whiteReserve.put(new ChessPiece(team, type), whiteReserve.get(new ChessPiece(team, type))+1); // increment the white reserve of the correct type
        }
        else if (team.equals("black")) {
            blackReserve.put(new ChessPiece(team, type), blackReserve.get(new ChessPiece(team, type))+1); // increment the black reserve of the correct type
        }
        else {
            assert false : "Nice try Max. Green is an alliance, not a team name.";
        }
    }
    private ChessPiece getTileFromPair(Pair<Integer, Integer> p){
        return board[p.getLeft()][p.getRight()];
    }
    private void deselect() {
        currentlySelectedBoard = null;
        currentlySelectedReserve = null;
    }

    private Pair<Integer, Integer>[] getAllowedMoves() {
        String selectedPieceType = board[currentlySelectedBoard.getLeft()][currentlySelectedBoard.getRight()].getPieceType();

        switch (selectedPieceType) {
            case ("pawn"):

        }

    }

}
