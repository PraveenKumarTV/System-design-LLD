
public class Main {

    public static void main(String[] args) {

        Board board = new Board();

        // First move: X places a mark at row 0, column 0
        boolean firstMove = board.placeMark(0, 0, 'X');

        // Second move: O places a mark at row 1, column 1
        boolean secondMove = board.placeMark(1, 1, 'O');

        // Third move: O tries to use an occupied cell
        boolean thirdMove = board.placeMark(0, 0, 'O');

        // Fourth move: X tries an invalid position
        boolean fourthMove = board.placeMark(5, 5, 'X');

        // Display move results
        System.out.println("First move successful: " + firstMove);
        System.out.println("Second move successful: " + secondMove);
        System.out.println("Third move successful: " + thirdMove);
        System.out.println("Fourth move successful: " + fourthMove);

        System.out.println();

        // Display the board
        board.display();
    }
}
