package minesweeper.server;

import java.awt.*;

/**
 * @author Dmitri Maksimov
 */
public interface BoardEventListener {

  String look();

  String dig(int x, int y);

  String flag(int x, int y);

  String deflag(int x, int y);

  void playerConnected();

  void playerDisconnected();

  int getPlayerCount();

  Point getBoardSize();

}
