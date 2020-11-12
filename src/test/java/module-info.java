module dev.secondsun.lsp.test {
    requires dev.secondsun.lsp;
    exports dev.secondsun.lsp.test;

    requires org.junit.jupiter.api;

    requires hamcrest.all;
    requires org.junit.platform.engine; // additional test requirement
    requires org.junit.jupiter.engine;
}