package ru.itis.parser

import ru.itis.analyzer.config.XmlAttributes
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties
import ru.itis.analyzer.utils.IdUtils
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class XmlLayoutParser {

    fun parse(file: File): UiComponent {
        val builder = createDocumentBuilderFactory().newDocumentBuilder()
        val document = builder.parse(file)
        val root = document.documentElement
        root.normalize()

        return parseElement(root, file.absolutePath)
    }

    private fun parseElement(element: Element, filePath: String): UiComponent {
        val attributes = mutableMapOf<String, String>()

        val attrMap = element.attributes
        for (i in 0 until attrMap.length) {
            val node = attrMap.item(i)
            attributes[node.nodeName] = node.nodeValue
        }

        val children = mutableListOf<UiComponent>()
        val childNodes = element.childNodes
        for (i in 0 until childNodes.length) {
            val node = childNodes.item(i)
            if (node is Element) {
                children += parseElement(node, filePath)
            }
        }

        return UiComponent(
            id = IdUtils.normalizeId(getAttribute(element, XmlAttributes.ANDROID_ID)),
            type = element.tagName,
            sourceType = SourceType.XML,
            filePath = filePath,
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

    private fun createDocumentBuilderFactory(): DocumentBuilderFactory {
        return DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = true
            isXIncludeAware = false
            isExpandEntityReferences = false

            // Secure XML parsing: disable DTDs and external entities.
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
