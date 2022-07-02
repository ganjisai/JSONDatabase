package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class GsonFromJson {
    String jsonString;
    Arguments arguments;

    public GsonFromJson(String jsonString) {
        this.jsonString = jsonString;
    }

    public void getString() {
        Gson gson = new Gson();
        arguments = gson.fromJson(jsonString, Arguments.class);
    }
    public String getType() {
        return arguments.getType();
    }
    public JsonElement getKey() {
        return arguments.getKey();
    }
    public JsonElement getValue() {
        return arguments.getValue();
    }
    public String getFileName() {
        return arguments.getFileName();
    }
}
