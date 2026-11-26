
public class Main {

    public static void main(String[] args) {

        GameRules rules = new TicTacToeRules();

        MoveStrategy strategy =
                new RandomMoveStrategy();

        Game game = new Game(
                "Player 1",
                "Player 2",
                rules,
                strategy
        );

        Command move1 =
                new MoveCommand(game, 0, 0);

        Command move2 =
                new MoveCommand(game, 1, 0);

        Command move3 =
                new MoveCommand(game, 0, 1);

        Command move4 =
                new MoveCommand(game, 1, 1);

        Command move5 =
                new MoveCommand(game, 0, 2);

        move1.execute();
        move2.execute();
        move3.execute();
        move4.execute();
        move5.execute();

        game.displayBoard();
        System.out.println();

        game.displayResult();
    }
}
