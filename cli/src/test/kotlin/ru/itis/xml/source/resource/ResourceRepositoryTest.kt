package ru.itis.xml.source.resource

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class ResourceRepositoryTest {

    @Test
    fun `loads colors dimensions strings and styles from values resources`() {
        val projectRoot = File("src/test/resources/demo-project")

        val repository = DefaultResourceRepository.load(projectRoot)

        assertEquals("#3366FF", repository.resolveColor("@color/demo_primary"))
        assertEquals("#123456", repository.resolveColor("?attr/manifestOnlyColor"))
        assertEquals("#FFFFFF", repository.resolveColor("?attr/colorOnPrimary"))
        assertEquals("#FFFFFF", repository.resolveColor("?android:attr/textColorPrimary"))
        assertEquals("48dp", repository.resolveDimension("@dimen/demo_button_height"))
        assertEquals("48dp", repository.resolveDimension("?attr/buttonHeight"))
        assertEquals("16sp", repository.resolveDimension("?attr/bodyTextSize"))
        assertEquals("Continue", repository.resolveString("@string/demo_action_continue"))
        assertEquals("@style/Widget.Demo.Button", repository.resolveStyleItem("Theme.Demo", "buttonStyle"))
        assertEquals("@dimen/demo_button_height", repository.resolveStyleItem("Widget.Demo.Button", "android:minHeight"))
    }
}
