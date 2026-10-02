# Java Language Server Protocol (LSP) Library

[![Java CI](https://github.com/secondsun/java-language-server/actions/workflows/ci.yml/badge.svg)](https://github.com/secondsun/java-language-server/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/dev.secondsun/languageserver.svg)](https://search.maven.org/artifact/dev.secondsun/languageserver)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

`dev.secondsun:languageserver` is a lightweight, pure-Java implementation of the [Language Server Protocol (LSP)](https://microsoft.github.io/language-server-protocol/) data structures and JSON-RPC message pump.

## Features

- **Standard LSP Data Types**: Clean Java POJOs for LSP protocol requests, responses, notifications, diagnostics, code actions, code lenses, completion, hover, and symbols.
- **Fast JSON-RPC Transport**: Built-in streaming message pump (`LSP.connect(...)`) over standard input/output streams with Content-Length header framing and cancellation routing.
- **Ultra-lightweight**: Minimal external dependencies (only Google Gson).
- **Modern Java**: Targeted for Java 26 with full Java Platform Module System (JPMS) support (`dev.secondsun.lsp`).

## Prerequisites

- **Java Development Kit (JDK)**: Java 26 or newer.
- **Build Tool**: Apache Maven (or the included `./mvnw` wrapper).

## Quick Start

### Build and Test

```bash
# Run all tests
./mvnw clean test

# Run a specific test
./mvnw test -Dtest=LanguageServerTest

# Package JAR, sources, and Javadoc
./mvnw clean package
```

### Maven Dependency

```xml
<dependency>
    <groupId>dev.secondsun</groupId>
    <artifactId>languageserver</artifactId>
    <version>0.10.0</version>
</dependency>
```

### Module Descriptor (`module-info.java`)

```java
module my.custom.lsp {
    requires dev.secondsun.lsp;
}
```

## Basic Usage

Implement the `LanguageServer` interface and connect it to your process's input/output streams:

```java
import dev.secondsun.lsp.*;
import java.io.InputStream;
import java.io.OutputStream;

public class MyServer extends LanguageServer {
    private final LanguageClient client;

    public MyServer(LanguageClient client) {
        this.client = client;
    }

    @Override
    public InitializeResult initialize(InitializeParams params) {
        var result = new InitializeResult();
        // configure capabilities
        return result;
    }

    public static void main(String[] args) {
        InputStream in = System.in;
        OutputStream out = System.out;
        
        LSP.connect(MyServer::new, in, out);
    }
}
```

## Agentic Development

This repository includes [AGENTS.md](AGENTS.md) as the canonical operational guide for AI agents and human pair programmers. Refer to `AGENTS.md` for build workflows, architectural invariants, and verification steps.

## License

This project is licensed under the [MIT License](LICENSE.md).