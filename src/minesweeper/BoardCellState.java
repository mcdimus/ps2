package minesweeper;

/**
 * @author Dmitri Maksimov
 */
public enum BoardCellState {

  UNTOUCHED("-"), FLAGGED("F"), DUG(" ");

  private String character;

  BoardCellState(String character) {
    this.character = character;
  }

  public String getCharacter() {
    return character;
  }

  @Override
  public String toString() {
    return getCharacter();
  }

}
