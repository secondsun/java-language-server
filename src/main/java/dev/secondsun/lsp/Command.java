package dev.secondsun.lsp;


import javax.json.JsonArray;

public class Command {
    public String title, command;
    public JsonArray arguments;

    public Command() {}

    public Command(String title, String command, JsonArray arguments) {
        this.title = title;
        this.command = command;
        this.arguments = arguments;
    }
}
