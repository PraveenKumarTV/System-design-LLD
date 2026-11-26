
public class Game {

    private Board board;
    private BoardDisplay boardDisplay;
    private GameRules rules;

    private Player player1;
    private Player player2;

    private Player currentPlayer;

    private GameResult result;

    public Game(
            String player1Name,
            String player2Name,
            GameRules rules
    ) {

        board = new Board();
        boardDisplay = new BoardDisplay();

        this.rules = rules;

        player1 = new Player(player1Name, 'X');
        player2 = new Player(player2Name, 'O');

        currentPlayer = player1;

        result = GameResult.inProgress();
    }

    public boolean makeMove(int row, int col) {

        if (result.getStatus() != GameResult.Status.IN_PROGRESS) {
            return false;
        }

        Move move = currentPlayer.createMove(row, col);

        boolean successful = board.placeMove(move);

        if (!successful) {
            return false;
        }

        // Check whether the current player has won
        if (rules.hasWon(board, currentPlayer.getMark())) {

            result = GameResult.win(currentPlayer);
            return true;
        }

        // Check whether the board is full
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
