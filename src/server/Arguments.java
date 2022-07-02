package server;

import com.google.gson.JsonElement;

public class Arguments {
    private final String type;
    private final JsonElement key;
    private final JsonElement value;
    private final String fileName;

    public Arguments(String type, JsonElement key, JsonElement value, String fileName) {
        this.type = type;
        this.key = key;
        this.value = value;
        this.fileName = fileName;
    }

    public String getType() {
        return type;
    }

    public JsonElement getKey() {
        return key;
    }

    public JsonElement getValue() {
        return value;
    }

    public String getFileName() {
        return fileName;
    }
}
