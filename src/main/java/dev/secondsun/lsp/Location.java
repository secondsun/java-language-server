package dev.secondsun.lsp;

import java.net.URI;
import java.util.Objects;

public class Location {
    public URI uri;
    public Range range;

    public Location() {}

    public Location(URI uri, Range range) {
        this.uri = uri;
        this.range = range;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Location location = (Location) o;
        return Objects.equals(uri, location.uri) &&
            Objects.equals(range, location.range);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri, range);
    }
}
