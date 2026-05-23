package ru.itis.android.runtime.uiautomator.parser

import ru.itis.model.UiComponent
import java.io.File

interface RuntimeDumpParser {
    fun parse(file: File): List<UiComponent>
}