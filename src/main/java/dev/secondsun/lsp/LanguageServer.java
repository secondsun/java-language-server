package dev.secondsun.lsp;

import java.util.List;
import java.util.Optional;

public class LanguageServer {
    public InitializeResult initialize(InitializeParams params) {
        throw new RuntimeException("Unimplemented");
    }

    public void initialized() {}

    public void shutdown() {}

    public void didChangeConfiguration(DidChangeConfigurationParams params) {}

    public void didOpenTextDocument(DidOpenTextDocumentParams params) {}

    public void didChangeTextDocument(DidChangeTextDocumentParams params) {}

    public void willSaveTextDocument(WillSaveTextDocumentParams params) {}

    public List<TextEdit> willSaveWaitUntilTextDocument(WillSaveTextDocumentParams params) {
        return List.of();
    }

    public void didSaveTextDocument(DidSaveTextDocumentParams params) {}

    public void didCloseTextDocument(DidCloseTextDocumentParams params) {}

    public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {}

    public Optional<CompletionList> completion(TextDocumentPositionParams params) {
        throw new RuntimeException("Unimplemented");
    }

    public CompletionItem resolveCompletionItem(CompletionItem params) {
        throw new RuntimeException("Unimplemented");
    }

    public Optional<Hover> hover(TextDocumentPositionParams params) {
        throw new RuntimeException("Unimplemented");
    }

    public Optional<SignatureHelp> signatureHelp(TextDocumentPositionParams params) {
        throw new RuntimeException("Unimplemented");
    }

    public Optional<List<Location>> gotoDefinition(TextDocumentPositionParams params) {
        throw new RuntimeException("Unimplemented");
    }

    public Optional<List<Location>> findReferences(ReferenceParams params) {
        throw new RuntimeException("Unimplemented");
    }

    public List<SymbolInformation> documentSymbol(DocumentSymbolParams params) {
        throw new RuntimeException("Unimplemented");
    }

    public List<SymbolInformation> workspaceSymbols(WorkspaceSymbolParams params) {
        throw new RuntimeException("Unimplemented");
    }

    public List<CodeAction> codeAction(CodeActionParams params) {
        throw new RuntimeException("Unimplemented");
    }

    public List<CodeLens> codeLens(CodeLensParams params) {
        throw new RuntimeException("Unimplemented");
    }

    public CodeLens resolveCodeLens(CodeLens params) {
        throw new RuntimeException("Unimplemented");
    }

    public Optional<RenameResponse> prepareRename(TextDocumentPositionParams params) {
        throw new RuntimeException("Unimplemented");
    }

    public WorkspaceEdit rename(RenameParams params) {
        throw new RuntimeException("Unimplemented");
    }

    public void didChangeWorkspaceFolders(DidChangeWorkspaceFoldersParams params) {
        throw new RuntimeException("Unimplemented");
    }

    public List<TextEdit> formatting(DocumentFormattingParams params) {
        throw new RuntimeException("Unimplemented");
    }

    public List<FoldingRange> foldingRange(FoldingRangeParams params) {
        throw new RuntimeException("Unimplemented");
    }

    public List<DocumentLink> documentLink(DocumentLinkParams params) {
        throw new RuntimeException("Unimplemented");
    }

    public void doAsyncWork() {}

    /**
     * This should call the client, and save the id that is returned
     * @param params
     */
    public void showMessageRequest(ShowMessageRequestParams params) {
        throw new RuntimeException("Unimplemented");
    }

    /**
     * A previous call to showMessageReques should cache the id and handle the result.  This method is called automatically
     *
     * @param id if os showMessageRequest
     * @param result ActionItem that was chosen by the user.
     */
    public void handleShowMessageRequestResponse(int id, MessageActionItem result) {
        throw new RuntimeException("Unimplemented");
    }
}
