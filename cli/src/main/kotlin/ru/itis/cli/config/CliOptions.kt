package ru.itis.cli.config

import ru.itis.analyzer.messages.cli.CliMessages
import ru.itis.cli.config.CliArguments.ARGUMENT_PREFIX
import ru.itis.cli.config.CliArguments.RULES_ARGUMENT_PREFIX
import ru.itis.cli.config.CliArguments.RUNTIME_ADB_ARGUMENT

data class CliOptions(
    val projectPath: String,
    val outputPath: String,
    val ruleMode: RuleMode,
    val runtimeMode: String?,
    val adbSerial: String?
) {
    companion object {
        fun parse(args: Array<String>): CliOptions {
            val projectPath = args[0]
            val remaining = args.drop(1).toMutableList()
            val ruleMode = remaining
                .firstOrNull { value -> value.startsWith(RULES_ARGUMENT_PREFIX) }
                ?.also { value -> remaining.remove(value) }
                ?.substringAfter(RULES_ARGUMENT_PREFIX)
                ?.let(RuleMode::fromValue)
                ?: RuleMode.ALL
            val adbIndex = remaining.indexOf(RUNTIME_ADB_ARGUMENT)
            val runtimeModeFromAdb = if (adbIndex >= 0) {
                remaining.removeAt(adbIndex)
            } else {
                null
            }
            val adbSerial = if (runtimeModeFromAdb != null &&
                adbIndex < remaining.size &&
                !remaining[adbIndex].startsWith(ARGUMENT_PREFIX)
            ) {
                remaining.removeAt(adbIndex)
            } else {
                null
            }
            val positionalArguments = remaining.filterNot { value -> value.startsWith(ARGUMENT_PREFIX) }
            val outputPath = positionalArguments.getOrNull(0) ?: CliMessages.DEFAULT_OUTPUT_PATH
            val runtimeMode = runtimeModeFromAdb ?: positionalArguments.getOrNull(1)

            return CliOptions(
                projectPath = projectPath,
                outputPath = outputPath,
                ruleMode = ruleMode,
                runtimeMode = runtimeMode,
                adbSerial = adbSerial
            )
        }
    }
}
