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
    Pair<String, String> currentlySelectedReserve = new MutablePair<>("", "");
    String currentTurn = "white";

    public ChessBoard(String colorNear) {
        //TODO - handle en passant and castling
        currentlySelectedBoard = null;
        currentlySelectedReserve = null;
        ChessPiece[] col0 = {new ChessPiece("b", "r"), new ChessPiece("b", "p"), null, null, null, null, new ChessPiece("w", "p"), new ChessPiece("w", "r")};
        ChessPiece[] col1 = {new ChessPiece("b", "kn"), new ChessPiece("b", "p"), null, null, null, null, new ChessPiece("w", "p"), new ChessPiece("w", "kn")};
        ChessPiece[] col2 = {new ChessPiece("b", "b"), new ChessPiece("b", "p"), null, null, null, null, new ChessPiece("w", "p"), new ChessPiece("w", "b")};
        ChessPiece[] col3 = {new ChessPiece("b", "q"), new ChessPiece("b", "p"), null, null, null, null, new ChessPiece("w", "p"), new ChessPiece("w", "q")};
        ChessPiece[] col4 = {new ChessPiece("b", "k"), new ChessPiece("b", "p"), null, null, null, null, new ChessPiece("w", "p"), new ChessPiece("w", "k")};
        ChessPiece[] col5 = {new ChessPiece("b", "b"), new ChessPiece("b", "p"), null, null, null, null, new ChessPiece("w", "p"), new ChessPiece("w", "b")};
        ChessPiece[] col6 = {new ChessPiece("b", "kn"), new ChessPiece("b", "p"), null, null, null, null, new ChessPiece("w", "p"), new ChessPiece("w", "kn")};
        ChessPiece[] col7 = {new ChessPiece("b", "r"), new ChessPiece("b", "p"), null, null, null, null, new ChessPiece("w", "p"), new ChessPiece("w", "r")};
        board[0] = col0; board[1] = col1; board[2] = col2; board[3] = col3; board[4] = col4; board[5] = col5; board[6] = col6; board[7] = col7;
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
            if (currentlySelectedReserve==null) { // If you don't have a piece selected
                if (currentTurn.equals(getPieceFromPair(new MutablePair<>(x, y)))) { // If you select one of your pieces
                    currentlySelectedBoard = new MutablePair<>(x, y); // select piece
                }
            }
            else { // Placing a piece from reserve //TODO finish
                if (hasPiece(x,y)) { // Can't place piece from reserve; deselect //TODO make it so pawns can't be placed on 1st/last
                    deselect();
                }
                else { // Can place piece from reserve, place piece and decrement reserve
                    decrementReserve(currentlySelectedReserve.getLeft(), currentlySelectedReserve.getRight());

                }
            }
        }
        else { // Has piece selected, attempting a move
            Pair<Integer, Integer>[] allowedMoves = getAllowedMoves();
            if (Arrays.asList(allowedMoves).contains(attemptedMove)) { // If you can move there
                // Check if you are taking an opponent's piece
                if (getPieceFromPair(attemptedMove)!=null) { // If it's not null it's an opponent's piece
                    ChessPiece takenPiece = getPieceFromPair(attemptedMove);
                    takenPiece.type = takenPiece.initalType;
                    MainActivity.coms.sendReserve(takenPiece); // Send it to the other phone
                }
                board[x][y] = board[currentlySelectedBoard.getLeft()][currentlySelectedBoard.getRight()]; // Put your piece there
                board[currentlySelectedBoard.getLeft()][currentlySelectedBoard.getRight()] = null; // Make the space you moved out of empty
            }
            deselect();
        }
    }
    public void clickOnReserve() {
        currentlySelectedBoard = null;
        // TODO write this
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
        incrementReserve(team, type);
    }
    private void incrementReserve(String team, String type) {
        team = HelperFunctions.unAbbrevTeam(team);
        type = HelperFunctions.unAbbrevType(type);
        if (team.equals("white")) {
            whiteReserve.put(new ChessPiece(team, type), whiteReserve.get(new ChessPiece(team, type))+1);
        }
        else if (team.equals("black")) {
            blackReserve.put(new ChessPiece(team, type), blackReserve.get(new ChessPiece(team, type))+1);
        }
    }
    private void decrementReserve(String team, String type) {
        team = HelperFunctions.unAbbrevTeam(team);
        type = HelperFunctions.unAbbrevType(type);
        if (team.equals("white")) {
            whiteReserve.put(new ChessPiece(team, type), whiteReserve.get(new ChessPiece(team, type))-1);
        }
        else if (team.equals("black")) {
            blackReserve.put(new ChessPiece(team, type), blackReserve.get(new ChessPiece(team, type))-1);
        }
    }
    private ChessPiece getPieceFromPair(Pair<Integer, Integer> p){
        return board[p.getLeft()][p.getRight()];
    }
    private boolean hasPiece(int x, int y) {
        return board[x][y]!=null;
    }
    private void deselect() {
        currentlySelectedBoard = null;
        currentlySelectedReserve = null;
    }

    private Pair<Integer, Integer>[] getAllowedMoves() { // TODO make this work
        String selectedPieceType = board[currentlySelectedBoard.getLeft()][currentlySelectedBoard.getRight()].getPieceType();

        switch (selectedPieceType) {
            case ("pawn"):

        }

        return new MutablePair[5];

    }

}
