package dev.secondsun.lsp.test;

import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import dev.secondsun.lsp.CompletionItem;
import dev.secondsun.lsp.LSP;
import dev.secondsun.lsp.MarkedString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class LspTest {
    PipedInputStream buffer = new PipedInputStream(10 * 1024 * 1024); // 10 MB buffer
    PipedOutputStream writer = new PipedOutputStream();

    private static final Gson jsonb = LSP.jsonb;

    public static JsonElement readObject(Object params) {
        return jsonb.toJsonTree(params);
    }

    @BeforeEach
    public void connectBuffer() throws IOException {
        writer.connect(buffer);
    }

    String bufferToString() {
        try {
            var available = buffer.available();
            var bytes = new byte[available];
            var read = buffer.read(bytes);
            assert read == available;
            return new String(bytes, Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void writeResponse() {
        LSP.respond(writer, 1, 2);
        var expected = "Content-Length: 35\r\n\r\n{\"jsonrpc\":\"2.0\",\"id\":1,\"result\":2}";
        assertThat(bufferToString(), equalTo(expected));
    }

    @Test
    public void writeMultibyteCharacters() {
        LSP.respond(writer, 1, "🔥");

        var expected = "Content-Length: 40\r\n\r\n{\"jsonrpc\":\"2.0\",\"id\":1,\"result\":\"🔥\"}";
        String bufferTo = bufferToString();
        System.out.println(bufferTo.getBytes().length);
        assertThat(bufferTo, equalTo(expected));
    }

    @Test
    public void writeOptional() {
        LSP.respond(writer, 1, Optional.of(1));
        var expected = "Content-Length: 35\r\n\r\n{\"jsonrpc\":\"2.0\",\"id\":1,\"result\":1}";
        assertThat(bufferToString(), equalTo(expected));
    }

    @Test
    public void writeEmpty() {
        LSP.respond(writer, 1, Optional.empty());
        var expected = "Content-Length: 38\r\n\r\n{\"jsonrpc\":\"2.0\",\"id\":1,\"result\":null}";
        assertThat(bufferToString(), equalTo(expected));
    }

    @Test
    public void readMessage() throws IOException {
        var message = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"initialize\",\"params\":{}}";
        var header = String.format("Content-Length: %d\r\n\r\n", message.getBytes().length);
        writer.write(header.getBytes());
        writer.write(message.getBytes());

        var token = LSP.nextToken(buffer);
        assertThat(token, equalTo(message));

        var parse = LSP.parseMessage(token);
        assertThat(parse.jsonrpc, equalTo("2.0"));
        assertThat(parse.id, equalTo(1));
        assertThat(parse.method, equalTo("initialize"));
        assertThat(parse.params, equalTo(new JsonObject()));
    }

    @Test
    public void excludeDefaults() {
        var item = new CompletionItem();
        var text = LSP.toJson(item);

        assertThat(text, equalTo("{\"kind\":0}"));
    }
}
