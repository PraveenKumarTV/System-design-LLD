
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

    public boolean placeMark(int row, int col, char mark) {

        // Check whether the position is inside the board
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
            return false;
        }

        // Only X and O are allowed
        if (mark != 'X' && mark != 'O') {
            return false;
        }

        // Check whether the cell is already occupied
        if (grid[row][col] != ' ') {
            return false;
        }

        // Place the mark
        grid[row][col] = mark;

        return true;
    }

    public void display() {

        for (int row = 0; row < SIZE; row++) {

            for (int col = 0; col < SIZE; col++) {

                System.out.print(" " + grid[row][col] + " ");

                if (col < SIZE - 1) {
                    System.out.print("|");
                }
            }

            System.out.println();

            if (row < SIZE - 1) {
                System.out.println("---+---+---");
            }
        }
    }
}
