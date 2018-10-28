package com.example.matth.bughousechess;

public class HelperFunctions {

    public static String unAbbrevType(String type) {
        switch (type) {
            case "p":
                return "pawn";
            case "r":
                return "rook";
            case "kn":
                return "knight";
            case "b":
                return "bishop";
            case "k":
                return "king";
            case "q":
                return "queen";
            default:
                return type;
        }
    }

    public static String unAbbrevTeam(String team) {
        switch (team) {
            case "b":
                return "black";
            case "w":
                return "white";
            default:
                return team;
        }
    }
}
