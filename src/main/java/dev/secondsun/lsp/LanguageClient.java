package dev.secondsun.lsp;

import jakarta.json.JsonValue;

public interface LanguageClient {
    public void publishDiagnostics(PublishDiagnosticsParams params);

    public void showMessage(ShowMessageParams params);

    public void registerCapability(String method, JsonValue options);

    public int showMessageRequest(final ShowMessageRequestParams requestParams);

    public void customNotification(String method, JsonValue params);
}
