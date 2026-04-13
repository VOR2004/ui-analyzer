package ru.itis.analyzer.resource

import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class AndroidManifestParser {

    fun parse(file: File): AndroidManifestInfo {
        val builder = createDocumentBuilderFactory().newDocumentBuilder()
        val document = builder.parse(file)
        val root = document.documentElement
        root.normalize()

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

    private fun createDocumentBuilderFactory(): DocumentBuilderFactory {
        return DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = true
            isXIncludeAware = false
            isExpandEntityReferences = false
            setFeature(XML_FEATURE_DISALLOW_DOCTYPE, true)
            setFeature(XML_FEATURE_EXTERNAL_GENERAL_ENTITIES, false)
            setFeature(XML_FEATURE_EXTERNAL_PARAMETER_ENTITIES, false)
            setFeature(XML_FEATURE_LOAD_EXTERNAL_DTD, false)
        }
    }

    private companion object {
        const val APPLICATION_TAG = "application"
        const val ANDROID_THEME_ATTRIBUTE = "android:theme"
        const val XML_FEATURE_DISALLOW_DOCTYPE =
            "http://apache.org/xml/features/disallow-doctype-decl"
        const val XML_FEATURE_EXTERNAL_GENERAL_ENTITIES =
            "http://xml.org/sax/features/external-general-entities"
        const val XML_FEATURE_EXTERNAL_PARAMETER_ENTITIES =
            "http://xml.org/sax/features/external-parameter-entities"
        const val XML_FEATURE_LOAD_EXTERNAL_DTD =
            "http://apache.org/xml/features/nonvalidating/load-external-dtd"
    }
}
