package ru.itis.android.runtime.adb.values

internal object AdbRuntimeMessages {
    fun deviceNotFound(serial: String): String =
        "ADB device not found: $serial"

    const val NO_ONLINE_DEVICES = "No online ADB devices found"

    fun multipleOnlineDevices(serials: String): String =
        "Multiple online ADB devices found: $serials. Pass device serial after --runtime-adb."

    fun failedToListDevices(details: String): String =
        "Failed to list ADB devices: $details"

    fun commandTimedOut(timeoutSeconds: Long): String =
        "adb command timed out after ${timeoutSeconds}s"

    fun failedToDumpWindow(details: String): String =
        "Failed to dump UIAutomator window: $details"

    fun failedToPullDump(details: String): String =
        "Failed to pull UIAutomator dump: $details"
}