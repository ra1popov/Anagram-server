package app.anagram;

import org.java_websocket.WebSocket;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Simple WebSocket signaling server for 2 clients (master and slave).
 * - Buffers messages from the first client until a second connects.
 * - Ensures only 2 clients connected at any time.
 * - If any client disconnects, all connections are closed and buffer cleared.
 */
public class Server extends WebSocketServer {

    private static final int PORT = 8888;

    // Connected clients (protected via synchronized blocks)
    private final Set<WebSocket> clients = new HashSet<>();

    // Buffered messages until second client connects
    private final Queue<String> messages = new LinkedList<>();

    public static void main(String[] args) {
        Server server = new Server(PORT);

        // Disables the timeout completely
        server.setConnectionLostTimeout(0);

        server.start();
    }

    private Server(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        synchronized (clients) {
            System.out.println("New connection: " + conn.getRemoteSocketAddress());

            if (clients.size() >= 2) {
                System.out.println("Connection refused: server allows only 2 clients");
                conn.close(CloseFrame.NORMAL, "Max clients reached");
                return;
            }

            clients.add(conn);

            // If a slave connects (second client), send buffered messages
            if (clients.size() > 1) {
                while (!messages.isEmpty()) {
                    conn.send(messages.poll());
                }
            }
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        synchronized (clients) {
            if (!clients.contains(conn)) {
                return;
            }

            clients.remove(conn);

            System.out.println("Connection closed: " + conn.getRemoteSocketAddress());
            System.out.println("Closing secondary client...");

            // Close all remaining clients
            for (WebSocket client : clients) {
                try {
                    client.close();
                } catch (Exception ignored) {
                }
            }

            clients.clear();
            messages.clear();

            System.out.println("All connections closed and message buffer cleared.");
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        synchronized (clients) {
            // If only one client is connected, buffer the message
            if (clients.size() == 1) {
                messages.add(message);
            } else {
                // If two clients are connected, forward the message to the other client
                for (WebSocket client : clients) {
                    if (!client.equals(conn)) {
                        try {
                            client.send(message);
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onError(WebSocket conn, Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Signaling server started on port: " + getPort());
    }

}