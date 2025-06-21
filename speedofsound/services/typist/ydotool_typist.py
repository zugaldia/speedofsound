import subprocess

from speedofsound.models import TypistRequest, TypistResponse
from speedofsound.services.typist.base_typist import BaseTypist


class YdotoolTypist(BaseTypist):
    def __init__(self):
        super().__init__(provider_name="ydotool")
        self._logger.info("Initialized.")

    def shutdown(self):
        pass

    def _execute_command(self, args: list[str]) -> bool:
        try:
            self._logger.info(f"Executing command: {' '.join(args)}")
            result = subprocess.run(args, check=True)
            return result.returncode == 0
        except Exception as e:
            self._logger.error(f"Command failed with error: {e}")
            return False

    def type(self, request: TypistRequest) -> TypistResponse:
        text = request.transcriber_response.get_text()
        self._logger.debug(f"Typing text: {text}")
        args = ["ydotool", "type", text]
        success = self._execute_command(args)
        return TypistResponse(success=success)
