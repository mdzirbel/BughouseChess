import java.util.Arrays;
import java.util.HashMap;

public class ChessPiece {

    String teams[] = {"white", "black"};
    String pieces[] = {"pawn", "rook", "knight", "bishop", "king", "queen"};
    String type;
    String team;

    public ChessPiece(String team, String type) {
        if (team.equals("b")) {
            team = "black";
        }
        if (team.equals("w")) {
            team = "white";
        }
        switch (type) {
            case "p":
                type = "pawn";
                break;
            case "r":
                type = "rook";
                break;
            case "kn":
                type = "knight";
                break;
            case "b":
                type = "bishop";
                break;
            case "k":
                type = "k";
                break;
            case "q":
                type = "queen";
                break;
        }
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
