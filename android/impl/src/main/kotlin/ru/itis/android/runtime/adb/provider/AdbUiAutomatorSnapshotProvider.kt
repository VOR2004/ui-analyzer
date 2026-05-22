package ru.itis.android.runtime.adb.provider

import ru.itis.android.runtime.RuntimeSnapshotProvider
import ru.itis.android.runtime.adb.AdbCommandRunner
import ru.itis.android.runtime.adb.AdbDevice
import ru.itis.android.runtime.adb.provider.ProcessAdbCommandRunner
import ru.itis.android.runtime.adb.values.AdbCommands
import ru.itis.android.runtime.adb.values.AdbRuntimeMessages
import ru.itis.android.runtime.uiautomator.parser.UiAutomatorDumpParser
import ru.itis.model.UiComponent
import java.io.File
import kotlin.io.path.createTempFile

class AdbUiAutomatorSnapshotProvider(
    private val commandRunner: AdbCommandRunner = ProcessAdbCommandRunner(),
    private val deviceProvider: AdbDeviceProvider = AdbDeviceProvider(commandRunner),
    private val dumpParser: UiAutomatorDumpParser = UiAutomatorDumpParser()
) : RuntimeSnapshotProvider {

    override fun capture(requestedSerial: String?): List<UiComponent> {
        val device = deviceProvider.selectDevice(requestedSerial)
        val localDump = createTempFile(
            prefix = AdbCommands.LOCAL_DUMP_PREFIX,
            suffix = AdbCommands.XML_EXTENSION
        ).toFile()

        dumpWindow(device)
        pullDump(device, localDump)

        return dumpParser.parse(localDump)
    }

    private fun dumpWindow(device: AdbDevice) {
        val result = commandRunner.run(
            listOf(
                AdbCommands.SERIAL_ARGUMENT,
                device.serial,
                AdbCommands.SHELL,
                AdbCommands.UI_AUTOMATOR,
                AdbCommands.DUMP,
                AdbCommands.UI_AUTOMATOR_REMOTE_DUMP_PATH
            )
        )
        if (!result.isSuccess) {
            error(AdbRuntimeMessages.failedToDumpWindow(result.stderr.ifBlank { result.stdout }))
        }
    }

    private fun pullDump(device: AdbDevice, localDump: File) {
        val result = commandRunner.run(
            listOf(
                AdbCommands.SERIAL_ARGUMENT,
                device.serial,
                AdbCommands.PULL,
                AdbCommands.UI_AUTOMATOR_REMOTE_DUMP_PATH,
                localDump.absolutePath
            )
        )
        if (!result.isSuccess) {
            error(AdbRuntimeMessages.failedToPullDump(result.stderr.ifBlank { result.stdout }))
        }
    }
}