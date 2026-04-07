package com.zugaldia.speedofsound.app.portals

enum class RemoteDesktopStatus {
    NeedToken,
    Ready,
    NotSupported;

    companion object {
        fun fromOrdinal(ordinal: Int): RemoteDesktopStatus =
            entries.getOrElse(ordinal) { NeedToken }
    }
}
