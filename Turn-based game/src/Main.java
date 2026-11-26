
public class Main {

    public static void main(String[] args) {

        Game game = new Game("Player 1", "Player 2");

        System.out.println("Game started!");
        System.out.println();

        // Player 1 makes a move
        System.out.println(
                game.getCurrentPlayer().getName() + "'s turn"
        );

        game.makeMove(0, 0);

        game.displayBoard();
        System.out.println();

        // Player 2 makes a move
        System.out.println(
                game.getCurrentPlayer().getName() + "'s turn"
        );

        game.makeMove(1, 1);

        game.displayBoard();
        System.out.println();

        // Player 1 tries an occupied cell
        System.out.println(
                game.getCurrentPlayer().getName() + "'s turn"
        );

        boolean result = game.makeMove(0, 0);

        System.out.println("Move successful: " + result);

        // The turn should remain with Player 1
        System.out.println(
                "Current player: "
                        + game.getCurrentPlayer().getName()
        );

        System.out.println();

        // Player 1 makes a valid move
        game.makeMove(0, 1);

        game.displayBoard();
    }
}
