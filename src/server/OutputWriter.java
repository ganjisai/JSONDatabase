package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class OutputWriter {
    private final Socket socket;
    private DataOutputStream outputStream;

    public OutputWriter(Socket socket) {
        this.socket = socket;
        createOutputStream();
    }
    private void createOutputStream() {
        try {
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendMessage(final String msg) {
        try {
            outputStream.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
