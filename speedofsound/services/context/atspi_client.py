from typing import Optional

from gi.repository import Atspi  # type: ignore

from speedofsound.models import ActiveApplication
from speedofsound.services.base_provider import BaseProvider


class AtspiClient(BaseProvider):
    def __init__(self):
        super().__init__(provider_name="atspi")
        self._active_app: Optional[ActiveApplication] = None
        self._desktop = Atspi.get_desktop(0)
        self._logger.info("Initialized.")

    def shutdown(self):
        pass

    @property
    def active_app(self) -> Optional[ActiveApplication]:
        """Get the currently active application."""
        return self._active_app

    def update_active_app(self):
        """Update the active application."""
        self._active_app = self._get_active_app()
        if self._active_app:
            self._logger.info(f"Active app updated: {self._active_app}")

    def _get_active_app(self) -> Optional[ActiveApplication]:
        if not self._desktop:
            return None

        app_count = self._desktop.get_child_count()
        for app_index in range(app_count):
            desktop_child = self._desktop.get_child_at_index(app_index)
            if not desktop_child:
                continue
            if desktop_child.get_role_name() not in ["application"]:
                continue
            window_count = desktop_child.get_child_count()
            for window_index in range(window_count):
                app_child = desktop_child.get_child_at_index(window_index)
                if not app_child:
                    continue
                if app_child.get_role_name() not in ["frame", "window"]:
                    continue
                if not app_child.get_state_set().contains(Atspi.StateType.ACTIVE):
                    continue

                # Found it
                return ActiveApplication(
                    application_name=desktop_child.get_name() or "Unnamed app",
                    window_name=app_child.get_name() or "Unnamed window",
                )

        # If no active application is found, return None
        # This is unlikely because even when the focus is on the desktop,
        # GJS is returned as the application in focus?
        return None
