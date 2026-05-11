package ru.itis.android.runtime.adb

import java.io.File
import kotlin.io.path.createTempFile
import ru.itis.android.runtime.uiautomator.UiAutomatorDumpParser
import ru.itis.model.UiComponent

class AdbUiAutomatorSnapshotProvider(
    private val commandRunner: AdbCommandRunner = ProcessAdbCommandRunner(),
    private val deviceProvider: AdbDeviceProvider = AdbDeviceProvider(commandRunner),
    private val dumpParser: UiAutomatorDumpParser = UiAutomatorDumpParser()
) {

    fun capture(requestedSerial: String? = null): List<UiComponent> {
        val device = deviceProvider.selectDevice(requestedSerial)
        val localDump = createTempFile(prefix = LOCAL_DUMP_PREFIX, suffix = XML_EXTENSION).toFile()

        dumpWindow(device)
        pullDump(device, localDump)

        return dumpParser.parse(localDump)
    }

    private fun dumpWindow(device: AdbDevice) {
        val result = commandRunner.run(
            listOf(
                SERIAL_ARGUMENT,
                device.serial,
                SHELL_COMMAND,
                UIAUTOMATOR_COMMAND,
                DUMP_COMMAND,
                REMOTE_DUMP_PATH
            )
        )
        if (!result.isSuccess) {
            error("Failed to dump UIAutomator window: ${result.stderr.ifBlank { result.stdout }}")
        }
    }

    private fun pullDump(device: AdbDevice, localDump: File) {
        val result = commandRunner.run(
            listOf(
                SERIAL_ARGUMENT,
                device.serial,
                PULL_COMMAND,
                REMOTE_DUMP_PATH,
                localDump.absolutePath
            )
        )
        if (!result.isSuccess) {
            error("Failed to pull UIAutomator dump: ${result.stderr.ifBlank { result.stdout }}")
        }
    }

    private companion object {
        const val SERIAL_ARGUMENT = "-s"
        const val SHELL_COMMAND = "shell"
        const val UIAUTOMATOR_COMMAND = "uiautomator"
        const val DUMP_COMMAND = "dump"
        const val PULL_COMMAND = "pull"
        const val REMOTE_DUMP_PATH = "/sdcard/ui-analyzer-window.xml"
        const val LOCAL_DUMP_PREFIX = "ui-analyzer-window"
        const val XML_EXTENSION = ".xml"
    }
}
