package ru.itis.compose.runtime

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import ru.itis.model.RuntimeAttributes
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties

class RuntimePackageGuardTest {

    @Test
    fun `detects runtime snapshot package mismatch`() {
        val components = listOf(runtimeNode(packageName = "com.google.android.apps.nexuslauncher"))

        val hasMismatch = RuntimePackageGuard.hasPackageMismatch(
            components = components,
            expectedPackageName = "com.uniboard"
        )

        assertTrue(hasMismatch)
    }

    @Test
    fun `allows runtime snapshot with expected package`() {
        val components = listOf(runtimeNode(packageName = "com.uniboard"))

        val hasMismatch = RuntimePackageGuard.hasPackageMismatch(
            components = components,
            expectedPackageName = "com.uniboard"
        )

        assertFalse(hasMismatch)
    }

    @Test
    fun `allows runtime snapshot when expected package is unknown`() {
        val components = listOf(runtimeNode(packageName = "com.google.android.apps.nexuslauncher"))

        val hasMismatch = RuntimePackageGuard.hasPackageMismatch(
            components = components,
            expectedPackageName = null
        )

        assertFalse(hasMismatch)
    }

    @Test
    fun `allows runtime snapshot without package metadata`() {
        val components = listOf(runtimeNode(packageName = null))

        val hasMismatch = RuntimePackageGuard.hasPackageMismatch(
            components = components,
            expectedPackageName = "com.uniboard"
        )

        assertFalse(hasMismatch)
    }

    private fun runtimeNode(packageName: String?): UiComponent {
        return UiComponent(
            id = null,
            type = "View",
            sourceType = SourceType.ANDROID_RUNTIME,
            filePath = "runtime.xml",
            treePath = "/View[0]",
            properties = UiProperties(
                rawAttributes = packageName?.let { value ->
                    mapOf(RuntimeAttributes.PACKAGE to value)
                }.orEmpty()
            )
        )
    }
}
