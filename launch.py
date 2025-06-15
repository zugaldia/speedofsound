import sys

from speedofsound.application import SosApplication

app = SosApplication()
exit_code = app.run()
sys.exit(exit_code)
