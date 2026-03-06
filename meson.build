project('speedofsound',
          version: run_command('cat', 'VERSION', check: true).stdout().strip(),
    meson_version: '>= 1.0.0'
)

gnome = import('gnome')

subdir('data')

gnome.post_install(
     glib_compile_schemas: true,
    gtk_update_icon_cache: true,
  update_desktop_database: true,
)

app_name = meson.project_name()

# Use system gradle (Flatpak) or fall back to the project wrapper for local development.
# In Flatpak, pass --offline so Gradle uses only the pre-populated offline repository.
gradle = find_program('gradle', required: false)
gradle_offline_args = gradle.found() ? ['--offline'] : []
if not gradle.found()
  gradle = find_program('gradlew', dirs: [meson.project_source_root()])
endif
gradle_dir = meson.project_source_root()
gradle_install_dir = gradle_dir / 'app' / 'build' / 'install' / app_name + '-shadow'

custom_target('gradle',
    build_by_default: true,
  build_always_stale: true,
             install: true,
         install_dir: [ get_option('bindir'), get_option('prefix') / 'lib' ],
              output: [ app_name, 'speedofsound.jar' ],
             command: [ gradle, '-p', gradle_dir ] + gradle_offline_args +
                      [ '-PreleaseVersion=' + meson.project_version(), 'installShadowDist',
                        '&&', 'mv', gradle_install_dir / 'bin' / app_name, '@OUTPUT0@',
                        '&&', 'mv', gradle_install_dir / 'lib' / 'speedofsound.jar', '@OUTPUT1@'
                      ]
)
