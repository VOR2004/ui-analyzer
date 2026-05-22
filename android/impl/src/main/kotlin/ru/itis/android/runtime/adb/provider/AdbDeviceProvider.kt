package ru.itis.android.runtime.adb.provider

import ru.itis.android.runtime.adb.AdbCommandRunner
import ru.itis.android.runtime.adb.AdbDevice
import ru.itis.android.runtime.adb.values.AdbCommands
import ru.itis.android.runtime.adb.values.AdbDeviceStates
import ru.itis.android.runtime.adb.values.AdbRuntimeMessages

class AdbDeviceProvider(
    private val commandRunner: AdbCommandRunner
) {

    fun selectDevice(requestedSerial: String? = null): AdbDevice {
        val devices = loadDevices()
        if (requestedSerial != null) {
            return devices.firstOrNull { device -> device.serial == requestedSerial }
                ?: error(AdbRuntimeMessages.deviceNotFound(requestedSerial))
        }

        val onlineDevices = devices.filter { device -> device.state == AdbDeviceStates.ONLINE }
        return when (onlineDevices.size) {
            0 -> error(AdbRuntimeMessages.NO_ONLINE_DEVICES)
            1 -> onlineDevices.single()
            else -> error(AdbRuntimeMessages.multipleOnlineDevices(onlineDevices.joinToString { device -> device.serial }))
        }
    }

    private fun loadDevices(): List<AdbDevice> {
        val result = commandRunner.run(listOf(AdbCommands.DEVICES))
        if (!result.isSuccess) {
            error(AdbRuntimeMessages.failedToListDevices(result.stderr.ifBlank { result.stdout }))
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
}