package minesweeper.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author Dmitri Maksimov
 */
public class Worker implements Runnable {

  private final boolean debug;
  private final BoardEventListener boardEventListener;
  private final Socket socket;
  private boolean keepListening = true; // when false, ends runnable

  public Worker(Socket socket, boolean debug, BoardEventListener boardEventListener) {
    this.socket = socket;
    this.debug = debug;
    this.boardEventListener = boardEventListener;
  }

  @Override
  public void run() {
    PrintWriter out = null;
    try {
      out = new PrintWriter(socket.getOutputStream(), true);
    } catch (IOException e) {
      e.printStackTrace();
    }
    out.printf(
        "Welcome to Minesweeper. " +
            "Players: %d including you. " +
            "Board: %d columns by %d rows. " +
            "Type 'help' for help.\r\n",
        0, 0, 0
    );

    while (keepListening) {
      // handle the client
      try {
        System.out.println("listening " + Thread.currentThread().getName());
        handleConnection(socket);
      } catch (IOException ioe) {
        ioe.printStackTrace(); // but don't terminate serve()
      } finally {
        stopListening();
      }
    }
    try {
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Handle a single client connection. Returns when client disconnects.
   *
   * @param socket socket where the client is connected
   * @throws IOException if the connection encounters an error or terminates unexpectedly
   */
  private void handleConnection(Socket socket) throws IOException {
    try (
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
    ) {
      for (String line = in.readLine(); line != null; line = in.readLine()) {
        String output = handleRequest(line);
        if (output != null) {
          // TODO: Consider improving spec of handleRequest to avoid use of null
          out.println(output);
        } else {
          break;
        }
      }
    }
  }

  /**
   * Handler for client input, performing requested operations and returning an output message.
   *
   * @param input message from client
   * @return message to client, or null if none
   */
  private String handleRequest(String input) {
    String regex = "(look)|(help)|(bye)|"
        + "(dig -?\\d+ -?\\d+)|(flag -?\\d+ -?\\d+)|(deflag -?\\d+ -?\\d+)";
    if (!input.matches(regex)) {
      // invalid input
      // TODO Problem 5
    }
    String[] tokens = input.split(" ");
    if (tokens[0].equals("look")) {
      // 'look' request
      return boardEventListener.look();
    } else if (tokens[0].equals("help")) {
      // 'help' request
      return "RTFM!";
    } else if (tokens[0].equals("bye")) {
      // 'bye' request
      stopListening();
      return null;
    } else {
      int x = Integer.parseInt(tokens[1]);
      int y = Integer.parseInt(tokens[2]);
      if (tokens[0].equals("dig")) {
        // 'dig x y' request
        return boardEventListener.dig(x, y);
      } else if (tokens[0].equals("flag")) {
        // 'flag x y' request
        // TODO Problem 5
      } else if (tokens[0].equals("deflag")) {
        // 'deflag x y' request
        // TODO Problem 5
      }
    }
    // TODO: Should never get here, make sure to return in each of the cases above
    throw new UnsupportedOperationException();
  }

  // stop listening for incoming messages
  public void stopListening() {
    keepListening = false;
  }

}
