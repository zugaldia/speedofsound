# This YAML format is now deprecated (hence ESLINT_USE_FLAT_CONFIG)
# https://eslint.org/docs/latest/use/configure/configuration-files-deprecated

download:
	rm -f eslintrc-gjs.yml eslintrc-shell.yml
	wget https://gitlab.gnome.org/GNOME/gnome-shell-extensions/-/raw/main/lint/eslintrc-gjs.yml
	wget https://gitlab.gnome.org/GNOME/gnome-shell-extensions/-/raw/main/lint/eslintrc-shell.yml

lint-gjs:
	ESLINT_USE_FLAT_CONFIG=false npx eslint \
		--config eslintrc-gjs.yml \
		--parser-options="ecmaVersion:2021" \
		extension.js

lint-shell:
	ESLINT_USE_FLAT_CONFIG=false npx eslint \
		--config eslintrc-shell.yml \
		--parser-options="ecmaVersion:2021" \
		extension.js

lint: lint-gjs lint-shell

#
# Extension
#

test-schema:
	glib-compile-schemas --dry-run schemas/

pack-extension: test-schema
	rm -f extension@speedofsound.io.shell-extension.zip
	gnome-extensions pack .

install-extension:
	gnome-extensions install --force extension@speedofsound.io.shell-extension.zip

enable-extension:
	gnome-extensions enable extension@speedofsound.io

disable-extension:
	gnome-extensions disable extension@speedofsound.io
