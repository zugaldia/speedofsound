# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

### Development Commands
You should run both after making changes to the codebase (also checked by CI):

```bash
make lint         # Run ruff linter on speedofsound/
make format-check # Run ruff format check on speedofsound/
```

## Code Style
- Use type annotation whenever possible
- Do not add comments for obvious behavior
- When comments are recommended, keep them short
- Avoid relative imports, always use absolute imports

## Architecture Overview
- Speed of Sound is a GTK4-based Linux desktop application
- It has service-oriented architecture developed with Python.
- The UI follows the Model-View-ViewModel (MVVM) pattern.

### Core Services Pattern
All services inherit from `BaseService` (base_service.py) which provides:
- GObject-based signal support for event-driven communication
- Lifecycle management (init/shutdown)

### Service Hierarchy
1. **ConfigurationService**: Manages app configuration (input devices, model selection)
2. **OrchestratorService**: Coordinates between UI and speech recognizer

### UI Framework
- GTK4 with Adwaita design system
- PyGObject bindings for Python
- Custom styling via `speedofsound/data/style.css`
- Main window components in `speedofsound/ui/main/`
