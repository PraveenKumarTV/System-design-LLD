
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomMoveStrategy implements MoveStrategy {

    private final Random random = new Random();

    @Override
    public Move chooseMove(Board board, Player player) {

        List<Move> availableMoves = new ArrayList<>();

        int size = board.getSize();

        // Find all empty cells
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {

                if (board.getCell(row, col) == ' ') {
                    availableMoves.add(
                            player.createMove(row, col)
                    );
                }
            }
        }

        // No moves available
        if (availableMoves.isEmpty()) {
            return null;
        }

        // Choose a random move
        int randomIndex = random.nextInt(
                availableMoves.size()
        );

        return availableMoves.get(randomIndex);
    }
}
