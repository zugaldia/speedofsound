package com.zugaldia.speedofsound.app.screens.welcome

import com.zugaldia.speedofsound.core.APPLICATION_NAME
import com.zugaldia.speedofsound.core.APPLICATION_SHORTCUT_TRIGGER
import com.zugaldia.speedofsound.core.APPLICATION_URL

data class WelcomePage(
    val title: String,
    val description: String,
    val imageResourcePath: String? = null,
    val url: String? = null,
)

val welcomePages = listOf(
    WelcomePage(
        title = "Welcome",
        description = "Type with your voice in any Linux app at the speed of sound.\n" +
                "This short guide will walk you through the basics.",
        imageResourcePath = "/welcome/logo.png",
    ),
    WelcomePage(
        title = "Accept Desktop Permissions",
        description = "This app needs permission to type on your behalf. " +
                "Accept the prompt in the main window to allow this. " +
                "It works on all modern Linux desktops, on both X11 and Wayland.",
        imageResourcePath = "/welcome/permissions.png",
    ),
    WelcomePage(
        title = "Set Up a Global Shortcut",
        description = "By default, <tt>$APPLICATION_SHORTCUT_TRIGGER</tt> starts and stops listening, but only when " +
                "the $APPLICATION_NAME window is open and focused.\n\n" +
                "For a better experience, we recommend setting up a global shortcut in <b>Preferences</b>. " +
                "This lets you keep the window minimized or hidden and trigger $APPLICATION_NAME " +
                "from anywhere, typing directly into any app.",
    ),
    WelcomePage(
        title = "Before You Go",
        description = "The project website has everything you need to get the most out of this app. " +
                "You will find a getting started guide, answers to common questions, and support resources.",
        url = APPLICATION_URL,
    ),
)
