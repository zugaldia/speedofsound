#!/bin/bash
gdbus call \
    --session \
    --dest io.speedofsound.App \
    --object-path /io/speedofsound/App \
    --method org.gtk.Actions.Activate "trigger" [] {}
