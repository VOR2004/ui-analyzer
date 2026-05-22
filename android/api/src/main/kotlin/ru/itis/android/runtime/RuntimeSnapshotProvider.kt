package ru.itis.android.runtime

import ru.itis.model.UiComponent

interface RuntimeSnapshotProvider {
    fun capture(requestedSerial: String? = null): List<UiComponent>
}
