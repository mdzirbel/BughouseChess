package com.example.matth.bughousechess;

import java.util.Arrays;

public class ChessPiece {

    String teams[] = {"white", "black"};
    String pieces[] = {"pawn", "rook", "knight", "bishop", "king", "queen"};
    String type;
    String team;

    public ChessPiece(String team, String type) {
        team = HelperFunctions.unAbbrevTeam(team);
        type = HelperFunctions.unAbbrevType(type);
        assert Arrays.asList(teams).contains(team) : "Not a valid team color";
        assert Arrays.asList(pieces).contains(type) : "Not a valid piece type";
        this.type = type;
        this.team = team;
    }

    public String getPieceType() {
        return this.type;
    }
    public String getTeam() {
        return this.team;
    }
}
