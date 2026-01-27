.PHONY: run app-run cli-run build check clean

run:
	./gradlew :app:run

build:
	./gradlew build

check:
	./gradlew check

clean:
	./gradlew clean
