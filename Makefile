APP_ID = io.speedofsound.SpeedOfSound
export GRADLE_OPTS = --enable-native-access=ALL-UNNAMED

.PHONY: clean run run-light run-dark build shadow-build shadow-run check \
	meson-clean meson-setup meson-build meson-install uninstall install \
	flatpak-sources flatpak-linter flatpak-build flatpak-bundle flatpak-run desktop-validate \
	snapcraft-clean snapcraft-pack snapcraft-lint snap-install snap-remove \
	docs-serve docs-build

clean:
	./gradlew clean

run: clean
	SOS_DISABLE_GIO_STORE=true SOS_DISABLE_GSTREAMER=false ./gradlew :app:run

run-light: clean
	SOS_DISABLE_GIO_STORE=true SOS_DISABLE_GSTREAMER=false SOS_COLOR_SCHEME=light ./gradlew :app:run

run-dark: clean
	SOS_DISABLE_GIO_STORE=true SOS_DISABLE_GSTREAMER=false SOS_COLOR_SCHEME=dark ./gradlew :app:run

build:
	./gradlew build

shadow-build: clean
	./gradlew :app:shadowJar

shadow-run: shadow-build
	java --enable-native-access=ALL-UNNAMED -jar app/build/libs/speedofsound.jar

check:
	./gradlew check

#
# Meson build
#

meson-clean:
	rm -rf builddir

meson-setup: meson-clean
	meson setup builddir --prefix=$(HOME)/.local

meson-build:
	ninja -C builddir

meson-install:
	ninja -C builddir install

uninstall:
	ninja -C builddir uninstall

install: meson-setup meson-build meson-install

#
# Flatpak
#

flatpak-sources:
	rm -f buildSrc/flatpak-sources.json core/flatpak-sources.json app/flatpak-sources.json
	./gradlew --project-dir buildSrc flatpakGradleGenerator --no-configuration-cache
	./gradlew :app:flatpakGradleGenerator :core:flatpakGradleGenerator --no-configuration-cache

flatpak-linter:
	flatpak run --command=flatpak-builder-lint org.flatpak.Builder appstream data/$(APP_ID).metainfo.xml.in
	flatpak run --command=flatpak-builder-lint org.flatpak.Builder manifest $(APP_ID).yml

flatpak-build:
	rm -f speedofsound.flatpak
	flatpak-builder --force-clean --user --install-deps-from=flathub --repo=repo --install builddir $(APP_ID).yml

flatpak-bundle:
	flatpak build-bundle repo speedofsound.flatpak $(APP_ID) --runtime-repo=https://flathub.org/repo/flathub.flatpakrepo

flatpak-run:
	flatpak run $(APP_ID)

desktop-validate:
	desktop-file-validate data/$(APP_ID).desktop.in

#
# Snap
#

snapcraft-clean:
	snapcraft clean
	rm -f speedofsound_*.snap

snapcraft-pack:
	snapcraft pack

snapcraft-lint:
	snapcraft lint speedofsound_*.snap

snap-install:
	snap install speedofsound_*_amd64.snap --dangerous
	snap connect speedofsound:audio-record
	snap connect speedofsound:alsa

snap-remove:
	snap remove speedofsound

#
# Docs
#

docs-serve:
	mkdocs serve

docs-build:
	mkdocs build
