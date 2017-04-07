package minesweeper;

/**
 * @author Dmitri Maksimov
 */
public class BoardCell {

  private BoardCellState state;
  private int value;

  public BoardCell(int value) {
    this(value, BoardCellState.UNTOUCHED);
  }

  public BoardCell(int value, BoardCellState state) {
    this.state = state;
    this.value = value;
  }

  public BoardCellState getState() {
    return state;
  }

  public int getValue() {
    return value;
  }

  public boolean hasBomb() {
    return this.value == -1;
  }

  public void increment() {
    if (!hasBomb()) {
      this.value++;
    }
  }

  public void dig() {
    this.state = BoardCellState.DUG;
  }

  @Override
  public String toString() {
    if (this.state == BoardCellState.DUG) {
      return Integer.toString(this.value);
    }
    return state.toString();
  }

}
