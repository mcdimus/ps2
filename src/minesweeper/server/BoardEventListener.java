package minesweeper.server;

/**
 * @author Dmitri Maksimov
 */
public interface BoardEventListener {

  String look();

  String dig(int x, int y);

}
