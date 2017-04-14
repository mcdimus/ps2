/* Copyright (c) 2007-2017 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package minesweeper.server;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import minesweeper.Board;

/**
 * Multiplayer Minesweeper server.
 */
public class MinesweeperServer implements BoardEventListener, AutoCloseable {

  // System thread safety argument
  //   TODO Problem 5

  /**
   * Default server port.
   */
  private static final int DEFAULT_PORT = 4444;
  /**
   * Maximum port number as defined by ServerSocket.
   */
  private static final int MAXIMUM_PORT = 65535;
  /**
   * Default square board size.
   */
  private static final int DEFAULT_SIZE = 10;

  /**
   * Socket for receiving incoming connections.
   */
  private final ServerSocket serverSocket;
  /**
   * True if the server should *not* disconnect a client after a BOOM message.
   */
  private final boolean debug;

  private ExecutorService threadPool;

  // TODO: Abstraction function, rep invariant, rep exposure
  private Board board;
  private int playersCount;

  /**
   * Make a MinesweeperServer that listens for connections on port.
   *
   * @param port  port number, requires 0 <= port <= 65535
   * @param debug debug mode flag
   * @param board
   * @throws IOException if an error occurs opening the server socket
   */
  public MinesweeperServer(int port, boolean debug, Board board) throws IOException {
    serverSocket = new ServerSocket(port);
    this.debug = debug;
    this.board = board;
    threadPool = Executors.newCachedThreadPool();
  }

  /**
   * Run the server, listening for client connections and handling them.
   * Never returns unless an exception is thrown.
   *
   * @throws IOException if the main server socket is broken
   *                     (IOExceptions from individual clients do *not* terminate serve())
   */
  public void serve() throws IOException {
    System.out.println("Server listening on port " + serverSocket.getLocalPort());
    // listen for clients constantly
    while (true) {
      // block until a client connects
      Socket socket = serverSocket.accept();

      // handle the client
      System.out.println("connection received " + socket.getInetAddress());
      threadPool.execute(new Worker(socket, debug, this));
    }
  }

  /**
   * Start a MinesweeperServer using the given arguments.
   * <p>
   * <br> Usage:
   * MinesweeperServer [--debug | --no-debug] [--port PORT] [--size SIZE_X,SIZE_Y | --file FILE]
   * <p>
   * <br> The --debug argument means the server should run in debug mode. The server should disconnect a
   * client after a BOOM message if and only if the --debug flag was NOT given.
   * Using --no-debug is the same as using no flag at all.
   * <br> E.g. "MinesweeperServer --debug" starts the server in debug mode.
   * <p>
   * <br> PORT is an optional integer in the range 0 to 65535 inclusive, specifying the port the server
   * should be listening on for incoming connections.
   * <br> E.g. "MinesweeperServer --port 1234" starts the server listening on port 1234.
   * <p>
   * <br> SIZE_X and SIZE_Y are optional positive integer arguments, specifying that a random board of size
   * SIZE_X*SIZE_Y should be generated.
   * <br> E.g. "MinesweeperServer --size 42,58" starts the server initialized with a random board of size
   * 42*58.
   * <p>
   * <br> FILE is an optional argument specifying a file pathname where a board has been stored. If this
   * argument is given, the stored board should be loaded as the starting board.
   * <br> E.g. "MinesweeperServer --file boardfile.txt" starts the server initialized with the board stored
   * in boardfile.txt.
   * <p>
   * <br> The board file format, for use with the "--file" option, is specified by the following grammar:
   * <pre>
   *   FILE ::= BOARD LINE+
   *   BOARD ::= X SPACE Y NEWLINE
   *   LINE ::= (VAL SPACE)* VAL NEWLINE
   *   VAL ::= 0 | 1
   *   X ::= INT
   *   Y ::= INT
   *   SPACE ::= " "
   *   NEWLINE ::= "\n" | "\r" "\n"?
   *   INT ::= [0-9]+
   * </pre>
   * <p>
   * <br> If neither --file nor --size is given, generate a random board of size 10x10.
   * <p>
   * <br> Note that --file and --size may not be specified simultaneously.
   *
   * @param args arguments as described
   */
  public static void main(String[] args) {
    // Command-line argument parsing is provided. Do not change this method.
    boolean debug = false;
    int port = DEFAULT_PORT;
    int sizeX = DEFAULT_SIZE;
    int sizeY = DEFAULT_SIZE;
    Optional<File> file = Optional.empty();

    Queue<String> arguments = new LinkedList<String>(Arrays.asList(args));
    try {
      while (!arguments.isEmpty()) {
        String flag = arguments.remove();
        try {
          if (flag.equals("--debug")) {
            debug = true;
          } else if (flag.equals("--no-debug")) {
            debug = false;
          } else if (flag.equals("--port")) {
            port = Integer.parseInt(arguments.remove());
            if (port < 0 || port > MAXIMUM_PORT) {
              throw new IllegalArgumentException("port " + port + " out of range");
            }
          } else if (flag.equals("--size")) {
            String[] sizes = arguments.remove().split(",");
            sizeX = Integer.parseInt(sizes[0]);
            sizeY = Integer.parseInt(sizes[1]);
            file = Optional.empty();
          } else if (flag.equals("--file")) {
            sizeX = -1;
            sizeY = -1;
            file = Optional.of(new File(arguments.remove()));
            if (!file.get().isFile()) {
              throw new IllegalArgumentException("file not found: \"" + file.get() + "\"");
            }
          } else {
            throw new IllegalArgumentException("unknown option: \"" + flag + "\"");
          }
        } catch (NoSuchElementException nsee) {
          throw new IllegalArgumentException("missing argument for " + flag);
        } catch (NumberFormatException nfe) {
          throw new IllegalArgumentException("unable to parse number for " + flag);
        }
      }
    } catch (IllegalArgumentException iae) {
      System.err.println(iae.getMessage());
      System.err.println("usage: MinesweeperServer [--debug | --no-debug] [--port PORT] [--size SIZE_X,SIZE_Y | --file FILE]");
      return;
    }

    try {
      runMinesweeperServer(debug, file, sizeX, sizeY, port);
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  /**
   * Start a MinesweeperServer running on the specified port, with either a random new board or a
   * board loaded from a file.
   *
   * @param debug The server will disconnect a client after a BOOM message if and only if debug is false.
   * @param file  If file.isPresent(), start with a board loaded from the specified file,
   *              according to the input file format defined in the documentation for main(..).
   * @param sizeX If (!file.isPresent()), start with a random board with width sizeX
   *              (and require sizeX > 0).
   * @param sizeY If (!file.isPresent()), start with a random board with height sizeY
   *              (and require sizeY > 0).
   * @param port  The network port on which the server should listen, requires 0 <= port <= 65535.
   * @throws IOException if a network error occurs
   */
  public static void runMinesweeperServer(boolean debug, Optional<File> file, int sizeX, int sizeY, int port) throws IOException {
    Board board = file.map(x -> new Board(x.toPath()))
        .orElseGet(() -> new Board(sizeX, sizeY));

    MinesweeperServer server = new MinesweeperServer(port, debug, board);
    server.serve();
  }

  @Override
  synchronized public String look() {
    return board.look();
  }

  @Override
  synchronized public String dig(int x, int y) {
    return board.dig(x,y);
  }

  @Override
  synchronized public String flag(int x, int y) {
    return board.flag(x,y);
  }

  @Override
  synchronized public String deflag(int x, int y) {
    return board.deflag(x,y);
  }

  @Override
  public void close() throws Exception {
    serverSocket.close();
  }

  @Override
  synchronized public void playerConnected() {
    playersCount++;
  }

  @Override
  synchronized public void playerDisconnected() {
    playersCount--;
  }

  @Override
  synchronized public int getPlayerCount() {
    return playersCount;
  }

  @Override
  public Point getBoardSize() {
    return board.getSize();
  }
}
