
public class Game {

    private Board board;
    private BoardDisplay boardDisplay;

    private Player player1;
    private Player player2;

    private Player currentPlayer;

    private GameResult result;

    public Game(String player1Name, String player2Name) {

        board = new Board();
        boardDisplay = new BoardDisplay();

        player1 = new Player(player1Name, 'X');
        player2 = new Player(player2Name, 'O');

        // Player 1 starts the game
        currentPlayer = player1;

        result = GameResult.inProgress();
    }

    public boolean makeMove(int row, int col) {

        // Do not allow moves after the game ends
        if (result.getStatus() != GameResult.Status.IN_PROGRESS) {
            return false;
        }

        // Current player creates their move
        Move move = currentPlayer.createMove(row, col);

        // Try to place the move on the board
        boolean successful = board.placeMove(move);

        // If the move is invalid, do not change the turn
        if (!successful) {
            return false;
        }

        // Switch player after a successful move
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
}
