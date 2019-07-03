package org.javacs.lsp;


import javax.json.JsonValue;

public class ResponseMessage {
    public String id;
    public JsonValue result;
    public ResponseError error;
}
