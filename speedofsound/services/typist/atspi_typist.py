"""

AT-SPI (Assistive Technology Service Provider Interface) is a platform-neutral
framework for providing communication between assistive technologies (AT) and
applications, like SOS. It is AFAICT the de facto standard for providing
accessibility to free and open desktops.

I wanted to make this the default mechanism to enter text into the desktop, because
in theory it should work across all desktop environments. However, in practice
it seems to have some limitations, especially with non-ASCII characters and on
Wayland (old but actual? https://gitlab.gnome.org/GNOME/gnome-shell/-/issues/2958).

Docs:
https://gnome.pages.gitlab.gnome.org/at-spi2-core/libatspi/func.generate_keyboard_event.html

"""

from gi.repository import Atspi  # type: ignore

from speedofsound.models import TypistRequest, TypistResponse
from speedofsound.services.typist.base_typist import BaseTypist

# Character replacement table for problematic characters.
# I was hoping that I could throw at AT-SPI any text, but I'm seeing
# errors when standard Spanish characters are included. We are for
# now handling this with the replacement table below and also issuing
# warnings for non-ASCII characters in case we need to add more.
REPLACEMENT_TABLE = {
    # Accented vowels -> base vowels
    "á": "a",
    "à": "a",
    "ä": "a",
    "â": "a",
    "ã": "a",
    "å": "a",
    "é": "e",
    "è": "e",
    "ë": "e",
    "ê": "e",
    "í": "i",
    "ì": "i",
    "ï": "i",
    "î": "i",
    "ó": "o",
    "ò": "o",
    "ö": "o",
    "ô": "o",
    "õ": "o",
    "ø": "o",
    "ú": "u",
    "ù": "u",
    "ü": "u",
    "û": "u",
    "Á": "A",
    "À": "A",
    "Ä": "A",
    "Â": "A",
    "Ã": "A",
    "Å": "A",
    "É": "E",
    "È": "E",
    "Ë": "E",
    "Ê": "E",
    "Í": "I",
    "Ì": "I",
    "Ï": "I",
    "Î": "I",
    "Ó": "O",
    "Ò": "O",
    "Ö": "O",
    "Ô": "O",
    "Õ": "O",
    "Ø": "O",
    "Ú": "U",
    "Ù": "U",
    "Ü": "U",
    "Û": "U",
    # Spanish ñ
    "ñ": "n",
    "Ñ": "N",
    # Characters to remove
    "¿": "",
    "¡": "",
}


class AtSpiTypist(BaseTypist):
    def __init__(self):
        super().__init__(provider_name="atspi")
        # Needed?
        # result = Atspi.init()
        # if result != 0 and result != 1:  # 0 = success, 1 = already initialized
        #     raise RuntimeError(f"AT-SPI initialization failed with code: {result}")
        self._logger.info("Initialized.")

    def shutdown(self):
        # Needed?
        # self._logger.info("Shutting down.")
        # result = Atspi.exit()
        # if result != 0:
        #     self._logger.error(f"AT-SPI exit failed with code: {result}")
        pass

    def _clean_text(self, text: str) -> str:
        """Clean text by replacing problematic characters."""
        return "".join(REPLACEMENT_TABLE.get(char, char) for char in text)

    def _check_non_ascii(self, text: str) -> None:
        """Log warnings for non-ASCII characters that might need replacement."""
        non_ascii_chars = set()
        for char in text:
            if ord(char) > 127:
                non_ascii_chars.add(char)
        if non_ascii_chars:
            chars_str = "".join(sorted(non_ascii_chars))
            self._logger.warning(f"Found non-ASCII characters: {chars_str}")

    def type(self, request: TypistRequest) -> TypistResponse:
        text = request.transcriber_response.get_text()
        clean = self._clean_text(text)
        self._check_non_ascii(clean)
        if text != clean:
            self._logger.debug(f"Cleaned text: '{text}' -> '{clean}'")
        self._logger.debug(f"Typing text: {clean}")

        try:
            success = Atspi.generate_keyboard_event(0, clean, Atspi.KeySynthType.STRING)
            if not success:
                message = f"Failed to type text: {clean}"
                self._logger.error(message)
                return TypistResponse(success=False, message=message)
            return TypistResponse(success=True)
        except Exception as e:
            message = f"Error during typing: {str(e)}"
            self._logger.error(message)
            return TypistResponse(success=False, message=message)
