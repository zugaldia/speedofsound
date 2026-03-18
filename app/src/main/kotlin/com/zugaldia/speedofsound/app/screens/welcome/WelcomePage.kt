package com.zugaldia.speedofsound.app.screens.welcome

import com.zugaldia.speedofsound.core.APPLICATION_NAME
import com.zugaldia.speedofsound.core.APPLICATION_URL

data class WelcomePage(
    val title: String,
    val description: String,
    val iconResourcePath: String? = null,
    val url: String? = null,
)

val welcomePages = listOf(
    WelcomePage(
        title = "Welcome",
        description = "Type with your voice in any Linux app at the speed of sound. " +
                "This short guide will walk you through the basics.",
        iconResourcePath = "/io.speedofsound.SpeedOfSound.png",
    ),
    WelcomePage(
        title = "Set Up a Shortcut",
        description = "$APPLICATION_NAME uses a keyboard shortcut that you set in Preferences. " +
                "Press your shortcut to start and stop dictating, " +
                "and it will type your words into whatever app is open.",
    ),
    WelcomePage(
        title = "Accept Desktop Permissions",
        description = "$APPLICATION_NAME needs permission to type on your behalf. " +
                "Accept the prompt in the main window to allow this. " +
                "It works on all modern Linux desktops, on both X11 and Wayland.",
    ),
    WelcomePage(
        title = "Before You Go",
        description = "The project website has everything you need to get the most out of $APPLICATION_NAME. " +
                "You will find a getting started guide, answers to common questions, and support resources.",
        url = APPLICATION_URL,
    ),
)
