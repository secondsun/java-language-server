# Todo

## Autocomplete
- Autocomplete POJO constructor This(T f, U g) { this.f = f; ... }

## Navigation
- Go-to-subclasses
- Test coverage codelens
- Go-to-implementation for overridden methods
- `Thing#close()` shows 0 references for `try (thing)`

## Bugs 
- Deleting file doesn't clear it from javac
- External delete causes find-references to crash because it's still in FileStore.javaSources()

## Polish
- Error squigglies should be on method name, not entire method
- Underline vars that are not effectively final
- Complete parens