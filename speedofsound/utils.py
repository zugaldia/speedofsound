import os
import uuid
from pathlib import Path

from speedofsound.constants import APPLICATION_ID


def get_uuid() -> str:
    return str(uuid.uuid4())


def get_config_dir() -> Path:
    """
    Returns the path to the application's configuration directory.
    The directory will be created if it doesn't exist.
    For Linux (XDG compliant), this will be:
    ~/.config/io.speedofsound.App/
    """

    xdg_config_home = os.environ.get("XDG_CONFIG_HOME")
    if xdg_config_home:
        base_config_dir = Path(xdg_config_home)
    else:
        base_config_dir = Path.home() / ".config"

    config_dir = base_config_dir / APPLICATION_ID
    config_dir.mkdir(parents=True, exist_ok=True)
    return config_dir


def is_empty(text):
    """Return True if the string is None, empty, or contains only whitespace."""
    return text is None or not text.strip()
