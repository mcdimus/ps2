package minesweeper.server;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import minesweeper.Board;
import static org.junit.Assert.*;

/**
 * @author Dmitri Maksimov
 */
public class MinesweeperServerTest {

  private Board board;
  private MinesweeperServer server;

  @Before
  public void setUp() throws Exception {
    // given
    String boardFile = "test_board.txt";
    final URL boardURL = ClassLoader.getSystemClassLoader().getResource("minesweeper/" + boardFile);
    if (boardURL == null) {
      throw new IOException("Failed to locate resource " + boardFile);
    }
    final String boardPath;
    try {
      boardPath = new File(boardURL.toURI()).getAbsolutePath();
    } catch (URISyntaxException urise) {
      throw new IOException("Invalid URL " + boardURL, urise);
    }

    // when
    board = new Board(Paths.get(boardPath));
    server = new MinesweeperServer(10000, false, board);
  }

  @After
  public void tearDown() throws Exception {
    server.close();
  }

  @Test
  public void look_UntouchedBoard() throws Exception {
    // when
    String result = server.look();

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
    String result = server.dig(5,0);

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
    String result = server.dig(0,0);

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
    String result = server.dig(6,0);

    // then
    assertEquals("BOOM!",result);
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
    String result = server.dig(3,3);

    // then
    assertEquals("BOOM!",result);
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
