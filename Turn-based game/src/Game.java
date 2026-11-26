
public class Game {

    private Board board;
    private BoardDisplay boardDisplay;

    private GameRules rules;
    private MoveStrategy moveStrategy;

    private Player player1;
    private Player player2;
    private Player currentPlayer;

    private GameResult result;

    public Game(
            String player1Name,
            String player2Name,
            GameRules rules,
            MoveStrategy moveStrategy
    ) {

        board = new Board();
        boardDisplay = new BoardDisplay();

        this.rules = rules;
        this.moveStrategy = moveStrategy;

        player1 = new Player(player1Name, 'X');
        player2 = new Player(player2Name, 'O');

        currentPlayer = player1;
        result = GameResult.inProgress();
    }

    // Used when a human provides row and column
    public boolean makeMove(int row, int col) {

        Move move = currentPlayer.createMove(row, col);

        return applyMove(move);
    }

    // Used when the computer chooses its own move
    public boolean makeComputerMove() {

        if (result.getStatus()
                != GameResult.Status.IN_PROGRESS) {
            return false;
        }

        Move move = moveStrategy.chooseMove(
                board,
                currentPlayer
        );

        if (move == null) {
            return false;
        }

        return applyMove(move);
    }

    // Common logic for both human and computer moves
    private boolean applyMove(Move move) {

        if (result.getStatus()
                != GameResult.Status.IN_PROGRESS) {
            return false;
        }

        boolean successful = board.placeMove(move);

        if (!successful) {
            return false;
        }

        if (rules.hasWon(
                board,
                currentPlayer.getMark()
        )) {

            result = GameResult.win(currentPlayer);
            return true;
        }

        if (rules.isBoardFull(board)) {

            result = GameResult.draw();
            return true;
        }

        switchPlayer();

        return true;
    }

    private void switchPlayer() {

        if (currentPlayer == player1) {
            currentPlayer = player2;
        } else {
            currentPlayer = player1;
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public GameResult getResult() {
        return result;
    }

    public void displayBoard() {
        boardDisplay.display(board);
    }

    public void displayResult() {
        result.displayResult();
    }
}
