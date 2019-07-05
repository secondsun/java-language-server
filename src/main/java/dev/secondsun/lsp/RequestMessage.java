package dev.secondsun.lsp;


import javax.json.JsonValue;

public class RequestMessage {
    public String id, method;
    public JsonValue params;
}
