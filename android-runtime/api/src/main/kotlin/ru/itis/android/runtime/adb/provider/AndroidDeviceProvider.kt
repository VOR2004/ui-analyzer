package ru.itis.android.runtime.adb.provider

import ru.itis.android.runtime.adb.AdbDevice

interface AndroidDeviceProvider {
    fun selectDevice(requestedSerial: String? = null): AdbDevice
}