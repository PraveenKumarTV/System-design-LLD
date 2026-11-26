
public class BoardDisplay {

    public void display(Board board) {

        int size = board.getSize();

        for (int row = 0; row < size; row++) {

            for (int col = 0; col < size; col++) {

                System.out.print(" " + board.getCell(row, col) + " ");

                if (col < size - 1) {
                    System.out.print("|");
                }
            }

            System.out.println();

            if (row < size - 1) {
                System.out.println("---+---+---");
            }
        }
    }
}
