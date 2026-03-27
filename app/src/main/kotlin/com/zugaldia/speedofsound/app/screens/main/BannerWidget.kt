package com.zugaldia.speedofsound.app.screens.main

import org.gnome.adw.Banner

fun buildBannerWidget(onAllow: () -> Unit): Banner =
    Banner("Permission required to enable typing").apply {
        buttonLabel = "Allow"
        onButtonClicked { onAllow() }
        revealed = false
    }
