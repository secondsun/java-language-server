package dev.secondsun.lsp;


import com.google.gson.JsonElement;

public class Message {
    public String jsonrpc;
    public Integer id;
    public String method;
    public JsonElement params;
    // Overloading for response messages
    public JsonElement result;
    public JsonElement error;



}
