package server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Database {
    private final String fileName;
    ReadWriteLock lock;
    Lock readLock, writeLock;
    private final JsonObject database;

    public Database(String fileName) {
        this.fileName = fileName;
        database = new JsonObject();
        lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
        try {
            writeLock.lock();
            FileWriter writer = new FileWriter(fileName);
            writer.write("{}");
            writer.close();
            writeLock.unlock();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean setData(JsonElement key, JsonElement value) {
        boolean result = false;
        try {
            writeLock.lock();
            if (key.isJsonPrimitive()) {
                database.add(key.getAsString(), value);
            }
            else if (key.isJsonArray()) {
                JsonArray keys = key.getAsJsonArray();
                String toAdd = keys.remove(keys.size() - 1).getAsString();
                findElement(keys, true).getAsJsonObject().add(toAdd, value);
            }
            FileWriter writer = new FileWriter(fileName);
            writer.write(new Gson().toJson(database));
            writer.close();
            writeLock.unlock();

            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private JsonElement findElement(JsonArray keys, boolean createIfAbsent) {
        JsonElement temp = database;
        if (createIfAbsent) {
            for (JsonElement key : keys) {
                if (!temp.getAsJsonObject().has(key.getAsString())) {
                    temp.getAsJsonObject().add(key.getAsString(), new JsonObject());
                }
                temp = temp.getAsJsonObject().get(key.getAsString());
            }
        } else {
            for (JsonElement key : keys) {
                if (!key.isJsonPrimitive()) {
                    throw new NoSuchElementException();
                }
                temp = temp.getAsJsonObject().get(key.getAsString());
            }
        }
        return temp;
    }

    public JsonElement getData(JsonElement key) {
        try {
            readLock.lock();
            if (key.isJsonPrimitive() && database.has(key.getAsString())) {
                return database.get(key.getAsString());
            } else if (key.isJsonArray()) {
                return findElement(key.getAsJsonArray(), false);
            }
            throw new RuntimeException();
        } finally {
            readLock.unlock();
        }
    }

    public boolean deleteData(JsonElement key) throws IOException {
        try {
            writeLock.lock();
            if (key.isJsonPrimitive() && database.has(key.getAsString())) {
                database.remove(key.getAsString());
                return true;
            } else if (key.isJsonArray()) {
                JsonArray keys = key.getAsJsonArray();
                String toRemove = keys.remove(keys.size() - 1).getAsString();
                findElement(keys, false).getAsJsonObject().remove(toRemove);
                FileWriter writer = new FileWriter(fileName);
                writer.write(new Gson().toJson(database));
                writer.close();
                return true;
            }
            throw new RuntimeException();
        } finally {
            writeLock.unlock();
        }
    }
}
