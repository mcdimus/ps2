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

}
