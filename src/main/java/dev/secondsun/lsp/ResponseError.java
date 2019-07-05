package dev.secondsun.lsp;


import javax.json.JsonValue;

public class ResponseError {
    public int code;
    public String message;
    public JsonValue data;

    public ResponseError() {}

    public ResponseError(int code, String message, JsonValue data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
