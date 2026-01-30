APP_ID = io.speedofsound.App
export GRADLE_OPTS = --enable-native-access=ALL-UNNAMED

.PHONY: run app-run cli-run build check clean jar-run

clean:
	./gradlew clean

run:
	./gradlew :app:run

build:
	./gradlew build

shadow-build: clean
	./gradlew :app:shadowJar

shadow-run: shadow-build
	java --enable-native-access=ALL-UNNAMED -jar app/build/libs/speedofsound.jar

check:
	./gradlew check

#
# Flatpak
#

flatpak-linter:
	flatpak run --command=flatpak-builder-lint org.flatpak.Builder appstream $(APP_ID).metainfo.xml

flatpak-build: shadow-build
	flatpak-builder --force-clean --user --install-deps-from=flathub --repo=repo --install builddir $(APP_ID).yml

flatpak-bundle:
	flatpak build-bundle repo speedofsound.flatpak $(APP_ID) --runtime-repo=https://flathub.org/repo/flathub.flatpakrepo

flatpak-run:
	flatpak run $(APP_ID)

#
# Snap
#

snapcraft-clean:
	snapcraft clean

snapcraft-pack: shadow-build
	snapcraft pack

snap-install:
	snap install speedofsound_0.1_amd64.snap --dangerous
	snap connect speedofsound:audio-record
	snap connect speedofsound:alsa

snap-remove:
	snap remove speedofsound
