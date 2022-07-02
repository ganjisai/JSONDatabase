package client;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;


import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import server.InputReader;
import server.OutputWriter;

public class Main {
    private static Socket clientSocket;
    @Parameter(names = {"--type", "-t"})
    String type;
    @Parameter(names = {"-key", "-k"})
    String key;
    @Parameter(names = {"--values", "-v"})
    String value;
    @Parameter(names = {"--input", "-in"})
    String fileName;

    Map<String, String> request = new LinkedHashMap<>();

    public static void main(String[] args) {
        Main main = new Main();
        JCommander.newBuilder().addObject(main).build().parse(args);
        main.run();
    }

    private void run() {
        createSocket();
        InputReader inputReader = new InputReader(clientSocket);
        OutputWriter outputWriter = new OutputWriter(clientSocket);
        String output = "";
        System.out.println("Client started!");
        if (fileName == null) {
            createRequest(type, key, value);
            Gson gson = new Gson();
            output = gson.toJson(request);
        }
        else {
            try {
                String PATH = "./src/client/data/";
                File file = new File(PATH + fileName);
                Scanner scanner = new Scanner(file);
                output = scanner.nextLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Sent: " + output);
        outputWriter.sendMessage(output);

        String received = inputReader.read().trim();
        System.out.println("Received: " + received);

        closeSocket();
    }
    private void createRequest(String type, String key, String value) {
        request.put("type", type);
        if (key != null) {
            request.put("key", key);
        }
        if (value != null) {
            request.put("value", value);
        }
    }
    public static void closeSocket() {
        try {
            clientSocket.close();
        } catch (Exception ignored) {}
    }
    public static void createSocket() {
        final String address = "127.0.0.1";
        final int port = 23456;
        while (true) {
            try {
                clientSocket = new Socket(InetAddress.getByName(address), port);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
