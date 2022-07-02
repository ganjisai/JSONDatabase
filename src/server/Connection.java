package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Connection implements Runnable{
    private final InputReader inputReader;
    private final OutputWriter outputWriter;
    private final Socket socket;
    private final ServerSocket serverSocket;
    private static final boolean DEBUG_MODE = false;
    private final Database database;

    JsonObject answer = new JsonObject();

    public Connection(final Socket socket, ServerSocket serverSocket, Database database) {
        if (DEBUG_MODE) {
            System.out.println("Client connected!");
        }
        this.inputReader = new InputReader(socket);
        this.outputWriter = new OutputWriter(socket);
        this.socket = socket;
        this.serverSocket = serverSocket;
        this.database = database;
    }

    @Override
    public void run() {
        final String rawMessage = inputReader.read();

        GsonFromJson gsonFromJson = new GsonFromJson(rawMessage);
        gsonFromJson.getString();
        String command = gsonFromJson.getType();
        JsonElement key = gsonFromJson.getKey();
        JsonElement value = gsonFromJson.getValue();

        switch (command) {
            case "get" -> {
                JsonElement result = database.getData(key);
                System.out.println(result);
                if (result == null) {
                    answer.addProperty("response", "ERROR");
                    answer.addProperty("reason", "No such key");
                } else {
                    answer.addProperty("response", "OK");
                    answer.add("value", result);
                }
                sentAnswer(answer);
            }
            case "set" -> {
                if (database.setData(key, value)) {
                    answer.addProperty("response", "OK");
                } else {
                    answer.addProperty("response", "ERROR");
                }
                sentAnswer(answer);
            }
            case "delete" -> {
                try {
                    if (database.deleteData(key)) {
                        answer.addProperty("response", "OK");
                    } else {
                        answer.addProperty("response", "ERROR");
                        answer.addProperty("reason", "No such key");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sentAnswer(answer);
            }
            case "exit" -> {
                answer.addProperty("respone", "OK");
                sentAnswer(answer);
                closeSocket();
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        outputWriter.sendMessage(sentAnswer(answer));
    }
    public String sentAnswer(Object obj) {
        return new Gson().toJson(obj);
    }

    private void closeSocket() {
        try {
            socket.close();
        } catch (Exception ignored) {}
    }
}
