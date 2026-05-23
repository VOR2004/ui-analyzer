package ru.itis.xml.source.parser.factory

import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory

internal class SecureXmlDocumentBuilderFactory {

    fun create(): DocumentBuilderFactory {
        return DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = true
            isXIncludeAware = false
            isExpandEntityReferences = false

            setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
            setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "")
            setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "")

            setFeature(XML_FEATURE_DISALLOW_DOCTYPE, true)
            setFeature(XML_FEATURE_EXTERNAL_GENERAL_ENTITIES, false)
            setFeature(XML_FEATURE_EXTERNAL_PARAMETER_ENTITIES, false)
            setFeature(XML_FEATURE_LOAD_EXTERNAL_DTD, false)
        }
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
