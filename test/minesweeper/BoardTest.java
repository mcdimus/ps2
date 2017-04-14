/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.assertEquals;

/**
 * TODO: Description
 */
public class BoardTest {

  @Rule
  public TemporaryFolder folder= new TemporaryFolder();

  private Board board;

  // TODO: Testing strategy

  @Test(expected = AssertionError.class)
  public void testAssertionsEnabled() {
    assert false; // make sure assertions are enabled with VM argument: -ea
  }

  @Before
  public void setUp() throws Exception {
    // given
    File file = folder.newFile();
    Files.write(file.toPath(), ("7 7\n" +
        "0 0 0 0 0 0 1\n" +
        "0 0 0 0 1 0 1\n" +
        "0 0 0 0 0 0 1\n" +
        "0 0 0 1 0 0 0\n" +
        "0 0 0 0 1 0 0\n" +
        "0 0 0 0 0 1 0\n" +
        "1 0 0 0 0 0 1\n").getBytes());

    final String boardPath = file.getAbsolutePath();

    // when
    board = new Board(Paths.get(boardPath));
  }

  @Test
  public void testBoardCreation() throws Exception {
    assertEquals("" +
            "0 0 0 1 1 3 *\n" +
            "0 0 0 1 * 4 *\n" +
            "0 0 1 2 2 3 *\n" +
            "0 0 1 * 2 2 1\n" +
            "0 0 1 2 * 2 1\n" +
            "1 1 0 1 2 * 2\n" +
            "* 1 0 0 1 2 *\n",
        board.toString()
    );
  }

  @Test
  public void look_UntouchedBoard() throws Exception {
    // when
    String result = board.look();

    // then
    assertEquals("" +
            "- - - - - - -\n" +
            "- - - - - - -\n" +
            "- - - - - - -\n" +
            "- - - - - - -\n" +
            "- - - - - - -\n" +
            "- - - - - - -\n" +
            "- - - - - - -",
        result
    );
    assertEquals("" +
            "0 0 0 1 1 3 *\n" +
            "0 0 0 1 * 4 *\n" +
            "0 0 1 2 2 3 *\n" +
            "0 0 1 * 2 2 1\n" +
            "0 0 1 2 * 2 1\n" +
            "1 1 0 1 2 * 2\n" +
            "* 1 0 0 1 2 *\n",
        board.toString()
    );
  }

  @Test
  public void dig_IfDugNumber() throws Exception {
    // when
    String result = board.dig(5, 0);

    // then
    assertEquals("" +
            "- - - - - 3 -\n" +
            "- - - - - - -\n" +
            "- - - - - - -\n" +
            "- - - - - - -\n" +
            "- - - - - - -\n" +
            "- - - - - - -\n" +
            "- - - - - - -",
        result
    );
    assertEquals("" +
            "0 0 0 1 1 3 *\n" +
            "0 0 0 1 * 4 *\n" +
            "0 0 1 2 2 3 *\n" +
            "0 0 1 * 2 2 1\n" +
            "0 0 1 2 * 2 1\n" +
            "1 1 0 1 2 * 2\n" +
            "* 1 0 0 1 2 *\n",
        board.toString()
    );
    assertEquals("" +
            "- - - - - 3 -\n" +
            "- - - - - - -\n" +
            "- - - - - - -\n" +
            "- - - - - - -\n" +
            "- - - - - - -\n" +
            "- - - - - - -\n" +
            "- - - - - - -",
        board.look()
    );
  }

  @Test
  public void dig_IfDugEmpty() throws Exception {
    // when
    String result = board.dig(0, 0);

    // then
    assertEquals("" +
            "      1 - - -\n" +
            "      1 - - -\n" +
            "    1 2 - - -\n" +
            "    1 - - - -\n" +
            "    1 2 - - -\n" +
            "1 1   1 2 - -\n" +
            "- 1     1 - -",
        result
    );
    // TODO: probably is incorrect
    assertEquals("" +
            "0 0 0 1 1 3 *\n" +
            "0 0 0 1 * 4 *\n" +
            "0 0 1 2 2 3 *\n" +
            "0 0 1 * 2 2 1\n" +
            "0 0 1 2 * 2 1\n" +
            "1 1 0 1 2 * 2\n" +
            "* 1 0 0 1 2 *\n",
        board.toString()
    );
    assertEquals("" +
            "      1 - - -\n" +
            "      1 - - -\n" +
            "    1 2 - - -\n" +
            "    1 - - - -\n" +
            "    1 2 - - -\n" +
            "1 1   1 2 - -\n" +
            "- 1     1 - -",
        board.look()
    );
  }

  @Test
  public void dig_IfDugBomb() throws Exception {
    // when
    String result = board.dig(6, 0);

    // then
    assertEquals("BOOM!", result);
    assertEquals("" +
            "0 0 0 1 1 2 1\n" +
            "0 0 0 1 * 3 *\n" +
            "0 0 1 2 2 3 *\n" +
            "0 0 1 * 2 2 1\n" +
            "0 0 1 2 * 2 1\n" +
            "1 1 0 1 2 * 2\n" +
            "* 1 0 0 1 2 *\n",
        board.toString()
    );
    assertEquals("" +
            "- - - - - - 1\n" +
            "- - - - - - -\n" +
            "- - - - - - -\n" +
            "- - - - - - -\n" +
            "- - - - - - -\n" +
            "- - - - - - -\n" +
            "- - - - - - -",
        board.look()
    );
  }

  @Test
  public void dig_IfDugBombAdjacentToEmpty() throws Exception {
    // when
    String result = board.dig(3, 3);

    // then
    assertEquals("BOOM!", result);
    assertEquals("" +
            "0 0 0 1 1 3 *\n" +
            "0 0 0 1 * 4 *\n" +
            "0 0 0 1 1 3 *\n" +
            "0 0 0 1 1 2 1\n" +
            "0 0 0 1 * 2 1\n" +
            "1 1 0 1 2 * 2\n" +
            "* 1 0 0 1 2 *\n",
        board.toString()
    );
    assertEquals("" +
            "- - - - - - -\n" +
            "- - - - - - -\n" +
            "- - - - - - -\n" +
            "- - - 1 - - -\n" +
            "- - - - - - -\n" +
            "- - - - - - -\n" +
            "- - - - - - -",
        board.look()
    );
  }

}
