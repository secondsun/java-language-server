#!/bin/bash
# Check the version of java pointed to by JAVA_HOME is version >= 26.

set -e

if [[ -z "${JAVA_HOME}" ]]; then
  if command -v java >/dev/null 2>&1; then
    JAVA_HOME="$(dirname "$(dirname "$(readlink -f "$(command -v java)")")")"
    export JAVA_HOME
  else
    echo "JAVA_HOME must be set or 'java' must be on PATH"
    exit 1
  fi
fi

if [ ! -f "$JAVA_HOME/bin/java" ]; then
  echo "JAVA_HOME is set to: $JAVA_HOME"
  echo "JAVA_HOME does not point to an installation of Java"
  exit 1
fi

java_version=$("$JAVA_HOME/bin/java" -version 2>&1 | head -n 1 | sed -E 's/.*version "([0-9]+).*/\1/')
if [ -z "$java_version" ] || [ "$java_version" -lt 26 ]; then
  echo "JAVA_HOME is set to: $JAVA_HOME"
  echo "JAVA_HOME version is: $java_version"
  echo "JAVA_HOME must be set to a JDK version >=26"
  exit 1
fi
