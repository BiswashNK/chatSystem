import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {
    private static List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private static List<String> chatHistory = new ArrayList<>();
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(6000);
            System.out.println("Server started. Waiting for clients...");

            // Add shutdown hook to handle server closure
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutdown hook triggered. Closing server...");
                closeServer();
            }));

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket);
                    ClientHandler clientThread = new ClientHandler(clientSocket, clients, chatHistory);
                    clients.add(clientThread);
                    new Thread(clientThread).start();
                } catch (SocketException e) {
                    // Server socket was closed, exit loop
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeServer();
        }
    }

    // Method to close the server and all client connections
    public static void closeServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            for (ClientHandler client : clients) {
                client.closeResources();
            }
            System.out.println("Server and all client connections closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to check if there are no active clients
    public static boolean noActiveClients() {
        return clients.isEmpty();
    }

    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private List<ClientHandler> clients;
        private List<String> chatHistory;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket, List<ClientHandler> clients, List<String> chatHistory) throws IOException {
            this.clientSocket = socket;
            this.clients = clients;
            this.chatHistory = chatHistory;
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }

        public void run() {
            try {
                // Send chat history to the new client
                sendChatHistoryToClient();

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    chatHistory.add(inputLine);
                    broadcastMessage(inputLine);
                }
            } catch (IOException e) {
                System.out.println("An error occurred: " + e.getMessage());
            } finally {
                closeResources();
                clients.remove(this);
                System.out.println("Client disconnected: " + clientSocket);
                checkAndCloseServer();
            }
        }

        private void sendChatHistoryToClient() {
            for (String message : chatHistory) {
                out.println(message);
            }
        }

        private void broadcastMessage(String message) {
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }

        public void closeResources() {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void checkAndCloseServer() {
            if (ChatServer.noActiveClients()) {
                ChatServer.closeServer();
            }
        }
    }
}
