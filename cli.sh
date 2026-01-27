#!/usr/bin/env bash
if [ $# -eq 0 ]; then
    ./gradlew :cli:run --quiet
else
    ./gradlew :cli:run --args="$*" --quiet
fi
