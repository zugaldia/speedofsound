import Gio from 'gi://Gio';
import GLib from 'gi://GLib';
import Meta from 'gi://Meta';
import Shell from 'gi://Shell';
import St from 'gi://St';

import * as Main from 'resource:///org/gnome/shell/ui/main.js';
import * as PanelMenu from 'resource:///org/gnome/shell/ui/panelMenu.js';
import * as PopupMenu from 'resource:///org/gnome/shell/ui/popupMenu.js';
import Extension from 'resource:///org/gnome/shell/extensions/extension.js';

const KEYBINDING_NAME = 'trigger-sos';
const DEFAULT_COLOR = 'white';
const ICON_NAME = 'sound-wave-symbolic';
const SETTING_APP_STATUS = 'app-status';
const SETTING_APP_ERROR = 'app-error';

const ALLOWED_COLORS = [
    'red', 'blue', 'green', 'yellow', 'orange',
    'purple', 'pink', 'black', 'white', 'gray'
];

export default class SosExtension extends Extension {
    enable() {
        console.log(`Enabling ${this.metadata.name}`);

        // https://gitlab.gnome.org/GNOME/gnome-shell/-/blob/main/js/ui/panelMenu.js
        const menuAlignment = 0.0;
        const nameText = this.metadata.name;
        const dontCreateMenu = false;
        this._indicator = new PanelMenu.Button(menuAlignment, nameText, dontCreateMenu);

        this._icon = new St.Icon({
            icon_name: ICON_NAME,
            style_class: 'system-status-icon',
        });

        this._updateAppStatus(DEFAULT_COLOR);
        this._indicator.add_child(this._icon);

        try {
            this._createMenuItems();
        } catch (error) {
            console.log(`Failed to create menu items: ${error.message}`);
        }

        try {
            this._setupSettings();
        } catch (error) {
            console.log(`Failed to setup settings: ${error.message}`);
        }

        try {
            this._createKeybinding();
        } catch (error) {
            console.log(`Failed to create keybinding: ${error.message}`);
        }

        Main.panel.addToStatusArea(this.uuid, this._indicator);
    }

    _createMenuItems() {
        let triggerItem = new PopupMenu.PopupMenuItem('Trigger');
        triggerItem.connect('activate', () => this._trigger());
        this._indicator.menu.addMenuItem(triggerItem);

        this._indicator.menu.addMenuItem(new PopupMenu.PopupSeparatorMenuItem());

        let showItem = new PopupMenu.PopupMenuItem('Show app');
        showItem.connect('activate', () => this._showApp());
        this._indicator.menu.addMenuItem(showItem);

        let quitItem = new PopupMenu.PopupMenuItem('Quit app');
        quitItem.connect('activate', () => this._quitApp());
        this._indicator.menu.addMenuItem(quitItem);
    }

    _setupSettings() {
        this._settings = this.getSettings();
        this._settings.list_keys().forEach((key) => {
            console.log(`Available setting: ${key}`);
        });

        this._settingsChangedId = this._settings.connect('changed', (settings, key) => {
            if (key === SETTING_APP_STATUS) {
                const color = settings.get_string(key);
                console.log(`App status changed to: ${color}`);
                this._updateAppStatus(color);
            } else if (key === SETTING_APP_ERROR) {
                const error = settings.get_string(key);
                console.log(`App error changed to: ${error}`);
                this._handleAppError(error);
            } else {
                console.log(`Unhandled setting change: ${key}`);
            }
        });
    }

    _createKeybinding() {
        Main.wm.addKeybinding(
            KEYBINDING_NAME, // name
            this._settings, // settings
            Meta.KeyBindingFlags.IGNORE_AUTOREPEAT, // flags
            // Controls in which GNOME Shell states an action should be handled.
            // https://gnome.pages.gitlab.gnome.org/gnome-shell/shell/flags.ActionMode.html
            Shell.ActionMode.NORMAL | Shell.ActionMode.OVERVIEW, // modes
            () => this._trigger() // handler
        );
    }

    _updateAppStatus(color) {
        if (color !== '' && ALLOWED_COLORS.includes(color)) {
            this._icon.set_style(`color: ${color};`);
        } else {
            this._icon.set_style(`color: ${DEFAULT_COLOR};`);
        }
    }

    _handleAppError(error) {
        if (error && error.trim() !== '') {
            console.log(`Handling app error: ${error}`);
            Main.notify('Oops! Speed of Sound Error', error);
        }
    }

    _showApp() {
        Main.notify('TODO: Show app');
    }

    _quitApp() {
        Main.notify('TODO: Quit app');
    }

    _trigger() {
        try {
            Main.notify('TODO: App triggered');

            // Equivalent to scripts/trigger.sh
            Gio.DBus.session.call(
                'io.speedofsound.App',
                '/io/speedofsound/App',
                'org.gtk.Actions',
                'Activate',
                new GLib.Variant('(sava{sv})', ['trigger', [], {}]),
                null, Gio.DBusCallFlags.NONE, -1, null
            );
        } catch (error) {
            console.log(`Failed to trigger app: ${error.message}`);
        }
    }

    disable() {
        console.log(`Disabling ${this.metadata.name}`);

        Main.wm.removeKeybinding(KEYBINDING_NAME);
        if (this._settings && this._settingsChangedId) {
            this._settings.disconnect(this._settingsChangedId);
        }

        this._settings = null;
        this._settingsChangedId = null;

        this._indicator.destroy();
        this._indicator = null;
    }
}
