package dev.secondsun.lsp;


import com.google.gson.JsonElement;

public interface LanguageClient {
    public void publishDiagnostics(PublishDiagnosticsParams params);

    public void showMessage(ShowMessageParams params);

    public void registerCapability(String method, JsonElement options);

    public int showMessageRequest(final ShowMessageRequestParams requestParams);

    public void customNotification(String method, JsonElement params);
}
