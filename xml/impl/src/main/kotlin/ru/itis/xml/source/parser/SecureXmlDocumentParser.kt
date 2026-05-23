package ru.itis.xml.source.parser

import org.w3c.dom.Document
import ru.itis.xml.source.parser.factory.SecureXmlDocumentBuilderFactory
import java.io.File

class SecureXmlDocumentParser private constructor(
    private val documentBuilderFactory: SecureXmlDocumentBuilderFactory = SecureXmlDocumentBuilderFactory()
) : XmlDocumentParser {

    constructor() : this(SecureXmlDocumentBuilderFactory())

    override fun parse(file: File): Document {
        val document = documentBuilderFactory
            .create()
            .newDocumentBuilder()
            .parse(file)

        document.documentElement.normalize()
        return document
    }
}
