package com.zugaldia.speedofsound.app.portals

import org.gnome.gdk.Gdk

object TextUtils {
    private const val GDK_NO_KEYSYM_FLAG = 0x01000000

    /**
     * Maps accented and special characters to their safe ASCII equivalents.
     * This avoids issues with the remote desktop portal keysym handling.
     * Some characters map to empty strings to filter them out entirely.
     */
    private val SAFE_CHAR_MAP = mapOf(
        // Lowercase vowels with diacritics
        'á' to "a", 'à' to "a", 'â' to "a", 'ä' to "a", 'ã' to "a", 'å' to "a",
        'é' to "e", 'è' to "e", 'ê' to "e", 'ë' to "e",
        'í' to "i", 'ì' to "i", 'î' to "i", 'ï' to "i",
        'ó' to "o", 'ò' to "o", 'ô' to "o", 'ö' to "o", 'õ' to "o",
        'ú' to "u", 'ù' to "u", 'û' to "u", 'ü' to "u",
        'ý' to "y", 'ÿ' to "y",
        // Uppercase vowels with diacritics
        'Á' to "A", 'À' to "A", 'Â' to "A", 'Ä' to "A", 'Ã' to "A", 'Å' to "A",
        'É' to "E", 'È' to "E", 'Ê' to "E", 'Ë' to "E",
        'Í' to "I", 'Ì' to "I", 'Î' to "I", 'Ï' to "I",
        'Ó' to "O", 'Ò' to "O", 'Ô' to "O", 'Ö' to "O", 'Õ' to "O",
        'Ú' to "U", 'Ù' to "U", 'Û' to "U", 'Ü' to "U",
        'Ý' to "Y",
        // Common special consonants
        'ñ' to "n", 'Ñ' to "N",
        'ç' to "c", 'Ç' to "C",
        // Spanish punctuation marks to filter out
        '¿' to "", '¡' to "",
    )

    /**
     * Replaces accented and special characters with their safe ASCII equivalents.
     */
    private fun sanitizeSpecialChars(text: String): String =
        text.map { SAFE_CHAR_MAP.getOrDefault(it, it.toString()) }.joinToString("")

    /**
     * Converts a string of text into a list of GDK key symbols suitable for
     * org.freedesktop.portal.RemoteDesktop.NotifyKeyboardKeysym. See:
     * https://flatpak.github.io/xdg-desktop-portal/docs/doc-org.freedesktop.portal.RemoteDesktop.html#org-freedesktop-portal-remotedesktop-notifykeyboardkeysym
     *
     * It also removes new lines to avoid accidentally triggering text input, typically found in chat applications.
     */
    fun textToKeySym(text: String): Result<List<Int>> = runCatching {
        sanitizeSpecialChars(text) // Replace accented/special characters with ASCII equivalents
            .lines().joinToString(" ") // Convert newlines to spaces
            .filterNot { it.isISOControl() } // Remove control characters (tabs, backspace, escape, etc.)
            .map { Gdk.unicodeToKeyval(it.code) } // Convert each character to its GDK keysym value
            .filter { (it and GDK_NO_KEYSYM_FLAG) == 0 } // Filter out characters with no corresponding keysym
    }
}
