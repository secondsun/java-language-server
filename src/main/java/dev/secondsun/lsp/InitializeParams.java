package dev.secondsun.lsp;

import jakarta.json.JsonValue;
import java.net.URI;
import java.util.List;

public class InitializeParams {
    public int processId;
    public String rootPath;
    public URI rootUri;
    public JsonValue initializationOptions;
    public String trace;
    public List<WorkspaceFolder> workspaceFolders;
}
