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

  public void decrement() {
    if (hasBomb() && state == BoardCellState.DUG) {
      this.value = 0;
    } else if (!hasBomb()) {
      this.value = Math.max(this.value - 1, 0);
    }
  }

  public int dig() {
    this.state = BoardCellState.DUG;
    return value;
  }

  @Override
  public String toString() {
    if (this.state == BoardCellState.DUG) {
      switch (this.value) {
        case 0:
          return " ";
        case -1:
          return "*";
        default:
          return Integer.toString(this.value);
      }
    }
    return state.toString();
  }

  public void flag() {
    if (state == BoardCellState.UNTOUCHED) {
      state = BoardCellState.FLAGGED;
    }
  }

  public void deflag() {
    if (state == BoardCellState.FLAGGED) {
      state = BoardCellState.UNTOUCHED;
    }
  }

}
