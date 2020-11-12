open module dev.secondsun.lsp.test {
    requires jdk.compiler;
    requires jdk.zipfs;
    requires java.logging;
    requires java.xml;
    requires jakarta.json.bind;
    requires transitive jakarta.json;
    requires org.eclipse.yasson;

    uses javax.tools.JavaCompiler;
    exports dev.secondsun.lsp.test;

    requires org.junit.jupiter.api;
    requires dev.secondsun.lsp;
    requires hamcrest.all; // additional test requirement

}