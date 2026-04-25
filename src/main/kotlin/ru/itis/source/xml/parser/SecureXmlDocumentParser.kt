package ru.itis.source.xml.parser

import org.w3c.dom.Document
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

object SecureXmlDocumentParser {

    fun parse(file: File): Document {
        val document = createDocumentBuilderFactory()
            .newDocumentBuilder()
            .parse(file)

        document.documentElement.normalize()
        return document
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

    private const val XML_FEATURE_DISALLOW_DOCTYPE =
        "http://apache.org/xml/features/disallow-doctype-decl"
    private const val XML_FEATURE_EXTERNAL_GENERAL_ENTITIES =
        "http://xml.org/sax/features/external-general-entities"
    private const val XML_FEATURE_EXTERNAL_PARAMETER_ENTITIES =
        "http://xml.org/sax/features/external-parameter-entities"
    private const val XML_FEATURE_LOAD_EXTERNAL_DTD =
        "http://apache.org/xml/features/nonvalidating/load-external-dtd"
}
