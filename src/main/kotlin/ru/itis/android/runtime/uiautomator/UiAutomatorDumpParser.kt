package ru.itis.android.runtime.uiautomator

import java.io.File
import org.w3c.dom.Element
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties
import ru.itis.xml.source.parser.SecureXmlDocumentParser

class UiAutomatorDumpParser {

    fun parse(file: File): List<UiComponent> {
        val root = SecureXmlDocumentParser.parse(file).documentElement
        return root.childElements()
            .filter { element -> element.tagName == NODE_TAG }
            .mapIndexed { index, node ->
                parseNode(
                    element = node,
                    filePath = file.absolutePath,
                    parentPath = "",
                    siblingIndex = index
                )
            }
    }

    private fun parseNode(
        element: Element,
        filePath: String,
        parentPath: String,
        siblingIndex: Int
    ): UiComponent {
        val type = element.attributeOrNull(CLASS_ATTRIBUTE) ?: UNKNOWN_TYPE
        val treePath = "$parentPath/$type[$siblingIndex]"
        val rawAttributes = element.attributesMap()
        val bounds = UiAutomatorBoundsParser.parse(rawAttributes[BOUNDS_ATTRIBUTE])

        return UiComponent(
            id = element.attributeOrNull(RESOURCE_ID_ATTRIBUTE),
            type = type,
            sourceType = SourceType.ANDROID_RUNTIME,
            filePath = filePath,
            treePath = treePath,
            properties = UiProperties(
                width = bounds?.width?.toPixelString(),
                height = bounds?.height?.toPixelString(),
                text = element.attributeOrNull(TEXT_ATTRIBUTE),
                contentDescription = element.attributeOrNull(CONTENT_DESCRIPTION_ATTRIBUTE),
                isClickable = element.booleanAttribute(CLICKABLE_ATTRIBUTE),
                bounds = bounds,
                rawAttributes = rawAttributes
            ),
            children = element.childElements()
                .filter { child -> child.tagName == NODE_TAG }
                .mapIndexed { index, child ->
                    parseNode(
                        element = child,
                        filePath = filePath,
                        parentPath = treePath,
                        siblingIndex = index
                    )
                }
        )
    }

    private fun Element.attributesMap(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val attributes = attributes
        for (index in 0 until attributes.length) {
            val attribute = attributes.item(index)
            result[attribute.nodeName] = attribute.nodeValue
        }
        return result
    }

    private fun Element.childElements(): List<Element> {
        val result = mutableListOf<Element>()
        val nodes = childNodes
        for (index in 0 until nodes.length) {
            val node = nodes.item(index)
            if (node is Element) {
                result += node
            }
        }
        return result
    }

    private fun Element.attributeOrNull(name: String): String? {
        return getAttribute(name).takeIf { value -> value.isNotBlank() }
    }

    private fun Element.booleanAttribute(name: String): Boolean {
        return getAttribute(name).equals(TRUE_VALUE, ignoreCase = true)
    }

    private fun Float.toPixelString(): String {
        return if (this % 1f == 0f) {
            "${toInt()}px"
        } else {
            "${this}px"
        }
    }

    private companion object {
        const val NODE_TAG = "node"
        const val UNKNOWN_TYPE = "UnknownRuntimeNode"
        const val CLASS_ATTRIBUTE = "class"
        const val TEXT_ATTRIBUTE = "text"
        const val CONTENT_DESCRIPTION_ATTRIBUTE = "content-desc"
        const val RESOURCE_ID_ATTRIBUTE = "resource-id"
        const val CLICKABLE_ATTRIBUTE = "clickable"
        const val BOUNDS_ATTRIBUTE = "bounds"
        const val TRUE_VALUE = "true"
    }
}
