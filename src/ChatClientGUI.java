import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatClientGUI extends JFrame {
    private static final long serialVersionUID = 1L;
	private JTextArea messageArea;
    private JTextField textField;
    private JButton exitButton;
    private JLabel statusLabel;
    private ChatClient client;

    public ChatClientGUI() {
        super("Chat Application");
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Color backgroundColor = Color.DARK_GRAY;
        Color buttonColor = new Color(75, 75, 75);
        Color textColor = Color.white;
        Font textFont = new Font("Arial", Font.PLAIN, 14);
        Font buttonFont = new Font("Arial", Font.BOLD, 12);

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setBackground(backgroundColor);
        messageArea.setForeground(textColor);
        messageArea.setFont(textFont);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        add(scrollPane, BorderLayout.CENTER);

        String name = JOptionPane.showInputDialog(this, "Enter your name:", "Name Entry", JOptionPane.PLAIN_MESSAGE);
        if (name == null || name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Invalid Name", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        this.setTitle("Chat Application - " + name);

        textField = new JTextField();
        textField.setFont(textFont);
        textField.setForeground(textColor);
        textField.setBackground(Color.gray);
        textField.addActionListener(e -> {
            String messageText = textField.getText().trim();
            if (!messageText.isEmpty()) {
                String message = "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] " + name + ": " + messageText;
                client.sendMessage(message);
                textField.setText("");
            }
        });

        exitButton = new JButton("Exit");
        exitButton.setFont(buttonFont);
        exitButton.setBackground(buttonColor);
        exitButton.setForeground(Color.black);

        exitButton.addActionListener(e -> {
            String departureMessage = name + " has left the chat.";
            client.sendMessage(departureMessage);
            
            // Ensure the message is sent before closing resources and exiting
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Thread.sleep(10); // Ensure the message is sent
                    return null;
                }

                @Override
                protected void done() {
                    client.closeResources(); // Close resources properly
                    
                                    }
            }.execute();
        });


        statusLabel = new JLabel("Connecting...");
        statusLabel.setFont(buttonFont);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(backgroundColor);
        bottomPanel.add(textField, BorderLayout.CENTER);
        bottomPanel.add(exitButton, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
        add(statusLabel, BorderLayout.NORTH);

        try {
            this.client = new ChatClient("127.0.0.1", 6000, this::onMessageReceived);
            client.startClient();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to the server", "Connection error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void onMessageReceived(String message) {
        SwingUtilities.invokeLater(() -> {
            messageArea.append(message + "\n");
            if (message.contains("Connected to the server.")) {
                statusLabel.setText("Connected");
            } else if (message.contains("Connection lost.")) {
                statusLabel.setText("Disconnected");
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatClientGUI().setVisible(true));
    }
}
