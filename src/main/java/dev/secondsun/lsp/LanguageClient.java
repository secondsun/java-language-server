package dev.secondsun.lsp;


import javax.json.JsonValue;

public interface LanguageClient {
    public void publishDiagnostics(PublishDiagnosticsParams params);

    public void showMessage(ShowMessageParams params);

    public void registerCapability(String method, JsonValue options);

    public void customNotification(String method, JsonValue params);
}
