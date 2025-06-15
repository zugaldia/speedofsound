from gi.repository import Atspi

from speedofsound.models import ActiveApplication
from speedofsound.services.base_provider import BaseProvider


class AtspiClient(BaseProvider):
    def __init__(self):
        super().__init__(provider_name="atspi")
        self._desktop = Atspi.get_desktop(0)
        if not self._desktop:
            self._logger.error("No desktop found.")
            return None

        version = Atspi.get_version()
        version_text = f"{version.major}.{version.minor}.{version.micro}"
        self._logger.info(f"Initialized (Atspi v{version_text}).")

    def get_focused_app(self) -> ActiveApplication:
        try:
            return self._get_focused_app()
        except Exception as e:
            self._logger.error(f"Error getting focused app: {e}")
            return None

    def _get_focused_app(self) -> ActiveApplication:
        if not self._desktop:
            raise RuntimeError("No desktop found.")

        app_count = self._desktop.get_child_count()
        for i in range(app_count):
            desktop_child = self._desktop.get_child_at_index(i)
            is_application = desktop_child.get_role_name() == "application"
            if not is_application:
                continue

            window_count = desktop_child.get_child_count()
            for j in range(window_count):
                app_child = desktop_child.get_child_at_index(j)
                is_window = app_child.get_role_name() in ["frame", "window"]
                if not is_window:
                    continue

                is_active = app_child.get_state_set().contains(Atspi.StateType.ACTIVE)
                if not is_active:
                    continue

                # Found it
                return ActiveApplication(
                    application_name=desktop_child.get_name() or "Unnamed app",
                    window_name=app_child.get_name() or "Unnamed window",
                )

        # If no active application is found, return None
        # This is unlikely because even when the focus is on the desktop,
        # GJS is returned as the application in focus.
        return None
