import uuid
from pathlib import Path

from gi.repository import GLib  # type: ignore

from speedofsound.constants import APPLICATION_ID


def get_uuid() -> str:
    return str(uuid.uuid4())


def get_cache_path() -> Path:
    """
    Returns the path to the application's cache directory.
    The directory will be created if it doesn't exist.
    Typically: /home/<user>/.cache/io.speedofsound.App
    """
    cache_path = Path(GLib.get_user_cache_dir()) / APPLICATION_ID
    cache_path.mkdir(parents=True, exist_ok=True)
    return cache_path


def get_config_path() -> Path:
    """
    Returns the path to the application's configuration directory.
    The directory will be created if it doesn't exist.
    Typically: /home/<user>/.config/io.speedofsound.App/
    """

    config_path = Path(GLib.get_user_config_dir()) / APPLICATION_ID
    config_path.mkdir(parents=True, exist_ok=True)
    return config_path


def get_data_path() -> Path:
    """
    Returns the path to the application's data directory.
    The directory will be created if it doesn't exist.
    Typically: /home/<user>/.local/share/io.speedofsound.App/
    """
    data_path = Path(GLib.get_user_data_dir()) / APPLICATION_ID
    data_path.mkdir(parents=True, exist_ok=True)
    return data_path


def is_empty(text):
    """Return True if the string is None, empty, or contains only whitespace."""
    return text is None or not text.strip()
