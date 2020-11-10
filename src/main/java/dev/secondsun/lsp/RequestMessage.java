package dev.secondsun.lsp;

import jakarta.json.JsonValue;

public class RequestMessage {
    public String id, method;
    public JsonValue params;
}
