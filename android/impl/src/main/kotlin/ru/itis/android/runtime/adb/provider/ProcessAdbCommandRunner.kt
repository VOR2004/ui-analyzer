package ru.itis.android.runtime.adb.provider

import ru.itis.android.runtime.adb.AdbCommandResult
import ru.itis.android.runtime.adb.provider.AdbCommandRunner
import ru.itis.android.runtime.adb.values.AdbCommands
import ru.itis.android.runtime.adb.values.AdbRuntimeMessages
import java.util.concurrent.TimeUnit

class ProcessAdbCommandRunner(
    private val adbPath: String = AdbCommands.DEFAULT_ADB_PATH,
    private val timeoutSeconds: Long = DEFAULT_TIMEOUT_SECONDS
) : AdbCommandRunner {

    override fun run(arguments: List<String>): AdbCommandResult {
        val process = ProcessBuilder(listOf(adbPath) + arguments)
            .redirectErrorStream(false)
            .start()

        val finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS)
        if (!finished) {
            process.destroyForcibly()
            return AdbCommandResult(
                exitCode = TIMEOUT_EXIT_CODE,
                stdout = process.inputStream.bufferedReader().readText(),
                stderr = AdbRuntimeMessages.commandTimedOut(timeoutSeconds)
            )
        }

        return AdbCommandResult(
            exitCode = process.exitValue(),
            stdout = process.inputStream.bufferedReader().readText(),
            stderr = process.errorStream.bufferedReader().readText()
        )
    }

    private companion object {
        const val DEFAULT_TIMEOUT_SECONDS = 30L
        const val TIMEOUT_EXIT_CODE = -1
    }
}