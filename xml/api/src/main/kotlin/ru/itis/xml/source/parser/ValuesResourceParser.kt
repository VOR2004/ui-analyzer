package ru.itis.xml.source.parser

import ru.itis.xml.source.resource.ValuesResourceSet
import java.io.File

interface ValuesResourceParser {

    fun parse(file: File): ValuesResourceSet
}
