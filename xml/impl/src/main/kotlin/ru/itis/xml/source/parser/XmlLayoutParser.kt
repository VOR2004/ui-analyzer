package ru.itis.xml.source.parser

import ru.itis.analyzer.config.components.XmlAttributes
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties
import ru.itis.analyzer.utils.IdUtils
import org.w3c.dom.Element
import java.io.File

class XmlLayoutParser(
    private val documentParser: SecureXmlDocumentParser = SecureXmlDocumentParser()
) {

    fun parse(file: File): UiComponent {
        val document = documentParser.parse(file)
        val root = document.documentElement

        return parseElement(
            element = root,
            filePath = file.absolutePath,
            treePath = buildTreePath(parentPath = "", tagName = root.tagName, index = 0)
        )
    }

    private fun parseElement(
        element: Element,
        filePath: String,
        treePath: String
    ): UiComponent {
        val attributes = mutableMapOf<String, String>()

        val attrMap = element.attributes
        for (i in 0 until attrMap.length) {
            val node = attrMap.item(i)
            attributes[node.nodeName] = node.nodeValue
        }

        val children = elementChildElements(element)
            .mapIndexed { index, childElement ->
                parseElement(
                    element = childElement,
                    filePath = filePath,
                    treePath = buildTreePath(
                        parentPath = treePath,
                        tagName = childElement.tagName,
                        index = index
                    )
                )
            }

        return UiComponent(
            id = IdUtils.normalizeId(getAttribute(element, XmlAttributes.ANDROID_ID)),
            type = element.tagName,
            sourceType = SourceType.XML,
            filePath = filePath,
            treePath = treePath,
            properties = UiProperties(
                width = getAttribute(element, XmlAttributes.ANDROID_LAYOUT_WIDTH),
                height = getAttribute(element, XmlAttributes.ANDROID_LAYOUT_HEIGHT),
                padding = firstNotNull(
                    getAttribute(element, XmlAttributes.ANDROID_PADDING),
                    getAttribute(element, XmlAttributes.ANDROID_PADDING_START),
                    getAttribute(element, XmlAttributes.ANDROID_PADDING_END),
                    getAttribute(element, XmlAttributes.ANDROID_PADDING_LEFT),
                    getAttribute(element, XmlAttributes.ANDROID_PADDING_RIGHT),
                    getAttribute(element, XmlAttributes.ANDROID_PADDING_TOP),
                    getAttribute(element, XmlAttributes.ANDROID_PADDING_BOTTOM),
                    getAttribute(element, XmlAttributes.ANDROID_PADDING_HORIZONTAL),
                    getAttribute(element, XmlAttributes.ANDROID_PADDING_VERTICAL)
                ),
                margin = firstNotNull(
                    getAttribute(element, XmlAttributes.ANDROID_LAYOUT_MARGIN),
                    getAttribute(element, XmlAttributes.ANDROID_LAYOUT_MARGIN_START),
                    getAttribute(element, XmlAttributes.ANDROID_LAYOUT_MARGIN_END),
                    getAttribute(element, XmlAttributes.ANDROID_LAYOUT_MARGIN_LEFT),
                    getAttribute(element, XmlAttributes.ANDROID_LAYOUT_MARGIN_RIGHT),
                    getAttribute(element, XmlAttributes.ANDROID_LAYOUT_MARGIN_TOP),
                    getAttribute(element, XmlAttributes.ANDROID_LAYOUT_MARGIN_BOTTOM),
                    getAttribute(element, XmlAttributes.ANDROID_LAYOUT_MARGIN_HORIZONTAL),
                    getAttribute(element, XmlAttributes.ANDROID_LAYOUT_MARGIN_VERTICAL)
                ),
                backgroundColor = firstNotNull(
                    getAttribute(element, XmlAttributes.ANDROID_BACKGROUND),
                    getAttribute(element, XmlAttributes.APP_CARD_BACKGROUND_COLOR),
                    getAttribute(element, XmlAttributes.APP_BOX_BACKGROUND_COLOR)
                ),
                backgroundTint = firstNotNull(
                    getAttribute(element, XmlAttributes.ANDROID_BACKGROUND_TINT),
                    getAttribute(element, XmlAttributes.APP_BACKGROUND_TINT)
                ),
                tint = firstNotNull(
                    getAttribute(element, XmlAttributes.ANDROID_TINT),
                    getAttribute(element, XmlAttributes.APP_TINT)
                ),
                textColor = getAttribute(element, XmlAttributes.ANDROID_TEXT_COLOR),
                textSize = getAttribute(element, XmlAttributes.ANDROID_TEXT_SIZE),
                fontFamily = getAttribute(element, XmlAttributes.ANDROID_FONT_FAMILY),
                textStyle = getAttribute(element, XmlAttributes.ANDROID_TEXT_STYLE),
                contentDescription = getAttribute(element, XmlAttributes.ANDROID_CONTENT_DESCRIPTION),
                text = getAttribute(element, XmlAttributes.ANDROID_TEXT),
                rawAttributes = attributes
            ),
            children = children
        )
    }

    private fun elementChildElements(element: Element): List<Element> {
        val result = mutableListOf<Element>()
        val childNodes = element.childNodes
        for (i in 0 until childNodes.length) {
            val node = childNodes.item(i)
            if (node is Element) {
                result += node
            }
        }
        return result
    }

    private fun getAttribute(element: Element, name: String): String? {
        return if (element.hasAttribute(name)) {
            element.getAttribute(name).takeIf { it.isNotBlank() }
        } else {
            null
        }
    }

    private fun firstNotNull(vararg values: String?): String? {
        return values.firstOrNull { !it.isNullOrBlank() }
    }

    private fun buildTreePath(parentPath: String, tagName: String, index: Int): String {
        return "$parentPath/$tagName[$index]"
    }
}
