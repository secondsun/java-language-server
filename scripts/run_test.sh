#!/bin/bash

set -e

./scripts/check_java_home.sh

./mvnw test -Dtest="$1#$2"