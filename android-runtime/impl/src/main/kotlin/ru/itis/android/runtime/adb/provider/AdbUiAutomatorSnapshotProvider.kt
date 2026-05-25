package ru.itis.android.runtime.adb.provider

import ru.itis.android.runtime.uiautomator.parser.RuntimeDumpParser
import ru.itis.android.runtime.adb.AdbDevice
import ru.itis.android.runtime.adb.values.AdbCommands
import ru.itis.android.runtime.adb.values.AdbRuntimeMessages
import ru.itis.android.runtime.uiautomator.parser.UiAutomatorDumpParser
import ru.itis.model.RuntimeAttributes
import ru.itis.model.UiComponent
import java.io.File
import kotlin.io.path.createTempFile

class AdbUiAutomatorSnapshotProvider(
    private val commandRunner: AdbCommandRunner = ProcessAdbCommandRunner(),
    private val deviceProvider: AndroidDeviceProvider = AdbDeviceProvider(commandRunner),
    private val dumpParser: RuntimeDumpParser = UiAutomatorDumpParser()
) : RuntimeSnapshotProvider {

    override fun capture(requestedSerial: String?): List<UiComponent> {
        val device = deviceProvider.selectDevice(requestedSerial)
        val localDump = createTempFile(
            prefix = AdbCommands.LOCAL_DUMP_PREFIX,
            suffix = AdbCommands.XML_EXTENSION
        ).toFile()

        val metadata = readRuntimeMetadata(device)
        dumpWindow(device)
        pullDump(device, localDump)

        return dumpParser.parse(localDump)
            .map { root -> root.withRuntimeMetadata(metadata) }
    }

    private fun readRuntimeMetadata(device: AdbDevice): Map<String, String> {
        val densityDpi = readDensityDpi(device)
        val screenSize = readScreenSize(device)
        return buildMap {
            densityDpi?.let { dpi ->
                put(RuntimeAttributes.DENSITY_DPI, dpi.toString())
                put(RuntimeAttributes.DENSITY, (dpi / BASE_DENSITY_DPI).toString())
            }
            screenSize?.let { size ->
                put(RuntimeAttributes.SCREEN_WIDTH_PX, size.widthPx.toString())
                put(RuntimeAttributes.SCREEN_HEIGHT_PX, size.heightPx.toString())
                put(RuntimeAttributes.ORIENTATION, size.orientation)
            }
        }
    }

    private fun readDensityDpi(device: AdbDevice): Int? {
        val result = commandRunner.run(
            listOf(
                AdbCommands.SERIAL_ARGUMENT,
                device.serial,
                AdbCommands.SHELL,
                AdbCommands.WM,
                AdbCommands.DENSITY
            )
        )
        if (!result.isSuccess) return null

        return DENSITY_PATTERN.find(result.stdout)
            ?.groupValues
            ?.get(DENSITY_VALUE_GROUP_INDEX)
            ?.toIntOrNull()
    }

    private fun readScreenSize(device: AdbDevice): RuntimeScreenSize? {
        val result = commandRunner.run(
            listOf(
                AdbCommands.SERIAL_ARGUMENT,
                device.serial,
                AdbCommands.SHELL,
                AdbCommands.WM,
                AdbCommands.SIZE
            )
        )
        if (!result.isSuccess) return null

        val match = SIZE_PATTERN.find(result.stdout) ?: return null
        val width = match.groupValues[SIZE_WIDTH_GROUP_INDEX].toIntOrNull() ?: return null
        val height = match.groupValues[SIZE_HEIGHT_GROUP_INDEX].toIntOrNull() ?: return null
        return RuntimeScreenSize(
            widthPx = width,
            heightPx = height
        )
    }

    private fun UiComponent.withRuntimeMetadata(metadata: Map<String, String>): UiComponent {
        if (metadata.isEmpty()) return this

        return copy(
            properties = properties.copy(
                rawAttributes = properties.rawAttributes + metadata
            ),
            children = children.map { child -> child.withRuntimeMetadata(metadata) }
        )
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

    private data class RuntimeScreenSize(
        val widthPx: Int,
        val heightPx: Int
    ) {
        val orientation: String
            get() = when {
                widthPx > heightPx -> LANDSCAPE_ORIENTATION
                heightPx > widthPx -> PORTRAIT_ORIENTATION
                else -> SQUARE_ORIENTATION
            }
    }

    private companion object {
        const val BASE_DENSITY_DPI = 160f
        const val DENSITY_VALUE_GROUP_INDEX = 1
        const val SIZE_WIDTH_GROUP_INDEX = 1
        const val SIZE_HEIGHT_GROUP_INDEX = 2
        const val LANDSCAPE_ORIENTATION = "landscape"
        const val PORTRAIT_ORIENTATION = "portrait"
        const val SQUARE_ORIENTATION = "square"
        val DENSITY_PATTERN = Regex("""(?:Physical|Override) density:\s*(\d+)""")
        val SIZE_PATTERN = Regex("""(?:Physical|Override) size:\s*(\d+)x(\d+)""")
    }
}
