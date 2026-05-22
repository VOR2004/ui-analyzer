package ru.itis.android.runtime.adb.provider

import ru.itis.model.UiComponent

interface RuntimeSnapshotProvider {
    fun capture(requestedSerial: String? = null): List<UiComponent>
}