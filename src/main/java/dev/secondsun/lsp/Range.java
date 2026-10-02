package dev.secondsun.lsp;

import java.util.Objects;

public class Range {
    public Position start, end;

    public Range() {}

    public Range(Position start, Position end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Range range = (Range) o;
        return Objects.equals(start, range.start) &&
            Objects.equals(end, range.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return start + "-" + end;
    }

    public static final Range NONE = new Range(Position.NONE, Position.NONE);
}
