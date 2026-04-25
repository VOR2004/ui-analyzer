package ru.itis.source.xml.resource

import org.w3c.dom.Element
import java.io.File
import ru.itis.source.xml.parser.SecureXmlDocumentParser

class AndroidManifestParser {

    fun parse(file: File): AndroidManifestInfo {
        val document = SecureXmlDocumentParser.parse(file)
        val root = document.documentElement

        val application = root
            .getElementsByTagName(APPLICATION_TAG)
            .item(0) as? Element

        return AndroidManifestInfo(
            applicationTheme = application
                ?.getAttribute(ANDROID_THEME_ATTRIBUTE)
                ?.trim()
                ?.ifBlank { null }
        )
    }

    private companion object {
        const val APPLICATION_TAG = "application"
        const val ANDROID_THEME_ATTRIBUTE = "android:theme"
    }
}
