package dev.secondsun.lsp;

import java.util.Objects;

public class Position {
    // 0-based
    public int line, character;

    public Position() {}

    public Position(int line, int character) {
        this.line = line;
        this.character = character;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Position position = (Position) o;
        return line == position.line &&
            character == position.character;
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, character);
    }
}
