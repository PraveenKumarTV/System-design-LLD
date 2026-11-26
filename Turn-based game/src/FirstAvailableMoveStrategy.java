
public class FirstAvailableMoveStrategy
        implements MoveStrategy {

    @Override
    public Move chooseMove(Board board, Player player) {

        int size = board.getSize();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {

                if (board.getCell(row, col) == ' ') {
                    return player.createMove(row, col);
                }
            }
        }

        return null;
    }
}
