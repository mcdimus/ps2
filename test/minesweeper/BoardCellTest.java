package minesweeper;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Dmitri Maksimov
 */
public class BoardCellTest {

  @Test
  public void hasBomb_True_IfValueIsMinus1() throws Exception {
    // given
    BoardCell boardCell = new BoardCell(-1, BoardCellState.UNTOUCHED);

    // when
    boolean hasBomb = boardCell.hasBomb();

    // then
    assertTrue(hasBomb);
  }

  @Test
  public void hasBomb_False_IfValueIs0() throws Exception {
    // given
    BoardCell boardCell = new BoardCell(0, BoardCellState.UNTOUCHED);

    // when
    boolean hasBomb = boardCell.hasBomb();

    // then
    assertFalse(hasBomb);
  }

  @Test
  public void hasBomb_False_IfValueIs1() throws Exception {
    // given
    BoardCell boardCell = new BoardCell(1, BoardCellState.UNTOUCHED);

    // when
    boolean hasBomb = boardCell.hasBomb();

    // then
    assertFalse(hasBomb);
  }

  @Test
  public void increment_Increments_IfNoBomb() throws Exception {
    // given
    BoardCell boardCell = new BoardCell(1, BoardCellState.UNTOUCHED);

    // when
    boardCell.increment();

    // then
    assertEquals(2, boardCell.getValue());
  }

  @Test
  public void increment_DoesNotIncrement_IfBomb() throws Exception {
    // given
    BoardCell boardCell = new BoardCell(-1, BoardCellState.UNTOUCHED);

    // when
    boardCell.increment();

    // then
    assertEquals(-1, boardCell.getValue());
  }

  @Test
  public void decrement_Decrements_IfNoBomb() throws Exception {
    // given
    BoardCell boardCell = new BoardCell(2, BoardCellState.UNTOUCHED);

    // when
    boardCell.decrement();

    // then
    assertEquals(1, boardCell.getValue());
  }

  @Test
  public void decrement_SetsTo0_IfDugBomb() throws Exception {
    // given
    BoardCell boardCell = new BoardCell(-1, BoardCellState.DUG);

    // when
    boardCell.decrement();

    // then
    assertEquals(0, boardCell.getValue());
  }

  @Test
  public void decrement_DoesNothing_IfUntouchedBomb() throws Exception {
    // given
    BoardCell boardCell = new BoardCell(-1, BoardCellState.UNTOUCHED);

    // when
    boardCell.decrement();

    // then
    assertEquals(-1, boardCell.getValue());
  }

  @Test
  public void decrement_DoesNothing_IfFlaggedBomb() throws Exception {
    // given
    BoardCell boardCell = new BoardCell(-1, BoardCellState.FLAGGED);

    // when
    boardCell.decrement();

    // then
    assertEquals(-1, boardCell.getValue());
  }

  @Test
  public void dig_ChangesStateToDUG() throws Exception {
    // given
    BoardCell boardCell = new BoardCell(0, BoardCellState.UNTOUCHED);

    // when
    int dig = boardCell.dig();

    // then
    assertEquals(BoardCellState.DUG, boardCell.getState());
    assertEquals(0, boardCell.getValue());
    assertEquals(0, boardCell.getValue());
  }

  @Test
  public void toString_Dash_IsStateIsUntouched() throws Exception {
    // given
    BoardCell boardCell = new BoardCell(0, BoardCellState.UNTOUCHED);

    // when
    String result = boardCell.toString();

    // then
    assertEquals("-", result);
  }

  @Test
  public void toString_F_IsStateIsFlagged() throws Exception {
    // given
    BoardCell boardCell = new BoardCell(0, BoardCellState.FLAGGED);

    // when
    String result = boardCell.toString();

    // then
    assertEquals("F", result);
  }

  @Test
  public void toString_Space_IsStateIsDugAndValueIs0() throws Exception {
    // given
    BoardCell boardCell = new BoardCell(0, BoardCellState.DUG);

    // when
    String result = boardCell.toString();

    // then
    assertEquals(" ", result);
  }

  @Test
  public void toString_1_IsStateIsDugAndValueIs1() throws Exception {
    // given
    BoardCell boardCell = new BoardCell(1, BoardCellState.DUG);

    // when
    String result = boardCell.toString();

    // then
    assertEquals("1", result);
  }

  @Test
  public void toString_Asterisk_IsStateIsDugAndValueIsMinus1() throws Exception {
    // given
    BoardCell boardCell = new BoardCell(-1, BoardCellState.DUG);

    // when
    String result = boardCell.toString();

    // then
    assertEquals("*", result);
  }

}
