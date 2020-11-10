package dev.secondsun.lsp;

import jakarta.json.JsonValue;
import java.util.List;

public class CompletionItem {
    public String label;
    public int kind;
    public String detail;
    public MarkupContent documentation;
    public Boolean deprecated, preselect;
    public String sortText, filterText, insertText;
    public Integer insertTextFormat;
    public TextEdit textEdit;
    public List<TextEdit> additionalTextEdits;
    public List<Character> commitCharacters;
    public Command command;
    public JsonValue data;
}
