open module javacs {
    requires jdk.compiler;
    requires jdk.zipfs;
    requires java.logging;
    requires java.xml;
    requires java.json.bind;
    requires java.json;

    uses javax.tools.JavaCompiler;

    exports org.javacs.lsp;
}
