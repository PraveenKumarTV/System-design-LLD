
public interface GameRules {

    boolean hasWon(Board board, char mark);

    boolean isBoardFull(Board board);
}
