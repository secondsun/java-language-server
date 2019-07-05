package dev.secondsun.lsp;

import java.util.List;

public class CodeAction {
    public String title, kind;
    public List<Diagnostic> diagnostics;
    public WorkspaceEdit edit;
    public Command command;
}
