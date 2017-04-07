/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * TODO: Specification
 */
public class Board {

  // TODO: Abstraction function, rep invariant, rep exposure, thread safety
  // TODO: Specify, test, and implement in problem 2

  private BoardCell[][] cells;

  public Board(int sizeX, int sizeY) {
    this.cells = generateNewBoard(sizeX, sizeY);
  }

  private BoardCell[][] generateNewBoard(int sizeX, int sizeY) {
    int bombs = sizeX * sizeY / 10;

    BoardCell[][] boardCells = new BoardCell[sizeY][sizeX];
    for (BoardCell[] line : boardCells) {
      for (int i = 0; i < line.length; i++) {
        line[i] = new BoardCell(0);
      }
    }

    for (int i = 0; i < bombs; i++) {
      setRandomBomb(boardCells);
    }

    return boardCells;
  }

  private void setRandomBomb(BoardCell[][] boardCells) {
    int randomY = new Random().nextInt(boardCells.length);
    int randomX = new Random().nextInt(boardCells[randomY].length);

    if (!boardCells[randomY][randomX].hasBomb()) {
      for (int i = coerceIn(randomY - 1, 0, boardCells.length); i <= coerceIn(randomY + 1, 0, boardCells.length); i++) {
        for (int j = coerceIn(randomX - 1, 0, boardCells[randomY].length); j <= coerceIn(randomX + 1, 0, boardCells[randomY].length); j++) {
          if (randomY == i && randomX == j) {
            boardCells[randomY][randomX] = new BoardCell(-1);
          } else {
            boardCells[i][j].increment();
          }
//          System.out.println(toString(boardCells));
        }
      }
    } else {
      setRandomBomb(boardCells);
    }

  }

  private int coerceIn(int i, int min, int max) {
    if (i < min) {
      return min;
    }
    if (i >= max) {
      return max - 1;
    }
    return i;
  }

  @Override
  public String toString() {
    return toString(cells);
  }

  private String toString(BoardCell[][] cells) {
    StringBuilder stringBuilder = new StringBuilder();

    for (BoardCell[] cellLine : cells) {
      String line = Arrays.stream(cellLine)
          .map(x -> x.getValue() < 0 ? "*" : Integer.toString(x.getValue()))
          .collect(Collectors.joining(" "));
      stringBuilder.append(line);
      stringBuilder.append("\n");
    }

    return stringBuilder.toString();
  }

}
