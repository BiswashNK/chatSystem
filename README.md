Java Swing Chat System

A Java-based chat application with a graphical user interface (GUI) built using Java Swing. This system includes a server and multiple clients, where users can send and receive messages in real-time. The server manages the chat history, and new clients receive the entire chat history upon connection.
Features

    Multiple Clients: Server supports multiple clients to connect and chat simultaneously.
    Real-time Messaging: Messages are sent and received in real-time using Java sockets.
    Chat History: New clients receive the entire chat history upon joining the server.
    User-Friendly GUI: The client application is designed with a modern graphical user interface using Java Swing.
    Graceful Disconnection: Clients can exit the chat, and the server handles the disconnection gracefully.
    Server-Side Management: The server broadcasts messages to all connected clients and maintains a list of active users.

Technologies Used

    Java Swing: For building the client-side graphical user interface.
    Java Sockets: For managing server-client communication.
    Multithreading: To handle multiple clients and asynchronous communication.
    Java I/O Streams: For reading and writing data between the server and clients.

Getting Started
Prerequisites

    JDK 8 or higher installed.
    Basic understanding of Java, Swing, and socket programming.

Installation

    Clone the repository: git clone https://github.com/biswashNK/chatSystem

    bash: cd chatSystem




Compile the project: You can compile the code using the javac command:

bash

javac ChatServer.java ChatClient.java ChatClientGUI.java

Run the server: In one terminal, run the server:

bash

java ChatServer

Run the client: In separate terminals, run multiple clients:

bash

    java ChatClientGUI

Usage

    Enter your name: Upon starting the client application, you will be prompted to enter your name.
    Send messages: Type your message in the input field and press Enter to send it.
    Exit chat: Use the Exit button to disconnect gracefully from the chat.

Server Commands

The server automatically starts when running the ChatServer class and listens on port 6000 for incoming client connections. You can stop the server by interrupting the terminal or using the shutdown hook to close all resources.
