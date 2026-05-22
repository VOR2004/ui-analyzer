package ru.itis.xml.source.parser

import ru.itis.xml.source.resource.AndroidManifestInfo
import java.io.File

interface AndroidManifestParser {

    fun parse(file: File): AndroidManifestInfo
}
