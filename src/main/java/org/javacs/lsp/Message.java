package org.javacs.lsp;


import javax.json.JsonValue;

public class Message {
    public String jsonrpc;
    public Integer id;
    public String method;
    public JsonValue params;
}
