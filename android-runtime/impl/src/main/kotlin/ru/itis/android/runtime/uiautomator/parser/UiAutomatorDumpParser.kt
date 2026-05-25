package ru.itis.android.runtime.uiautomator.parser

import org.w3c.dom.Element
import ru.itis.android.runtime.uiautomator.schema.UiAutomatorDumpSchema
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties
import ru.itis.xml.source.parser.SecureXmlDocumentParser
import java.io.File

class UiAutomatorDumpParser private constructor(
    private val boundsParser: UiAutomatorBoundsParser = UiAutomatorBoundsParser(),
    private val documentParser: SecureXmlDocumentParser = SecureXmlDocumentParser()
) : RuntimeDumpParser {

    constructor() : this(UiAutomatorBoundsParser(), SecureXmlDocumentParser())

    override fun parse(file: File): List<UiComponent> {
        val root = documentParser.parse(file).documentElement
        return root.childElements()
            .filter { element -> element.tagName == UiAutomatorDumpSchema.NODE_TAG }
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
        val type = element.attributeOrNull(UiAutomatorDumpSchema.CLASS_ATTRIBUTE)
            ?: UiAutomatorDumpSchema.UNKNOWN_TYPE
        val treePath = "$parentPath/$type[$siblingIndex]"
        val rawAttributes = element.attributesMap()
        val bounds = boundsParser.parse(rawAttributes[UiAutomatorDumpSchema.BOUNDS_ATTRIBUTE])

        return UiComponent(
            id = element.attributeOrNull(UiAutomatorDumpSchema.RESOURCE_ID_ATTRIBUTE),
            type = type,
            sourceType = SourceType.ANDROID_RUNTIME,
            filePath = filePath,
            treePath = treePath,
            properties = UiProperties(
                width = bounds?.width?.toPixelString(),
                height = bounds?.height?.toPixelString(),
                text = element.attributeOrNull(UiAutomatorDumpSchema.TEXT_ATTRIBUTE),
                contentDescription = element.attributeOrNull(UiAutomatorDumpSchema.CONTENT_DESCRIPTION_ATTRIBUTE),
                isClickable = element.booleanAttribute(UiAutomatorDumpSchema.CLICKABLE_ATTRIBUTE),
                bounds = bounds,
                rawAttributes = rawAttributes
            ),
            children = element.childElements()
                .filter { child -> child.tagName == UiAutomatorDumpSchema.NODE_TAG }
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
        return getAttribute(name).equals(UiAutomatorDumpSchema.TRUE_VALUE, ignoreCase = true)
    }

    private fun Float.toPixelString(): String {
        return if (this % 1f == 0f) {
            "${toInt()}px"
        } else {
            "${this}px"
        }
    }
}
