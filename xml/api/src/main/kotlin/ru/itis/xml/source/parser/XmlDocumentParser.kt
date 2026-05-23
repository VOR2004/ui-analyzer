package ru.itis.xml.source.parser

import org.w3c.dom.Document
import java.io.File

interface XmlDocumentParser {

    fun parse(file: File): Document
}
