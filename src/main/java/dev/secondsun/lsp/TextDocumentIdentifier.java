package dev.secondsun.lsp;

import java.net.URI;

public class TextDocumentIdentifier {
    public URI uri;

    public TextDocumentIdentifier() {}

    public TextDocumentIdentifier(URI uri) {
        this.uri = uri;
    }
}
