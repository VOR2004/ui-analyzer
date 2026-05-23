package ru.itis.android.runtime.adb.provider

import ru.itis.android.runtime.adb.AdbCommandResult

interface AdbCommandRunner {
    fun run(arguments: List<String>): AdbCommandResult
}