import sys

from speedofsound.application import SosApplication


def main() -> int:
    app = SosApplication(version="dev")
    return app.run(sys.argv)


if __name__ == "__main__":
    sys.exit(main())
