/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import static java.util.stream.Collectors.joining;

/**
 * TODO: Specification
 */
public class Board {

  // TODO: Abstraction function, rep invariant, rep exposure, thread safety
  // TODO: Specify, test, and implement in problem 2

  private volatile BoardCell[][] cells;

  public Board(int sizeX, int sizeY) {
    this.cells = generateNewBoard(sizeX, sizeY);
  }

  public Board(Path filePath) {
    this.cells = generateNewBoardFromFile(filePath);
  }

  private BoardCell[][] generateNewBoardFromFile(Path filePath) {
    try {
      List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
      String[] sizes = lines.remove(0).split(" ");
      int sizeX = Integer.parseInt(sizes[0]);
      int sizeY = Integer.parseInt(sizes[1]);

      BoardCell[][] boardCells = initBoard(sizeX, sizeY);

      for (int i = 0; i < sizeY; i++) {
        String[] values = lines.get(i).split(" ");
        for (int j = 0; j < sizeX; j++) {
          if (values[j].equals("1")) {
            setBomb(boardCells, j, i);
          }
        }
      }
      return boardCells;
    } catch (IOException e) {
      e.printStackTrace();
    }
    throw new IllegalStateException("should not reach");
  }

  private BoardCell[][] generateNewBoard(int sizeX, int sizeY) {
    BoardCell[][] boardCells = initBoard(sizeX, sizeY);

    int bombs = sizeX * sizeY / 10;
    for (int i = 0; i < bombs; i++) {
      setRandomBomb(boardCells);
    }

    return boardCells;
  }

  private BoardCell[][] initBoard(int sizeX, int sizeY) {
    BoardCell[][] boardCells = new BoardCell[sizeY][sizeX];
    for (BoardCell[] line : boardCells) {
      for (int i = 0; i < line.length; i++) {
        line[i] = new BoardCell(0);
      }
    }
    return boardCells;
  }

  private void setRandomBomb(BoardCell[][] boardCells) {
    int randomY = new Random().nextInt(boardCells.length);
    int randomX = new Random().nextInt(boardCells[randomY].length);

    if (!boardCells[randomY][randomX].hasBomb()) {
      setBomb(boardCells, randomX, randomY);
    } else {
      setRandomBomb(boardCells);
    }
  }

  private void setBomb(BoardCell[][] boardCells, int bombX, int bombY) {
    for (int i = coerceIn(bombY - 1, 0, boardCells.length); i <= coerceIn(bombY + 1, 0, boardCells.length); i++) {
      for (int j = coerceIn(bombX - 1, 0, boardCells[bombY].length); j <= coerceIn(bombX + 1, 0, boardCells[bombY].length); j++) {
        if (bombY == i && bombX == j) {
          boardCells[bombY][bombX] = new BoardCell(-1);
        } else {
          boardCells[i][j].increment();
        }
      }
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
          .collect(joining(" "));
      stringBuilder.append(line);
      stringBuilder.append("\n");
    }

    return stringBuilder.toString();
  }

  public String look() {
    StringBuilder stringBuilder = new StringBuilder();

    for (BoardCell[] cellLine : cells) {
      String line = Arrays.stream(cellLine)
          .map(BoardCell::toString)
          .collect(joining(" "));
      stringBuilder.append(line);
      stringBuilder.append("\n");
    }

    return stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString();
  }

  public String dig(int x, int y) {
    if (cells[y][x].getState() == BoardCellState.FLAGGED) {
      return look();
    }

    int val = cells[y][x].dig();
    if (val > 0) {
      return look();
    } else if (val == -1) {
      int countAdjacentBombs = 0;
      for (int i = coerceIn(y - 1, 0, cells.length); i <= coerceIn(y + 1, 0, cells.length); i++) {
        for (int j = coerceIn(x - 1, 0, cells[y].length); j <= coerceIn(x + 1, 0, cells[y].length); j++) {
          BoardCell cell = cells[i][j];
          if (cell.hasBomb() && !(i == y && j == x)) {
            countAdjacentBombs++;
          }
          cell.decrement();
        }
      }
      cells[y][x] = new BoardCell(countAdjacentBombs, BoardCellState.DUG);
      if (cells[y][x].getValue() == 0) {
        digEmpty(x, y);
      }
      return "BOOM!";
    } else {
      digEmpty(x, y);
      return look();
    }
  }

  private void digEmpty(int x, int y) {
    for (int i = coerceIn(y - 1, 0, cells.length); i <= coerceIn(y + 1, 0, cells.length); i++) {
      for (int j = coerceIn(x - 1, 0, cells[y].length); j <= coerceIn(x + 1, 0, cells[y].length); j++) {
        BoardCell cell = cells[i][j];
        if (cell.getState() == BoardCellState.UNTOUCHED) {
          int val = cell.dig();
          if (val == 0) {
            digEmpty(j, i);
          }
        }
      }
    }
  }

  public String flag(int x, int y) {
    cells[y][x].flag();
    return look();
  }

  public String deflag(int x, int y) {
    cells[y][x].deflag();
    return look();
  }

  public Point getSize() {
    return new Point(cells[0].length, cells.length);
  }

}
