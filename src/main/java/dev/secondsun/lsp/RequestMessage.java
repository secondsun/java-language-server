package dev.secondsun.lsp;


import com.google.gson.JsonElement;

public class RequestMessage {
    public String id, method;
    public JsonElement params;
}
