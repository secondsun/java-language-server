package dev.secondsun.lsp;


import javax.json.JsonValue;

public class Message {
    public String jsonrpc;
    public Integer id;
    public String method;
    public JsonValue params;
    //Overloading for response messages
    public JsonValue result;
    public JsonValue error;
}
