package ru.itis.android.runtime.adb

class AdbDeviceProvider(
    private val commandRunner: AdbCommandRunner
) {

    fun selectDevice(requestedSerial: String? = null): AdbDevice {
        val devices = loadDevices()
        if (requestedSerial != null) {
            return devices.firstOrNull { device -> device.serial == requestedSerial }
                ?: error("ADB device not found: $requestedSerial")
        }

        val onlineDevices = devices.filter { device -> device.state == DEVICE_STATE_ONLINE }
        return when (onlineDevices.size) {
            0 -> error("No online ADB devices found")
            1 -> onlineDevices.single()
            else -> error(
                "Multiple online ADB devices found: ${
                    onlineDevices.joinToString { device -> device.serial }
                }. Pass device serial after --runtime-adb."
            )
        }
    }

    private fun loadDevices(): List<AdbDevice> {
        val result = commandRunner.run(listOf(DEVICES_COMMAND))
        if (!result.isSuccess) {
            error("Failed to list ADB devices: ${result.stderr.ifBlank { result.stdout }}")
        }

        return result.stdout
            .lineSequence()
            .drop(1)
            .mapNotNull { line -> parseDeviceLine(line) }
            .toList()
    }

    private fun parseDeviceLine(line: String): AdbDevice? {
        val parts = line.trim().split(Regex("""\s+"""))
        if (parts.size < 2) return null

        return AdbDevice(
            serial = parts[0],
            state = parts[1]
        )
    }

    private companion object {
        const val DEVICES_COMMAND = "devices"
        const val DEVICE_STATE_ONLINE = "device"
    }
}
