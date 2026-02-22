package com.zugaldia.speedofsound.app.screens.main

import com.zugaldia.speedofsound.core.APPLICATION_NAME
import org.gnome.adw.Banner

fun buildBannerWidget(onStart: () -> Unit): Banner =
    Banner("Allow $APPLICATION_NAME to type for you.").apply {
        setButtonLabel("Start")
        onButtonClicked { onStart() }
        revealed = false
    }
