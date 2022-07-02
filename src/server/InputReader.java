package server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class InputReader {
    private final Socket socket;
    private DataInputStream inputStream;

    public InputReader(Socket socket) {
        this.socket = socket;
        createInputStream();
    }
    public String read() {
        try {
            return inputStream.readUTF().trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    private void createInputStream() {
        try {
            inputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
