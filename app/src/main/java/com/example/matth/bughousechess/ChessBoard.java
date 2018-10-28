package com.example.matth.bughousechess;

import android.util.Log;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ChessBoard {
    private ChessPiece[][] board = new ChessPiece[8][8]; // Row then Column
    private HashMap<ChessPiece, Integer> whiteReserve = new HashMap<>();
    private HashMap<ChessPiece, Integer> blackReserve = new HashMap<>();
    private Pair<Integer, Integer> currentlySelectedBoard;
    private Pair<String, String> currentlySelectedReserve; // Team, type
    private String currentPlayer = "white";

    public ChessBoard(String colorNear) {
        //TODO - handle en passant and check/checkmate
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

    public void clickOnBoard(int x, int y) {
        if (currentlySelectedBoard==null) {
            if (currentlySelectedReserve==null) { // If you don't have a piece selected
                if (tileHasPiece(x,y) && currentPlayer.equals(getPieceAt(new MutablePair<>(x, y)).team)) { // If you select one of your pieces
                    currentlySelectedBoard = new MutablePair<>(x, y); // select piece
                    Log.i("CLICK", "Selected board piece at ("+x+", "+y+")");
                }
            }
            else { // Placing a piece from reserve
                if (currentlySelectedReserve.getRight().equals("pawn") && (x==0 || x==7)) { // Don't let pawns be placed on first or last rank
                    deselect();
                    Log.i("CLICK", "Deselected because you can't place a pawn from reserve in first or last rank");
                }
                else {
                    if (tileHasPiece(x, y)) { // Can't place piece from reserve; deselect
                        deselect();
                        Log.i("CLICK", "Deselected because you can't place on preexisting piece");
                    } else { // Can place piece from reserve, place piece and decrement reserve
                        decrementReserve(currentlySelectedReserve.getLeft(), currentlySelectedReserve.getRight());
                        board[x][y] = new ChessPiece(currentlySelectedReserve.getLeft(), currentlySelectedReserve.getRight());
                        board[x][y].hasMoved = true;
                        switchCurrentPlayer();
                        Log.i("CLICK", "Placed piece on board");
                    }
                }
            }
        }
        else { // Has piece selected, attempting a move
            ArrayList<Pair<Integer, Integer>> allowedMoves = getAllowedMoves(currentlySelectedBoard.getLeft(), currentlySelectedBoard.getRight());
            if (allowedMoves.contains(new MutablePair<>(x, y))) { // If you can move there
                ChessPiece takenPiece = move(x, y);
                if (takenPiece!=null) {
//                    MainActivity.coms.sendReserve(takenPiece); // Send it to the other phone
                }
            }
            deselect();
            Log.i("CLICK", "Deselecting");
        }
    }
    private ChessPiece move(int x, int y) {
        ChessPiece takenPiece = null;
        if (getPieceAt(x, y)!=null) { // If it's not null it's an opponent's piece, so queue up takenPiece
            takenPiece = getPieceAt(x, y);
            takenPiece.type = takenPiece.initalType;
        }
        if (getPieceAt(currentlySelectedBoard).type.equals("king") && Math.abs(x-currentlySelectedBoard.getLeft())>=2) { // If castling
            if (x>currentlySelectedBoard.getLeft()) { // Castling to the right
                board[5][y] = board[7][y];
                board[7][y] = null;
                board[5][y].hasMoved = true;
                Log.i("CLICK", "Right Castle");
            }
            else { // Castling to the right
                board[3][y] = board[0][y];
                board[0][y] = null;
                board[3][y].hasMoved = true;
                Log.i("CLICK", "Right Castle");
            }
        }
        board[x][y] = board[currentlySelectedBoard.getLeft()][currentlySelectedBoard.getRight()]; // Put your piece there
        board[x][y].hasMoved = true;
        board[currentlySelectedBoard.getLeft()][currentlySelectedBoard.getRight()] = null; // Make the space you moved out of empty
        switchCurrentPlayer();
        Log.i("CLICK", "Moving piece");
        return takenPiece;
    }
    public void clickOnReserve(String team, String type) {
        currentlySelectedBoard = null;
        currentlySelectedReserve = new MutablePair<>(team, type);
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
    public boolean tileHasPiece(int x, int y) {
        if (isInBoard(x,y)) {
            return board[x][y] != null;
        }
        else {
            return false;
        }
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
    private ChessPiece getPieceAt(int x, int y) {
        return board[x][y];
    }
    private ChessPiece getPieceAt(Pair<Integer, Integer> p){
        return board[p.getLeft()][p.getRight()];
    }
    private void deselect() {
        currentlySelectedBoard = null;
        currentlySelectedReserve = null;
    }
    private void switchCurrentPlayer() {
        if (currentPlayer.equals("white")) {
            currentPlayer = "black";
        }
        else {
            currentPlayer = "white";
        }
    }

    private boolean isInBoard(int x, int y) {
        return (x>=0 && x<=7 && y>=0 && y <= 7);
    }
    private boolean isEnemy(String yourTeam, int x, int y) {
        return tileHasPiece(x, y) && !getPieceAt(x, y).team.equals(yourTeam);
    }
    private boolean isEmpty(int x, int y) {
        return isInBoard(x, y) && board[x][y] == null;
    }
    private boolean isEmptyOrEnemy(String yourTeam, int x, int y) {
        return isInBoard(x, y) && (board[x][y] == null || isEnemy(yourTeam, x, y));
    }
    private ArrayList<Pair<Integer, Integer>> getAllowedMoves(int x, int y) { // TODO make this work
        ChessPiece piece = board[x][y];
        String type = piece.getPieceType();
        String team = piece.getTeam();

        ArrayList<Pair<Integer, Integer>> allowedMoves = new ArrayList<>();

        switch (type) {
            case ("pawn"):
                if (team.equals("black")) {
                    if (isEnemy(team, x+1,y+1)) {
                        allowedMoves.add(new MutablePair<>(x+1,y+1));
                    }
                    if (isEnemy(team, x-1,y+1)) {
                        allowedMoves.add(new MutablePair<>(x-1,y+1));
                    }
                    if (!tileHasPiece(x,y+1)) {
                        allowedMoves.add(new MutablePair<>(x,y+1));
                    }
                    if (!piece.hasMoved && !tileHasPiece(x,y+2)) {
                        allowedMoves.add(new MutablePair<>(x,y+2));
                    }
                }
                if (team.equals("white")) {
                    if (isEnemy(team, x+1,y-1)) {
                        allowedMoves.add(new MutablePair<>(x+1,y-1));
                    }
                    if (isEnemy(team, x-1,y-1)) {
                        allowedMoves.add(new MutablePair<>(x-1,y-1));
                    }
                    if (!tileHasPiece(x,y-1)) {
                        allowedMoves.add(new MutablePair<>(x,y-1));
                    }
                    if (!piece.hasMoved && !tileHasPiece(x,y-2)) {
                        allowedMoves.add(new MutablePair<>(x,y-2));
                    }
                }
                break;
            case ("rook"):
                // up
                for (int i=1; i<8; i++) {
                    int newX = x;
                    int newY = y-i;
                    if (isEmpty(newX, newY)) {
                        allowedMoves.add(new MutablePair<>(newX,newY));
                    }
                    else if (isEnemy(team, newX, newY)) {
                        allowedMoves.add(new MutablePair<>(newX,newY));
                        break;
                    }
                    else {
                        break;
                    }
                }
                // down
                for (int i=1; i<8; i++) {
                    int newX = x;
                    int newY = y+i;
                    if (isEmpty(newX, newY)) {
                        allowedMoves.add(new MutablePair<>(newX,newY));
                    }
                    else if (isEnemy(team, newX, newY)) {
                        allowedMoves.add(new MutablePair<>(newX,newY));
                        break;
                    }
                    else {
                        break;
                    }

                }
                // right
                for (int i=1; i<8; i++) {
                    int newX = x+i;
                    int newY = y;
                    if (isEmpty(newX, newY)) {
                        allowedMoves.add(new MutablePair<>(newX,newY));
                    }
                    else if (isEnemy(team, newX, newY)) {
                        allowedMoves.add(new MutablePair<>(newX,newY));
                        break;
                    }
                    else {
                        break;
                    }
                }
                // left
                for (int i=1; i<8; i++) {
                    int newX = x-i;
                    int newY = y;
                    if (isEmpty(newX, newY)) {
                        allowedMoves.add(new MutablePair<>(newX,newY));
                    }
                    else if (isEnemy(team, newX, newY)) {
                        allowedMoves.add(new MutablePair<>(newX,newY));
                        break;
                    }
                    else {
                        break;
                    }
                }
                break;
            case ("knight"):
                // up left
                if (isEmptyOrEnemy(team, x-1,y-2)) {
                    allowedMoves.add(new MutablePair<>(x-1,y-2));
                }
                // up right
                if (isEmptyOrEnemy(team, x+1,y-2)) {
                    allowedMoves.add(new MutablePair<>(x+1,y-2));
                }
                // righ tup
                if (isEmptyOrEnemy(team, x+2,y-1)) {
                    allowedMoves.add(new MutablePair<>(x+2,y-1));
                }
                // right down
                if (isEmptyOrEnemy(team, x+2,y+1)) {
                    allowedMoves.add(new MutablePair<>(x+2,y+1));
                }
                // down right
                if (isEmptyOrEnemy(team, x+1,y+2)) {
                    allowedMoves.add(new MutablePair<>(x+1,y+2));
                }
                // down left
                if (isEmptyOrEnemy(team, x-1,y+2)) {
                    allowedMoves.add(new MutablePair<>(x-1,y+2));
                }
                // left up
                if (isEmptyOrEnemy(team, x-2,y-1)) {
                    allowedMoves.add(new MutablePair<>(x-2,y-1));
                }
                // left down
                if (isEmptyOrEnemy(team, x-2,y+1)) {
                    allowedMoves.add(new MutablePair<>(x-2,y+1));
                }
                break;
            case ("bishop"):
                // upright
                for (int i=1; i<8; i++) {
                    int newX = x+i;
                    int newY = y-i;
                    if (isEmpty(newX, newY)) {
                        allowedMoves.add(new MutablePair<Integer, Integer>(newX,newY));
                    }
                    else if (isEnemy(team, newX, newY)) {
                        allowedMoves.add(new MutablePair<Integer, Integer>(newX,newY));
                        break;
                    }
                    else {
                        break;
                    }
                }
                // downright
                for (int i=1; i<8; i++) {
                    int newX = x+i;
                    int newY = y+i;
                    if (isEmpty(newX, newY)) {
                        allowedMoves.add(new MutablePair<Integer, Integer>(newX,newY));
                    }
                    else if (isEnemy(team, newX, newY)) {
                        allowedMoves.add(new MutablePair<Integer, Integer>(newX,newY));
                        break;
                    }
                    else {
                        break;
                    }
                }
                // upleft
                for (int i=1; i<8; i++) {
                    int newX = x-i;
                    int newY = y-i;
                    if (isEmpty(newX, newY)) {
                        allowedMoves.add(new MutablePair<Integer, Integer>(newX,newY));
                    }
                    else if (isEnemy(team, newX, newY)) {
                        allowedMoves.add(new MutablePair<Integer, Integer>(newX,newY));
                        break;
                    }
                    else {
                        break;
                    }
                }
                // downleft
                for (int i=1; i<8; i++) {
                    int newX = x-i;
                    int newY = y+i;
                    if (isEmpty(newX, newY)) {
                        allowedMoves.add(new MutablePair<Integer, Integer>(newX,newY));
                    }
                    else if (isEnemy(team, newX, newY)) {
                        allowedMoves.add(new MutablePair<Integer, Integer>(newX,newY));
                        break;
                    }
                    else {
                        break;
                    }
                }
                break;
            case("king"):
                if (isEmptyOrEnemy(team, x-1,y-1)) {
                    allowedMoves.add(new MutablePair<>(x-1,y-1));
                }
                if (isEmptyOrEnemy(team, x-1,y)) {
                    allowedMoves.add(new MutablePair<>(x-1,y));
                }
                if (isEmptyOrEnemy(team, x-1,y+1)) {
                    allowedMoves.add(new MutablePair<>(x-1,y+1));
                }
                if (isEmptyOrEnemy(team, x,y+1)) {
                    allowedMoves.add(new MutablePair<>(x,y+1));
                }
                if (isEmptyOrEnemy(team, x+1,y+1)) {
                    allowedMoves.add(new MutablePair<>(x+1,y+1));
                }
                if (isEmptyOrEnemy(team, x+1,y)) {
                    allowedMoves.add(new MutablePair<>(x+1,y));
                }
                if (isEmptyOrEnemy(team, x+1,y-1)) {
                    allowedMoves.add(new MutablePair<>(x+1,y-1));
                }
                if (isEmptyOrEnemy(team, x,y-1)) {
                    allowedMoves.add(new MutablePair<>(x,y-1));
                }
                if (!piece.hasMoved) { // If you haven't moved the king, look into castling availability
                    ChessPiece left = getPieceAt(0, y);
                    ChessPiece right = getPieceAt(7, y);

                    if (!left.hasMoved && isEmpty(1, y) && isEmpty(2,y) && isEmpty(3,y)) {
                        allowedMoves.add(new MutablePair<>(2,y));
                    }
                    if (!right.hasMoved && isEmpty(5, y) && isEmpty(6,y)) {
                        allowedMoves.add(new MutablePair<>(6,y));
                    }
                }
                break;
            case("queen"):
                // upright
                for (int i=1; i<8; i++) {
                    int newX = x+i;
                    int newY = y-i;
                    if (isEmpty(newX, newY)) {
                        allowedMoves.add(new MutablePair<>(newX,newY));
                    }
                    else if (isEnemy(team, newX, newY)) {
                        allowedMoves.add(new MutablePair<>(newX,newY));
                        break;
                    }
                    else {
                        break;
                    }
                }
                // downright
                for (int i=1; i<8; i++) {
                    int newX = x+i;
                    int newY = y+i;
                    if (isEmpty(newX, newY)) {
                        allowedMoves.add(new MutablePair<>(newX,newY));
                    }
                    else if (isEnemy(team, newX, newY)) {
                        allowedMoves.add(new MutablePair<>(newX,newY));
                        break;
                    }
                    else {
                        break;
                    }
                }
                // upleft
                for (int i=1; i<8; i++) {
                    int newX = x-i;
                    int newY = y-i;
                    if (isEmpty(newX, newY)) {
                        allowedMoves.add(new MutablePair<>(newX,newY));
                    }
                    else if (isEnemy(team, newX, newY)) {
                        allowedMoves.add(new MutablePair<>(newX,newY));
                        break;
                    }
                    else {
                        break;
                    }
                }
                // downleft
                for (int i=1; i<8; i++) {
                    int newX = x-i;
                    int newY = y+i;
                    if (isEmpty(newX, newY)) {
                        allowedMoves.add(new MutablePair<>(newX,newY));
                    }
                    else if (isEnemy(team, newX, newY)) {
                        allowedMoves.add(new MutablePair<>(newX,newY));
                        break;
                    }
                    else {
                        break;
                    }
                }
                // up
                for (int i=1; i<8; i++) {
                    int newX = x;
                    int newY = y-i;
                    if (isEmpty(newX, newY)) {
                        allowedMoves.add(new MutablePair<>(newX,newY));
                    }
                    else if (isEnemy(team, newX, newY)) {
                        allowedMoves.add(new MutablePair<>(newX,newY));
                        break;
                    }
                    else {
                        break;
                    }
                }
                // down
                for (int i=1; i<8; i++) {
                    int newX = x;
                    int newY = y+i;
                    if (isEmpty(newX, newY)) {
                        allowedMoves.add(new MutablePair<>(newX,newY));
                    }
                    else if (isEnemy(team, newX, newY)) {
                        allowedMoves.add(new MutablePair<>(newX,newY));
                        break;
                    }
                    else {
                        break;
                    }
                }
                // right
                for (int i=1; i<8; i++) {
                    int newX = x+i;
                    int newY = y;
                    if (isEmpty(newX, newY)) {
                        allowedMoves.add(new MutablePair<>(newX,newY));
                    }
                    else if (isEnemy(team, newX, newY)) {
                        allowedMoves.add(new MutablePair<>(newX,newY));
                        break;
                    }
                    else {
                        break;
                    }
                }
                // left
                for (int i=1; i<8; i++) {
                    int newX = x-i;
                    int newY = y;
                    if (isEmpty(newX, newY)) {
                        allowedMoves.add(new MutablePair<>(newX,newY));
                    }
                    else if (isEnemy(team, newX, newY)) {
                        allowedMoves.add(new MutablePair<>(newX,newY));
                        break;
                    }
                    else {
                        break;
                    }
                }
                break;
            default:
                assert false : "You wound up with the default case for piece type. That shouldn't happen";


        }

        removeIllegalMoves(allowedMoves, x, y);

        return allowedMoves;

    }

    void removeIllegalMoves(ArrayList<Pair<Integer, Integer>> moves, int xPiece, int yPiece) {

        for (Pair<Integer, Integer> move : moves) {

        }
    }
}
