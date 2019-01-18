package org.javacs.lsp;

import java.util.Objects;

public class SymbolInformation {
    public String name;
    public int kind;
    public boolean deprecated;
    public Location location;
    public String containerName;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SymbolInformation that = (SymbolInformation) o;
        return kind == that.kind &&
            deprecated == that.deprecated &&
            Objects.equals(name, that.name) &&
            Objects.equals(location, that.location) &&
            Objects.equals(containerName, that.containerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, kind, deprecated, location, containerName);
    }
}
