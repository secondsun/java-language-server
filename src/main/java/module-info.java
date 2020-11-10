open module dev.secondsun.lsp {
    requires jdk.compiler;
    requires jdk.zipfs;
    requires java.logging;
    requires java.xml;
    requires jakarta.json.bind;
    requires transitive jakarta.json;

    uses javax.tools.JavaCompiler;

    exports dev.secondsun.lsp;
}
