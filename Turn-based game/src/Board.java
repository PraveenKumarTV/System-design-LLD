
public class Board {

    private final int SIZE = 3;
    private char[][] grid;

    public Board() {
        grid = new char[SIZE][SIZE];

        // Initialize every cell as empty
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                grid[row][col] = ' ';
            }
        }
    }

    public boolean placeMove(Move move) {

        int row = move.getRow();
        int col = move.getCol();
        char mark = move.getMark();

        // Check whether the position is valid
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
            return false;
        }

        // Only X and O are allowed
        if (mark != 'X' && mark != 'O') {
            return false;
        }

        // Check whether the cell is occupied
        if (grid[row][col] != ' ') {
            return false;
        }

        // Place the mark
        grid[row][col] = mark;

        return true;
    }

    public char getCell(int row, int col) {
        return grid[row][col];
    }

    public int getSize() {
        return SIZE;
    }
}
