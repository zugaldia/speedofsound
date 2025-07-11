from speedofsound.application import SosApplication


class TestSosApplication:
    """Test SosApplication initialization."""

    def test_application_instantiation(self):
        """Test that SosApplication can be instantiated without errors."""
        app = SosApplication(version="1.0.0")
        assert app is not None
        assert hasattr(app, "_logger")
