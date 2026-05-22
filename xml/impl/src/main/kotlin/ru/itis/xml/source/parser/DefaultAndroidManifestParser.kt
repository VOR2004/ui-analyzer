package ru.itis.xml.source.parser

import org.w3c.dom.Element
import ru.itis.xml.source.resource.AndroidManifestInfo
import java.io.File

class DefaultAndroidManifestParser(
    private val documentParser: XmlDocumentParser = SecureXmlDocumentParser()
) : AndroidManifestParser {

    override fun parse(file: File): AndroidManifestInfo {
        val document = documentParser.parse(file)
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
