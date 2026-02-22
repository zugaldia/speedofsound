#!/usr/bin/env bash
export GRADLE_OPTS="--enable-native-access=ALL-UNNAMED"
if [ $# -eq 0 ]; then
    ./gradlew :cli:run --quiet
else
    ./gradlew :cli:run --args="$*" --quiet
fi
