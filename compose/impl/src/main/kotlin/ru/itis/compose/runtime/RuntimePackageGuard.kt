package ru.itis.compose.runtime

import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.model.RuntimeAttributes
import ru.itis.model.SourceType
import ru.itis.model.UiComponent

object RuntimePackageGuard {

    fun hasPackageMismatch(
        components: List<UiComponent>,
        expectedPackageName: String?
    ): Boolean {
        val expectedPackage = expectedPackageName?.takeIf { packageName -> packageName.isNotBlank() }
            ?: return false
        val packages = ComponentUtils.flattenAll(components)
            .filter { component -> component.sourceType in runtimeSourceTypes }
            .mapNotNull { component -> component.properties.rawAttributes[RuntimeAttributes.PACKAGE] }
            .filter { packageName -> packageName.isNotBlank() }
            .toSet()

        return packages.isNotEmpty() && expectedPackage !in packages
    }

    private val runtimeSourceTypes = setOf(SourceType.COMPOSE_RUNTIME, SourceType.ANDROID_RUNTIME)
}
