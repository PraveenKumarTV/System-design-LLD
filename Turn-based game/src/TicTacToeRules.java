
public class TicTacToeRules implements GameRules {

    @Override
    public boolean hasWon(Board board, char mark) {

        int size = board.getSize();

        // Check rows
        for (int row = 0; row < size; row++) {

            boolean rowMatch = true;

            for (int col = 0; col < size; col++) {
                if (board.getCell(row, col) != mark) {
                    rowMatch = false;
                    break;
                }
            }

            if (rowMatch) {
                return true;
            }
        }

        // Check columns
        for (int col = 0; col < size; col++) {

            boolean colMatch = true;

            for (int row = 0; row < size; row++) {
                if (board.getCell(row, col) != mark) {
                    colMatch = false;
                    break;
                }
            }

            if (colMatch) {
                return true;
            }
        }

        // Check main diagonal
        boolean mainDiagonalMatch = true;

        for (int i = 0; i < size; i++) {
            if (board.getCell(i, i) != mark) {
                mainDiagonalMatch = false;
                break;
            }
        }

        if (mainDiagonalMatch) {
            return true;
        }

        // Check opposite diagonal
        boolean otherDiagonalMatch = true;

        for (int i = 0; i < size; i++) {
            if (board.getCell(i, size - 1 - i) != mark) {
                otherDiagonalMatch = false;
                break;
            }
        }

        return otherDiagonalMatch;
    }

    @Override
    public boolean isBoardFull(Board board) {

        int size = board.getSize();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {

                if (board.getCell(row, col) == ' ') {
                    return false;
                }
            }
        }

        return true;
    }
}
