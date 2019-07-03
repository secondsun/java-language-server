package org.javacs.lsp;

import org.junit.Before;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.bind.JsonbBuilder;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class LspTest {
    PipedInputStream buffer = new PipedInputStream(10 * 1024 * 1024); // 10 MB buffer
    PipedOutputStream writer = new PipedOutputStream();



    public static JsonValue readObject(Object params) {
        JsonReader jsonReader =
                Json.createReader(new StringReader(JsonbBuilder.create().toJson(params)));
        return jsonReader.readObject();
    }

    @Before
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
        assertThat(bufferToString(), equalTo(expected));
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
        assertThat(parse.params, equalTo(JsonObject.EMPTY_JSON_OBJECT));
    }

    @Test
    public void excludeDefaults() {
        var item = new CompletionItem();
        var text = LSP.toJson(item);

        assertThat(text, equalTo("{\"kind\":0}"));
    }
}
