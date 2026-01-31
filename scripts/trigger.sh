#!/bin/bash

# Alternative to XDG Desktop Global Shortcuts Portal
# https://flatpak.github.io/xdg-desktop-portal/docs/doc-org.freedesktop.portal.GlobalShortcuts.html
#
# The XDG Desktop Global Shortcuts Portal is a fairly recent addition that is
# still not widely supported across desktop environments. For systems where the
# portal is not available, this script provides a manual way to trigger the
# application via D-Bus, allowing users to connect it with their system's
# custom keyboard shortcuts (or other automation tools).

gdbus call \
    --session \
    --dest io.speedofsound.App \
    --object-path /io/speedofsound/App \
    --method org.gtk.Actions.Activate "trigger" [] {}
