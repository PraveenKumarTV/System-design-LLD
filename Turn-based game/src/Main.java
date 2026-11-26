
public class Main {

    public static void main(String[] args) {

        GameRules rules = new TicTacToeRules();

        MoveStrategy strategy =
                new RandomMoveStrategy();

        Game game = new Game(
                "Computer 1",
                "Computer 2",
                rules,
                strategy
        );

        while (game.getResult().getStatus()
                == GameResult.Status.IN_PROGRESS) {

            System.out.println(
                    game.getCurrentPlayer().getName()
                    + " is making a move..."
            );

            game.makeComputerMove();

            game.displayBoard();
            System.out.println();
        }

        game.displayResult();
    }
}
