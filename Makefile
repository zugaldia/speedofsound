run:
	python3 launch.py

run-light:
	GTK_THEME=Adwaita:light python3 launch.py

run-dark:
	GTK_THEME=Adwaita:dark python3 launch.py

run-local:
	PYTHONPATH=$(HOME)/.local/lib/python3/dist-packages:$$PYTHONPATH speedofsound

lint:
	ruff check speedofsound/

format-check:
	ruff format --check speedofsound/

format-diff:
	ruff format --diff speedofsound/

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
