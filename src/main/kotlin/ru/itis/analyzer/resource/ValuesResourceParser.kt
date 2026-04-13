package ru.itis.analyzer.resource

import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import ru.itis.analyzer.config.ResourcePatterns

class ValuesResourceParser {

    fun parse(file: File): ValuesResourceSet {
        val colors = mutableMapOf<String, String>()
        val dimensions = mutableMapOf<String, String>()
        val strings = mutableMapOf<String, String>()
        val styles = mutableMapOf<String, StyleResource>()

        val builder = createDocumentBuilderFactory().newDocumentBuilder()
        val document = builder.parse(file)
        val root = document.documentElement
        root.normalize()

        val childNodes = root.childNodes
        for (i in 0 until childNodes.length) {
            val node = childNodes.item(i)
            if (node !is Element) continue

            when (node.tagName) {
                ResourcePatterns.COLOR_TAG -> parseNamedValue(node)?.let { (name, value) ->
                    colors[name] = value
                }
                ResourcePatterns.DIMEN_TAG -> parseNamedValue(node)?.let { (name, value) ->
                    dimensions[name] = value
                }
                ResourcePatterns.STRING_TAG -> parseNamedValue(node)?.let { (name, value) ->
                    strings[name] = value
                }
                ResourcePatterns.STYLE_TAG -> parseStyle(node)?.let { style ->
                    styles[style.name] = style
                }
            }
        }

        return ValuesResourceSet(
            colors = colors,
            dimensions = dimensions,
            strings = strings,
            styles = styles
        )
    }

    private fun parseNamedValue(element: Element): Pair<String, String>? {
        val name = element.getAttribute(ResourcePatterns.NAME_ATTRIBUTE).trim()
        val value = element.textContent?.trim().orEmpty()

        if (name.isBlank() || value.isBlank()) {
            return null
        }

        return name to value
    }

    private fun parseStyle(element: Element): StyleResource? {
        val name = element.getAttribute(ResourcePatterns.NAME_ATTRIBUTE).trim()
        if (name.isBlank()) {
            return null
        }

        val parent = element
            .getAttribute(ResourcePatterns.PARENT_ATTRIBUTE)
            .trim()
            .ifBlank { null }

        val items = mutableMapOf<String, String>()
        val childNodes = element.childNodes
        for (i in 0 until childNodes.length) {
            val node = childNodes.item(i)
            if (node is Element && node.tagName == ResourcePatterns.ITEM_TAG) {
                parseNamedValue(node)?.let { (itemName, value) ->
                    items[itemName] = value
                }
            }
        }

        return StyleResource(
            name = name,
            parent = parent,
            items = items
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
