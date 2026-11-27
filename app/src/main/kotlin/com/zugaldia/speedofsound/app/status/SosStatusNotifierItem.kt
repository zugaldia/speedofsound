package com.zugaldia.speedofsound.app.status

import com.zugaldia.speedofsound.core.APPLICATION_ID
import com.zugaldia.speedofsound.core.APPLICATION_NAME
import com.zugaldia.speedofsound.core.getRuntimeEnvironment
import com.zugaldia.stargate.sdk.status.StargateMenu
import com.zugaldia.stargate.sdk.status.StargateStatusNotifierItem

class SosStatusNotifierItem(
    menu: StargateMenu,
    onActivate: (token: String?) -> Unit,
    onSecondaryActivate: (token: String?) -> Unit,
    private val monochrome: () -> Boolean = { false },
) : StargateStatusNotifierItem(menu, onActivate, onSecondaryActivate) {
    override fun getId(): String = APPLICATION_ID
    override fun getTitle(): String = APPLICATION_NAME

    // See [statusIconName] for why the name depends on the runtime environment. The remaining option for
    // sandboxes that do not export their icons is IconPixmap (raw pixel data over D-Bus).
    // See: https://github.com/ubuntu/gnome-shell-extension-appindicator/issues/544
    override fun getIconName(): String = statusIconName(monochrome(), getRuntimeEnvironment())

    override fun getToolTipInfo(): Pair<String, String> =
        Pair(APPLICATION_NAME, "Voice typing for the Linux desktop")
}
