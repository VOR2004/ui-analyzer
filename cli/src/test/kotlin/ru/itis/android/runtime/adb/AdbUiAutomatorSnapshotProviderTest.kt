package ru.itis.android.runtime.adb

import ru.itis.android.runtime.adb.provider.AdbCommandRunner
import ru.itis.android.runtime.adb.provider.AdbDeviceProvider
import ru.itis.android.runtime.adb.provider.AdbUiAutomatorSnapshotProvider
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.itis.android.runtime.uiautomator.parser.UiAutomatorDumpParser
import ru.itis.model.RuntimeAttributes
import ru.itis.model.SourceType

class AdbUiAutomatorSnapshotProviderTest {

    @Test
    fun `captures uiautomator dump through adb and parses pulled file`() {
        val runner = FakeAdbCommandRunner()
        val provider = AdbUiAutomatorSnapshotProvider(
            commandRunner = runner,
            deviceProvider = AdbDeviceProvider(runner),
            dumpParser = UiAutomatorDumpParser()
        )

        val components = provider.capture()
        val button = components.single().children.single()

        assertEquals(SourceType.ANDROID_RUNTIME, button.sourceType)
        assertEquals("android.widget.Button", button.type)
        assertEquals("Save", button.properties.text)
        assertTrue(button.properties.isClickable)
        assertEquals("440", button.properties.rawAttributes[RuntimeAttributes.DENSITY_DPI])
        assertEquals("2.75", button.properties.rawAttributes[RuntimeAttributes.DENSITY])
        assertEquals("1080", button.properties.rawAttributes[RuntimeAttributes.SCREEN_WIDTH_PX])
        assertEquals("2400", button.properties.rawAttributes[RuntimeAttributes.SCREEN_HEIGHT_PX])
        assertEquals("portrait", button.properties.rawAttributes[RuntimeAttributes.ORIENTATION])
        assertEquals(
            listOf(
                listOf("devices"),
                listOf("-s", "emulator-5554", "shell", "wm", "density"),
                listOf("-s", "emulator-5554", "shell", "wm", "size"),
                listOf("-s", "emulator-5554", "shell", "uiautomator", "dump", "/sdcard/ui-analyzer-window.xml"),
                listOf("-s", "emulator-5554", "pull", "/sdcard/ui-analyzer-window.xml")
            ),
            runner.recordedCommandPrefixes()
        )
    }

    private class FakeAdbCommandRunner : AdbCommandRunner {
        private val commands = mutableListOf<List<String>>()

        override fun run(arguments: List<String>): AdbCommandResult {
            commands += arguments
            return when {
                arguments == listOf("devices") -> AdbCommandResult(
                    exitCode = 0,
                    stdout = """
                    List of devices attached
                    emulator-5554	device
                    """.trimIndent(),
                    stderr = ""
                )
                arguments.contains("dump") -> AdbCommandResult(
                    exitCode = 0,
                    stdout = "UI hierchary dumped to: /sdcard/ui-analyzer-window.xml",
                    stderr = ""
                )
                arguments.takeLast(2) == listOf("wm", "density") -> AdbCommandResult(
                    exitCode = 0,
                    stdout = "Physical density: 440",
                    stderr = ""
                )
                arguments.takeLast(2) == listOf("wm", "size") -> AdbCommandResult(
                    exitCode = 0,
                    stdout = "Physical size: 1080x2400",
                    stderr = ""
                )
                arguments.contains("pull") -> {
                    File(arguments.last()).writeText(UIAUTOMATOR_DUMP)
                    AdbCommandResult(
                        exitCode = 0,
                        stdout = "1 file pulled",
                        stderr = ""
                    )
                }
                else -> error("Unexpected adb command: $arguments")
            }
        }

        fun recordedCommandPrefixes(): List<List<String>> {
            return commands.map { command ->
                if (command.contains("pull")) command.dropLast(1) else command
            }
        }
    }

    private companion object {
        val UIAUTOMATOR_DUMP = """
            <?xml version='1.0' encoding='UTF-8' standalone='yes' ?>
            <hierarchy rotation="0">
              <node class="android.widget.FrameLayout" bounds="[0,0][1080,2400]" clickable="false">
                <node
                    class="android.widget.Button"
                    text="Save"
                    content-desc="Save board"
                    resource-id="com.example:id/save"
                    clickable="true"
                    bounds="[24,48][224,144]" />
              </node>
            </hierarchy>
        """.trimIndent()
    }
}
