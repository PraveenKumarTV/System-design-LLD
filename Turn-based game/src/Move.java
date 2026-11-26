
public class Move {

    private final int row;
    private final int col;
    private final char mark;

    public Move(int row, int col, char mark) {
        this.row = row;
        this.col = col;
        this.mark = mark;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public char getMark() {
        return mark;
    }
}
