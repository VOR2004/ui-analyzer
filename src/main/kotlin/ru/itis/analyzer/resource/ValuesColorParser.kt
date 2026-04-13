package ru.itis.analyzer.resource

import ru.itis.analyzer.config.ResourcePatterns
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class ValuesColorParser {

    fun parseColors(file: File): Map<String, String> {
        val result = mutableMapOf<String, String>()

        val factory = DocumentBuilderFactory.newInstance()
        factory.isNamespaceAware = true

        val builder = factory.newDocumentBuilder()
        val document = builder.parse(file)
        val root = document.documentElement
        root.normalize()

        val childNodes = root.childNodes
        for (i in 0 until childNodes.length) {
            val node = childNodes.item(i)
            if (node is Element && node.tagName == ResourcePatterns.COLOR_TAG) {
                val name = node.getAttribute(ResourcePatterns.NAME_ATTRIBUTE).trim()
                val value = node.textContent?.trim().orEmpty()

                if (name.isNotBlank() && value.isNotBlank()) {
                    result[name] = value
                }
            }
        }

        return result
    }
}
