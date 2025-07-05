import argparse
import sys

from speedofsound.application import SosApplication
from speedofsound.verification import SystemVerification


def main() -> int:
    parser = argparse.ArgumentParser(
        description="Speed of Sound voice typing.",
        epilog="Run without --verify-* arguments to launch the GUI application.",
    )
    parser.add_argument(
        "--verify-keyboard",
        action="store_true",
        help="Verify virtual keyboard configuration and exit",
    )
    parser.add_argument(
        "--verify-speech",
        action="store_true",
        help="Verify speech recognition configuration and exit",
    )

    # Useful for troubleshooting
    args = parser.parse_args()
    if args.verify_keyboard:
        verifier = SystemVerification()
        return verifier.verify_keyboard()
    if args.verify_speech:
        verifier = SystemVerification()
        return verifier.verify_speech()

    # Launch the app
    app = SosApplication()
    return app.run(sys.argv)


if __name__ == "__main__":
    sys.exit(main())
