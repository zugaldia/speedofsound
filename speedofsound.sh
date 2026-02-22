#!/bin/bash
# $SNAP is set in snap (e.g., /snap/speedofsound/x1), empty in Flatpak (so defaults to /app)
exec java --enable-native-access=ALL-UNNAMED -jar ${SNAP:-}/app/bin/speedofsound.jar "$@"
