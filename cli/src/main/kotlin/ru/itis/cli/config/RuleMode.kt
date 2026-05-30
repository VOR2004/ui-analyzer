package ru.itis.cli.config

enum class RuleMode(
    val includesXml: Boolean,
    val includesCompose: Boolean,
    val includesRuntime: Boolean
) {
    ALL(includesXml = true, includesCompose = true, includesRuntime = true),
    STATIC(includesXml = true, includesCompose = true, includesRuntime = false),
    XML(includesXml = true, includesCompose = false, includesRuntime = false),
    COMPOSE(includesXml = false, includesCompose = true, includesRuntime = false),
    RUNTIME(includesXml = false, includesCompose = false, includesRuntime = true);

    companion object {
        fun fromValue(value: String): RuleMode {
            return entries.firstOrNull { mode -> mode.name.equals(value, ignoreCase = true) }
                ?: ALL
        }
    }
}
