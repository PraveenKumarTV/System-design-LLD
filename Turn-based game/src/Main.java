
public class Main {

    public static void main(String[] args) {

        Board board = new Board();
        BoardDisplay boardDisplay = new BoardDisplay();

        Player player1 = new Player("Player 1", 'X');
        Player player2 = new Player("Player 2", 'O');

        Move firstMove = player1.createMove(0, 0);
        Move secondMove = player2.createMove(1, 1);

        board.placeMove(firstMove);
        board.placeMove(secondMove);

        boardDisplay.display(board);

        System.out.println();

        // Represent different possible game results

        GameResult currentResult = GameResult.inProgress();
        currentResult.displayResult();

        GameResult winningResult = GameResult.win(player1);
        winningResult.displayResult();

        GameResult drawResult = GameResult.draw();
        drawResult.displayResult();
    }
}
