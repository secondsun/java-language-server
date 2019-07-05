package dev.secondsun.lsp;


import javax.json.JsonArray;

public class CodeLens {
    public Range range;
    public Command command;
    public JsonArray data;

    public CodeLens() {}

    public CodeLens(Range range, Command command, JsonArray data) {
        this.range = range;
        this.command = command;
        this.data = data;
    }
}
