package ru.itis.android.runtime.adb.values

internal object AdbCommands {
    const val DEFAULT_ADB_PATH = "adb"
    const val DEVICES = "devices"
    const val SERIAL_ARGUMENT = "-s"
    const val SHELL = "shell"
    const val WM = "wm"
    const val DENSITY = "density"
    const val SIZE = "size"
    const val UI_AUTOMATOR = "uiautomator"
    const val DUMP = "dump"
    const val PULL = "pull"
    const val UI_AUTOMATOR_REMOTE_DUMP_PATH = "/sdcard/ui-analyzer-window.xml"
    const val LOCAL_DUMP_PREFIX = "ui-analyzer-window"
    const val XML_EXTENSION = ".xml"
}
