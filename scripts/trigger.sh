#!/bin/bash
gdbus call \
    --session \
    --dest io.speedofsound.app \
    --object-path /io/speedofsound/app \
    --method org.gtk.Actions.Activate "trigger" [] {}
