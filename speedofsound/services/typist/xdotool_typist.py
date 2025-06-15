import shlex
import subprocess

from speedofsound.models import TypistRequest, TypistResponse
from speedofsound.services.base_provider import BaseProvider


class XdotoolTypist(BaseProvider):
    def __init__(self):
        super().__init__(provider_name="xdotool")
        self._logger.info("Initialized.")

    def shutdown(self):
        pass

    def _execute_command(self, command) -> bool:
        try:
            self._logger.info(f"Executing command: {command}")
            result = subprocess.run(command, shell=True, check=True)
            return result.returncode == 0
        except Exception as e:
            self._logger.error(f"Command failed with error: {e}")
            return False

    def type(self, request: TypistRequest) -> TypistResponse:
        text = request.transcriber_response.get_text()
        quoted = shlex.quote(text)
        self._logger.debug(f"Typing text: {quoted}")
        command = f"xdotool type -- {quoted}"
        success = self._execute_command(command)
        return TypistResponse(success=success)
