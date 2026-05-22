package ru.itis.android.runtime.adb

interface AdbCommandRunner {
    fun run(arguments: List<String>): AdbCommandResult
}
