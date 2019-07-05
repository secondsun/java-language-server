package dev.secondsun.lsp;

import javax.json.JsonValue;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LSP {
    private static final Jsonb jsonb = JsonbBuilder
            .create(new JsonbConfig().withDeserializers(MarkedString.Adapter.INSTANCE).withSerializers(MarkedString.Adapter.INSTANCE));


    private static String readHeader(InputStream client) {
        var line = new StringBuilder();
        for (var next = read(client); true; next = read(client)) {
            if (next == '\r') {
                var last = read(client);
                assert last == '\n';
                break;
            }
            line.append(next);
        }
        return line.toString();
    }

    private static int parseHeader(String header) {
        var contentLength = "Content-Length: ";
        if (header.startsWith(contentLength)) {
            var tail = header.substring(contentLength.length());
            var length = Integer.parseInt(tail);
            return length;
        }
        return -1;
    }

    static class EndOfStream extends RuntimeException {
    }

    private static char read(InputStream client) {
        try {
            var c = client.read();
            if (c == -1) {
                LOG.warning("Stream from client has been closed, throwing kill exception...");
                throw new EndOfStream();
            }
            return (char) c;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String readLength(InputStream client, int byteLength) {
        // Eat whitespace
        // Have observed problems with extra \r\n sequences from VSCode
        var next = read(client);
        while (Character.isWhitespace(next)) {
            next = read(client);
        }
        // Append next
        var result = new StringBuilder();
        var i = 0;
        while (true) {
            result.append(next);
            i++;
            if (i == byteLength) break;
            next = read(client);
        }
        return result.toString();
    }

    static String nextToken(InputStream client) {
        var contentLength = -1;
        while (true) {
            var line = readHeader(client);
            // If header is empty, next line is the start of the message
            if (line.isEmpty()) return readLength(client, contentLength);
            // If header contains length, save it
            var maybeLength = parseHeader(line);
            if (maybeLength != -1) contentLength = maybeLength;
        }
    }

    static Message parseMessage(String token) {
        return jsonb.fromJson(token, Message.class);
    }

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private static void writeClient(OutputStream client, String messageText) {
        var messageBytes = messageText.getBytes(UTF_8);
        var headerText = String.format("Content-Length: %d\r\n\r\n", messageBytes.length);
        var headerBytes = headerText.getBytes(UTF_8);
        try {
            client.write(headerBytes);
            client.write(messageBytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static String toJson(Object message) {
        if (message == null) {
            return "null";
        }
        return jsonb.toJson(message);
    }

    static void respond(OutputStream client, int requestId, Object params) {
        if (params instanceof Optional) {
            var option = (Optional) params;
            params = option.orElse(null);
        }
        var jsonText = toJson(params);
        var messageText = String.format("{\"jsonrpc\":\"2.0\",\"id\":%d,\"result\":%s}", requestId, jsonText);
        writeClient(client, messageText);
    }

    private static void notifyClient(OutputStream client, String method, Object params) {
        if (params instanceof Optional) {
            var option = (Optional) params;
            params = option.orElse(null);
        }
        var jsonText = toJson(params);
        var messageText = String.format("{\"jsonrpc\":\"2.0\",\"method\":\"%s\",\"params\":%s}", method, jsonText);
        writeClient(client, messageText);
    }

    /**
     * 
     * @param client output stream writes to client
     * @param method The method to be invoked.
     * @param params The method's params (gets turned into a json string)
     * @return request id to be used to handle the response from the client
     */
    private static int requestClient(OutputStream client, String method, Object params) {
        if (params instanceof Optional) {
            var option = (Optional) params;
            params = option.orElse(null);
        }
        
        var id = (int) (Math.random() * 20000);
        
        var jsonText = toJson(params);
        var messageText = String.format("{\"jsonrpc\":\"2.0\",\"method\":\"%s\",\"params\":%s,\"id\":%d}", method, jsonText, id);
        writeClient(client, messageText);
        return id;
    }
    
    private static class RealClient implements LanguageClient {
        final OutputStream send;
        final InputStream recv;

        RealClient(OutputStream send, InputStream recv) {
            this.send = send;
            this.recv = recv;
        }

        @Override
        public void publishDiagnostics(PublishDiagnosticsParams params) {
            notifyClient(send, "textDocument/publishDiagnostics", params);
        }

        @Override
        public void showMessage(ShowMessageParams params) {
            notifyClient(send, "window/showMessage", params);
        }

        @Override
        public void registerCapability(String method, JsonValue options) {
            var params = new RegistrationParams();
            params.id = UUID.randomUUID().toString();
            params.method = method;
            params.registerOptions = options;

            notifyClient(send, "client/registerCapability", params);
        }

        @Override
        public void customNotification(String method, JsonValue params) {
            notifyClient(send, method, params);
        }

        @Override
        public int showMessageRequest(ShowMessageRequestParams requestParams) {
            return requestClient(send, "window/showMessageRequest", requestParams);
        }
    }

    public static void connect(
            Function<LanguageClient, LanguageServer> serverFactory, InputStream receive, OutputStream send) {
        var server = serverFactory.apply(new RealClient(send, receive));
        var pending = new ArrayBlockingQueue<Message>(10);
        var endOfStream = new Message();

        // Read messages and process cancellations on a separate thread
        class MessageReader implements Runnable {
            void peek(Message message) {
                if (message.method != null) {//request
                    if (message.method.equals("$/cancelRequest")) {
                        var params = jsonb.fromJson(message.params.toString(), CancelParams.class);
                        var removed = pending.removeIf(r -> r.id != null && r.id.equals(params.id));
                        if (removed) LOG.info(String.format("Cancelled request %d, which had not yet started", params.id));
                        else LOG.info(String.format("Cannot cancel request %d because it has already started", params.id));
                    }
                }
            }

            private boolean kill() {
                LOG.info("Read stream has been closed, putting kill message onto queue...");
                try {
                    pending.put(endOfStream);
                    return true;
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Failed to put kill message onto queue, will try again...", e);
                    return false;
                }
            }

            @Override
            public void run() {
                LOG.info("Placing incoming messages on queue...");

                while (true) {
                    try {
                        var token = nextToken(receive);
                        var message = parseMessage(token);
                        peek(message);
                        pending.put(message);
                    } catch (EndOfStream __) {
                        if (kill()) return;
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, e.getMessage(), e);
                    }
                }
            }
        }
        Thread reader = new Thread(new MessageReader(), "reader");
        reader.setDaemon(true);
        reader.start();

        // Process messages on main thread
        LOG.info("Reading messages from queue...");
        var hasAsyncWork = false;
        processMessages:
        while (true) {
            Message r;
            try {
                // Take a break periodically
                r = pending.poll(200, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
                continue;
            }
            // If receive has been closed, exit
            if (r == endOfStream) {
                LOG.warning("Stream from client has been closed, exiting...");
                break processMessages;
            }
            // If poll(_) failed, loop again
            if (r == null) {
                if (hasAsyncWork) {
                    server.doAsyncWork();
                    hasAsyncWork = false;
                }
                continue;
            }
            // Otherwise, process the new message
            hasAsyncWork = true;
            try {
                if (r.method == null) {
                    MessageActionItem result = jsonb.fromJson(r.result.toString(), MessageActionItem.class);
                    int id = r.id;
                    if (r.error != null && !r.error.toString().isBlank()) {
                        LOG.severe(r.error.toString());
                        break;
                    }
                    server.handleShowMessageRequestResponse(id, result);

                    break;
                } else {
                switch (r.method) {
                    case "initialize": {
                        var params = jsonb.fromJson(r.params.toString(), InitializeParams.class);
                        var response = server.initialize(params);
                        respond(send, r.id, response);
                        break;
                    }
                    case "initialized": {
                        server.initialized();
                        break;
                    }
                    case "shutdown": {
                        LOG.warning("Got shutdown message");
                        respond(send, r.id, null);
                        break;
                    }
                    case "exit": {
                        LOG.warning("Got exit message, exiting...");
                        break processMessages;
                    }
                    case "workspace/didChangeWorkspaceFolders": {
                        var params = jsonb.fromJson(r.params.toString(), DidChangeWorkspaceFoldersParams.class);
                        server.didChangeWorkspaceFolders(params);
                        break;
                    }
                    case "workspace/didChangeConfiguration": {
                        var params = jsonb.fromJson(r.params.toString(), DidChangeConfigurationParams.class);
                        server.didChangeConfiguration(params);
                        break;
                    }
                    case "workspace/didChangeWatchedFiles": {
                        var params = jsonb.fromJson(r.params.toString(), DidChangeWatchedFilesParams.class);
                        server.didChangeWatchedFiles(params);
                        break;
                    }
                    case "workspace/symbol": {
                        var params = jsonb.fromJson(r.params.toString(), WorkspaceSymbolParams.class);
                        var response = server.workspaceSymbols(params);
                        respond(send, r.id, response);
                        break;
                    }
                    case "textDocument/documentLink": {
                        var params = jsonb.fromJson(r.params.toString(), DocumentLinkParams.class);
                        var response = server.documentLink(params);
                        respond(send, r.id, response);
                        break;
                    }
                    case "textDocument/didOpen": {
                        var params = jsonb.fromJson(r.params.toString(), DidOpenTextDocumentParams.class);
                        server.didOpenTextDocument(params);
                        break;
                    }
                    case "textDocument/didChange": {
                        var params = jsonb.fromJson(r.params.toString(), DidChangeTextDocumentParams.class);
                        server.didChangeTextDocument(params);
                        break;
                    }
                    case "textDocument/willSave": {
                        var params = jsonb.fromJson(r.params.toString(), WillSaveTextDocumentParams.class);
                        server.willSaveTextDocument(params);
                        break;
                    }
                    case "textDocument/willSaveWaitUntil": {
                        var params = jsonb.fromJson(r.params.toString(), WillSaveTextDocumentParams.class);
                        var response = server.willSaveWaitUntilTextDocument(params);
                        respond(send, r.id, response);
                        break;
                    }
                    case "textDocument/didSave": {
                        var params = jsonb.fromJson(r.params.toString(), DidSaveTextDocumentParams.class);
                        server.didSaveTextDocument(params);
                        break;
                    }
                    case "textDocument/didClose": {
                        var params = jsonb.fromJson(r.params.toString(), DidCloseTextDocumentParams.class);
                        server.didCloseTextDocument(params);
                        break;
                    }
                    case "textDocument/completion": {
                        var params = jsonb.fromJson(r.params.toString(), TextDocumentPositionParams.class);
                        var response = server.completion(params);
                        respond(send, r.id, response);
                        break;
                    }
                    case "completionItem/resolve": {
                        var params = jsonb.fromJson(r.params.toString(), CompletionItem.class);
                        var response = server.resolveCompletionItem(params);
                        respond(send, r.id, response);
                        break;
                    }
                    case "textDocument/hover": {
                        var params = jsonb.fromJson(r.params.toString(), TextDocumentPositionParams.class);
                        var response = server.hover(params);
                        respond(send, r.id, response);
                        break;
                    }
                    case "textDocument/signatureHelp": {
                        var params = jsonb.fromJson(r.params.toString(), TextDocumentPositionParams.class);
                        var response = server.signatureHelp(params);
                        respond(send, r.id, response);
                        break;
                    }
                    case "textDocument/definition": {
                        var params = jsonb.fromJson(r.params.toString(), TextDocumentPositionParams.class);
                        var response = server.gotoDefinition(params);
                        respond(send, r.id, response);
                        break;
                    }
                    case "textDocument/references": {
                        var params = jsonb.fromJson(r.params.toString(), ReferenceParams.class);
                        var response = server.findReferences(params);
                        respond(send, r.id, response);
                        break;
                    }
                    case "textDocument/documentSymbol": {
                        var params = jsonb.fromJson(r.params.toString(), DocumentSymbolParams.class);
                        var response = server.documentSymbol(params);
                        respond(send, r.id, response);
                        break;
                    }
                    case "textDocument/codeAction": {
                        var params = jsonb.fromJson(r.params.toString(), CodeActionParams.class);
                        var response = server.codeAction(params);
                        respond(send, r.id, response);
                        break;
                    }
                    case "textDocument/codeLens": {
                        var params = jsonb.fromJson(r.params.toString(), CodeLensParams.class);
                        var response = server.codeLens(params);
                        respond(send, r.id, response);
                        break;
                    }
                    case "codeLens/resolve": {
                        var params = jsonb.fromJson(r.params.toString(), CodeLens.class);
                        var response = server.resolveCodeLens(params);
                        respond(send, r.id, response);
                        break;
                    }
                    case "textDocument/prepareRename": {
                        var params = jsonb.fromJson(r.params.toString(), TextDocumentPositionParams.class);
                        var response = server.prepareRename(params);
                        respond(send, r.id, response);
                        break;
                    }
                    case "textDocument/rename": {
                        var params = jsonb.fromJson(r.params.toString(), RenameParams.class);
                        var response = server.rename(params);
                        respond(send, r.id, response);
                        break;
                    }
                    case "textDocument/formatting": {
                        var params = jsonb.fromJson(r.params.toString(), DocumentFormattingParams.class);
                        var response = server.formatting(params);
                        respond(send, r.id, response);
                        break;
                    }
                    case "textDocument/foldingRange": {
                        var params = jsonb.fromJson(r.params.toString(), FoldingRangeParams.class);
                        var response = server.foldingRange(params);
                        respond(send, r.id, response);
                        break;
                    }
                    case "$/cancelRequest":
                        // Already handled in peek(message)
                        break;
                    default:
                        LOG.warning(String.format("Don't know what to do with method `%s`", r.method));
                }
                }
            } catch (Exception e) {
                LOG.log(Level.SEVERE, e.getMessage(), e);
                if (r.id != null) {
                    respond(send, r.id, new ResponseError(ErrorCodes.InternalError, e.getMessage(), null));
                }
            }
        }
    }

    private static final Logger LOG = Logger.getLogger("main");
}
