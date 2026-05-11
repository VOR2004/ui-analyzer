package ru.itis.android.runtime.adb

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AdbDeviceProviderTest {

    @Test
    fun `selects single online device automatically`() {
        val provider = AdbDeviceProvider(
            FakeAdbCommandRunner(
                AdbCommandResult(
                    exitCode = 0,
                    stdout = """
                    List of devices attached
                    emulator-5554	device
                    offline-1	offline
                    """.trimIndent(),
                    stderr = ""
                )
            )
        )

        assertEquals("emulator-5554", provider.selectDevice().serial)
    }

    @Test
    fun `selects requested device by serial`() {
        val provider = AdbDeviceProvider(
            FakeAdbCommandRunner(
                AdbCommandResult(
                    exitCode = 0,
                    stdout = """
                    List of devices attached
                    emulator-5554	device
                    emulator-5556	device
                    """.trimIndent(),
                    stderr = ""
                )
            )
        )

        assertEquals("emulator-5556", provider.selectDevice("emulator-5556").serial)
    }

    @Test
    fun `fails when multiple online devices exist without requested serial`() {
        val provider = AdbDeviceProvider(
            FakeAdbCommandRunner(
                AdbCommandResult(
                    exitCode = 0,
                    stdout = """
                    List of devices attached
                    emulator-5554	device
                    emulator-5556	device
                    """.trimIndent(),
                    stderr = ""
                )
            )
        )

        assertFailsWith<IllegalStateException> {
            provider.selectDevice()
        }
    }

    private class FakeAdbCommandRunner(
        private val result: AdbCommandResult
    ) : AdbCommandRunner {
        override fun run(arguments: List<String>): AdbCommandResult = result
    }
}
