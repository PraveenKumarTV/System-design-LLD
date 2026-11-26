
public class Main {

    public static void main(String[] args) {

        GameRules rules = new TicTacToeRules();

        Game game = new Game(
                "Player 1",
                "Player 2",
                rules
        );

        // Player 1: X at (0, 0)
        game.makeMove(0, 0);

        // Player 2: O at (1, 0)
        game.makeMove(1, 0);

        // Player 1: X at (0, 1)
        game.makeMove(0, 1);

        // Player 2: O at (1, 1)
        game.makeMove(1, 1);

        // Player 1: X at (0, 2) -> wins
        game.makeMove(0, 2);

        game.displayBoard();

        System.out.println();

        game.displayResult();
    }
}
