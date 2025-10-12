# This is the source of truth for the version number.
# Currently requires manually updating snap/snapcraft.yaml too.
project(
  'speedofsound',
  version: '0.1.0',
  meson_version: '>= 0.62.0',
  default_options: [
    'warning_level=2',
    'werror=false',
  ],
)

i18n = import('i18n')
gnome = import('gnome')
python = import('python')

# Module check is non-critical (required: false) for Snap builds where Python
# packages are installed in a virtual environment by a previous build step.
py_installation = python.find_installation(
  'python3',
  modules: [
    'cairo', # pycairo imports as cairo
    'faster_whisper',
    'gi', # PyGObject imports as gi
    'openai',
    'pydantic',
    'pygame',
  ],
  required: false,
)

if not py_installation.found()
  # If module check failed, get basic Python installation
  py_installation = python.find_installation('python3')
endif

pkgdatadir = get_option('prefix') / get_option('datadir') / meson.project_name()

subdir('data')
subdir('po')

install_subdir('speedofsound', install_dir: py_installation.get_install_dir())

conf = configuration_data()
conf.set('PYTHON', py_installation.full_path())
conf.set('VERSION', meson.project_version())
conf.set('localedir', get_option('prefix') / get_option('localedir'))
conf.set('pkgdatadir', pkgdatadir)

configure_file(
  input: 'speedofsound.in',
  output: 'speedofsound',
  configuration: conf,
  install: true,
  install_dir: get_option('bindir'),
  install_mode: 'r-xr-xr-x',
)

gnome.post_install(
  glib_compile_schemas: true,
  gtk_update_icon_cache: true,
  update_desktop_database: true,
)