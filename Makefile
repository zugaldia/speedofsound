run:
	python3 launch.py

run-light:
	GTK_THEME=Adwaita:light python3 launch.py

run-dark:
	GTK_THEME=Adwaita:dark python3 launch.py

run-local:
	PYTHONPATH=$(HOME)/.local/lib/python3/dist-packages:$$PYTHONPATH speedofsound

test-trigger:
	./scripts/trigger.sh

lint:
	ruff check speedofsound/

format-check:
	ruff format --check speedofsound/

format-diff:
	ruff format --diff speedofsound/

test-schema:
	glib-compile-schemas --dry-run --strict data/

#
# Meson build
#

meson-setup:
	meson setup builddir --prefix=$(HOME)/.local

meson-build:
	ninja -C builddir

meson-install:
	ninja -C builddir install

meson-uninstall:
	ninja -C builddir uninstall

meson-clean:
	rm -rf builddir

rebuild-run: meson-build meson-install run-local

#
# Flatpak / Flathub
#

# Use req2flatpak instead
FLATPAK_PIP_GENERATOR ?= req2flatpak.py

# flatpak-dependencies:
# 	rm -f requirements.json
# 	python3 $(FLATPAK_PIP_GENERATOR) \
# 		--runtime=org.gnome.Sdk/x86_64/48 \
# 		--requirements-file=requirements.txt \
# 		--output=requirements.json

# In a new virtual environment, run:
# python -m pip install -r requirements.txt
# python -m pip freeze -l > requirements-frozen.txt
flatpak-dependencies:
	rm -f requirements-frozen.json
	python3 $(FLATPAK_PIP_GENERATOR) \
		--requirements-file requirements-frozen.txt \
		--outfile requirements-frozen.json \
		--target-platforms 312-x86_64 312-aarch64

flatpak-build:
	flatpak run org.flatpak.Builder \
	--force-clean \
	--sandbox \
	--user \
	--install \
	--install-deps-from=flathub \
	--ccache \
	--mirror-screenshots-url=https://dl.flathub.org/media/ \
	--repo=repo \
	builddir \
	io.speedofsound.App.json

flatpak-run:
	flatpak run io.speedofsound.App

ostree-commit:
	ostree commit \
		--repo=repo \
		--canonical-permissions \
		--branch=screenshots/$(flatpak --default-arch) \
		builddir/files/share/app-info/media

#
# Snap
#

snap-clean:
	snapcraft clean

snap-pack: snap-clean
	snapcraft pack --output=snap/

snap-install:
	snap install snap/speedofsound_0.1_amd64.snap --dangerous
