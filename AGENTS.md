# Repository Instructions for AI Agents

## Project Overview
`dev.secondsun:languageserver` is a lightweight, pure-Java implementation of the Language Server Protocol (LSP) data structures and JSON-RPC message pump.

- **Language Level**: Java 26 (`--release 26`).
- **Build System**: Maven (via `./mvnw`).
- **Dependencies**: Google Gson (`com.google.code.gson:gson:2.11.0`).
- **Testing**: JUnit 5 Jupiter (`5.11.4`) + Hamcrest (`3.0`).
- **Module System**: Java JPMS (`open module dev.secondsun.lsp`).

---

## Canonical Commands

### Build & Test
- **Compile and run all tests**:
  ```bash
  ./mvnw clean test
  ```
- **Run a single test class**:
  ```bash
  ./mvnw test -Dtest=LanguageServerTest
  ```
- **Run a single test method**:
  ```bash
  ./mvnw test -Dtest=LanguageServerTest#exitMessageKillsServer
  ```
- **Package JAR, sources, and javadocs**:
  ```bash
  ./mvnw clean package
  ```

---

## Architecture & Conventions

1. **Protocol Models (`dev.secondsun.lsp.*`)**:
   - POJO classes with public fields representing standard LSP JSON structures (e.g., `Position`, `Range`, `Diagnostic`, `CompletionItem`).
   - Must remain serializable and deserializable by Gson without requiring external frameworks.
   - The module descriptor (`src/main/java/module-info.java`) is declared `open module dev.secondsun.lsp` to enable reflection by Gson.

2. **Stream Transport (`LSP.java`)**:
   - `LSP.connect(serverFactory, inputStream, outputStream)` connects editor I/O streams to a `LanguageServer` instance.
   - Handles standard LSP headers (`Content-Length: ...\r\n\r\n`), JSON-RPC message parsing, response writing, and client requests/notifications.
   - Implements cancellation routing for `$/cancelRequest`.

3. **Invariants**:
   - **Zero bloat**: Keep external runtime dependencies strictly limited to Gson unless explicitly instructed.
   - **Module boundary**: Any new exported package must be declared in [src/main/java/module-info.java](file:///home/summers/Projects/java-language-server/src/main/java/module-info.java).
   - **Modern Java 26 idioms**: Use pattern matching (`instanceof`), `var` where clarity permits, and avoid raw types.

---

## Agent Verification Checklist
Before completing any task, ensure:
1. `./mvnw clean test` passes with zero failures and zero errors.
2. Javac compilation produces zero warnings (`-Xlint:unchecked` clean).
3. `module-info.java` matches exported packages and required modules.

