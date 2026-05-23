package ru.itis.xml.source.parser

import org.w3c.dom.Element
import ru.itis.analyzer.config.components.ResourcePatterns
import ru.itis.xml.source.resource.StyleResource
import ru.itis.xml.source.resource.ValuesResourceSet
import java.io.File

class DefaultValuesResourceParser(
    private val documentParser: XmlDocumentParser = SecureXmlDocumentParser()
) : ValuesResourceParser {

    override fun parse(file: File): ValuesResourceSet {
        val colors = mutableMapOf<String, String>()
        val dimensions = mutableMapOf<String, String>()
        val strings = mutableMapOf<String, String>()
        val styles = mutableMapOf<String, StyleResource>()

        val document = documentParser.parse(file)
        val root = document.documentElement

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
}
