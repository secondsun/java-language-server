open module dev.secondsun.lsp {
    requires jdk.compiler;
    requires jdk.zipfs;
    requires java.logging;
    requires java.xml;
    requires java.json.bind;
    requires java.json;

    uses javax.tools.JavaCompiler;

    exports dev.secondsun.lsp;
}
