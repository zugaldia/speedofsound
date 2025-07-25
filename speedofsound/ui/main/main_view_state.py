from gi.repository import GObject  # type: ignore

from speedofsound.models import OrchestratorStage
from speedofsound.ui.base_view_state import BaseViewState


class MainViewState(BaseViewState):
    orchestrator_state = GObject.Property(
        type=OrchestratorStage,
        default=OrchestratorStage.INITIALIZING,
    )

    status_text = GObject.Property(type=str, default="")
    volume_level = GObject.Property(type=float, default=0.0)

    language_name = GObject.Property(type=str, default="")
    model_name = GObject.Property(type=str, default="")
    words_per_minute = GObject.Property(type=float, default=0.0)
