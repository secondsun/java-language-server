package org.javacs.lsp;


import javax.json.JsonObject;

public class InitializeResult {
    public JsonObject capabilities;

    public InitializeResult() {}

    public InitializeResult(JsonObject capabilities) {
        this.capabilities = capabilities;
    }
}
