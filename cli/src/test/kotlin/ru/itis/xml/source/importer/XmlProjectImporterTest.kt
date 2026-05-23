package ru.itis.xml.source.importer

import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals

class XmlProjectImporterTest {

    @Test
    fun `findLayoutXmlFiles includes qualified layout directories and skips build folders`() {
        val root = createTempDirectory("ui-analyzer-importer").toFile()
        val layoutFile = root.resolve("app/src/main/res/layout/main.xml")
        val layoutLandFile = root.resolve("app/src/main/res/layout-land/details.xml")
        val buildLayoutFile = root.resolve("build/generated/res/layout/generated.xml")
        val valuesFile = root.resolve("app/src/main/res/values/colors.xml")

        layoutFile.parentFile.mkdirs()
        layoutLandFile.parentFile.mkdirs()
        buildLayoutFile.parentFile.mkdirs()
        valuesFile.parentFile.mkdirs()

        layoutFile.writeText("<LinearLayout />")
        layoutLandFile.writeText("<LinearLayout />")
        buildLayoutFile.writeText("<LinearLayout />")
        valuesFile.writeText("<resources />")

        val files = XmlProjectImporter().findLayoutXmlFiles(root)

        assertEquals(
            listOf(layoutFile.absolutePath, layoutLandFile.absolutePath).sorted(),
            files.map { it.absolutePath }
        )
    }
}
