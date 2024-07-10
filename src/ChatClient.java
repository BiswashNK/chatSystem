import java.io.*;
import java.net.*;
import java.util.function.Consumer;

import javax.swing.SwingUtilities;

public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Consumer<String> onMessageReceived;

    public ChatClient(String serverAddress, int serverPort, Consumer<String> onMessageReceived) throws IOException {
        try {
            this.socket = new Socket(serverAddress, serverPort);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.onMessageReceived = onMessageReceived;
            onMessageReceived.accept("Connected to the server.");
        } catch (IOException e) {
            onMessageReceived.accept("Error connecting to the server: " + e.getMessage());
            throw e;
        }
    }

    public void sendMessage(String msg) {
        if (out != null) {
            out.println(msg);
        } else {
            System.err.println("Output stream is not initialized.");
        }
    }

    public void startClient() {
        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    final String message = line; 
                    SwingUtilities.invokeLater(() -> onMessageReceived.accept(message));
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> onMessageReceived.accept("Connection lost."));
                e.printStackTrace();
            } finally {
                closeResources();
            }
        }).start();
    }

    public void closeResources() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}