package ru.itis.xml.source.parser

import kotlin.io.path.createTempFile
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class XmlLayoutParserTest {

    @Test
    fun `parse normalizes ids and collects layout-specific attributes`() {
        val xmlFile = createTempFile("layout", ".xml").toFile()
        xmlFile.writeText(
            """
            <androidx.cardview.widget.CardView
                xmlns:android="http://schemas.android.com/apk/res/android"
                xmlns:app="http://schemas.android.com/apk/res-auto"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginBottom="12dp"
                app:cardBackgroundColor="@color/surface">
                <TextView
                    android:id="@+id/title"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:paddingStart="16dp"
                    android:text="Hello" />
            </androidx.cardview.widget.CardView>
            """.trimIndent()
        )

        val component = XmlLayoutParser().parse(xmlFile)
        val textChild = component.children.single()

        assertEquals(xmlFile.absolutePath, component.filePath)
        assertEquals("12dp", component.properties.margin)
        assertEquals("@color/surface", component.properties.backgroundColor)
        assertEquals("title", textChild.id)
        assertEquals("16dp", textChild.properties.padding)
        assertEquals("Hello", textChild.properties.text)
        assertTrue("android:paddingStart" in textChild.properties.rawAttributes)
    }
}
