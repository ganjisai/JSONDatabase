package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private static ServerSocket socket;

    public static void main(String[] args) {

        String PATH = "./src/server/data/";
        Database database = new Database(PATH + "db.json");
        createServerSocket();
        createClientSocket(database);
        closeSocket();
    }
    private static void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void createClientSocket(Database database) {
        while (!socket.isClosed()) {
            final Socket clientSocket = getConnection();
            if (clientSocket != null) {
                new Thread(new Connection(clientSocket, socket, database)).start();
            }
        }
    }
    private static Socket getConnection() {
        try {
            return socket.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static void createServerSocket() {
        final String address = "127.0.0.1";
        final int port = 23456;
        while (true) {
            try {
                socket = new ServerSocket(port, 50, InetAddress.getByName(address));
                System.out.println("Server started!");
                return;
            }  catch (IOException e) {
                System.out.println("[SERVER] Can't create a socket");
            }
        }
    }
}
