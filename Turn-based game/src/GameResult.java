
public class GameResult {

    public enum Status {
        IN_PROGRESS,
        WIN,
        DRAW
    }

    private final Status status;
    private final Player winner;

    private GameResult(Status status, Player winner) {
        this.status = status;
        this.winner = winner;
    }

    public static GameResult inProgress() {
        return new GameResult(Status.IN_PROGRESS, null);
    }

    public static GameResult win(Player winner) {
        return new GameResult(Status.WIN, winner);
    }

    public static GameResult draw() {
        return new GameResult(Status.DRAW, null);
    }

    public Status getStatus() {
        return status;
    }

    public Player getWinner() {
        return winner;
    }

    public void displayResult() {

        if (status == Status.IN_PROGRESS) {
            System.out.println("Game is still in progress.");
        } else if (status == Status.WIN) {
            System.out.println(winner.getName() + " has won!");
        } else {
            System.out.println("The game is a draw.");
        }
    }
}
