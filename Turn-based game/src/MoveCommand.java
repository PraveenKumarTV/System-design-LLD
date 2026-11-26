
public class MoveCommand implements Command {

    private final Game game;
    private final int row;
    private final int col;

    public MoveCommand(Game game, int row, int col) {
        this.game = game;
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean execute() {
        return game.makeMove(row, col);
    }
}
