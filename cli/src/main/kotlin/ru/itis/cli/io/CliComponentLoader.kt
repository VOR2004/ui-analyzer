package ru.itis.cli.io

import java.io.File
import ru.itis.analyzer.messages.cli.CliMessages
import ru.itis.android.runtime.adb.provider.AdbUiAutomatorSnapshotProvider
import ru.itis.cli.config.CliArguments.RUNTIME_ADB_ARGUMENT
import ru.itis.cli.config.CliOptions
import ru.itis.compose.runtime.importer.ComposeRuntimeSnapshotImporter
import ru.itis.compose.source.importer.ComposeProjectImporter
import ru.itis.compose.source.parser.ComposeFunctionParser
import ru.itis.compose.source.psi.ComposePsiLayoutParser
import ru.itis.model.UiComponent
import ru.itis.xml.source.importer.XmlProjectImporter
import ru.itis.xml.source.parser.XmlLayoutParser

class CliComponentLoader(
    private val xmlImporter: XmlProjectImporter = XmlProjectImporter(),
    private val xmlParser: XmlLayoutParser = XmlLayoutParser(),
    private val composeImporter: ComposeProjectImporter = ComposeProjectImporter(),
    private val composeParser: ComposePsiLayoutParser = ComposePsiLayoutParser(),
    private val composeFunctionParser: ComposeFunctionParser = ComposeFunctionParser(),
    private val runtimeSnapshotImporter: ComposeRuntimeSnapshotImporter = ComposeRuntimeSnapshotImporter(),
    private val adbSnapshotProvider: AdbUiAutomatorSnapshotProvider = AdbUiAutomatorSnapshotProvider()
) {

    fun load(projectRoot: File, options: CliOptions): CliAnalysisInput {
        val composeFiles = if (options.ruleMode.includesCompose) {
            composeImporter.findComposeKotlinFiles(projectRoot)
        } else {
            emptyList()
        }
        val composeFunctions = if (options.ruleMode.includesCompose) {
            composeFiles.flatMap { file ->
                runCatching { composeFunctionParser.parse(file) }
                    .onFailure { error -> println(CliMessages.failedToParse(file.path, error.message)) }
                    .getOrDefault(emptyList())
            }
        } else {
            emptyList()
        }

        val xmlFiles = if (options.ruleMode.includesXml) {
            xmlImporter.findLayoutXmlFiles(projectRoot)
        } else {
            emptyList()
        }
        println(CliMessages.foundLayoutXmlFiles(xmlFiles.size))

        val xmlComponents = xmlFiles.mapNotNull { file ->
            runCatching { xmlParser.parse(file) }
                .onFailure { error -> println(CliMessages.failedToParse(file.path, error.message)) }
                .getOrNull()
        }
        val composeComponents = composeFiles.flatMap { file ->
            runCatching { composeParser.parse(file) }
                .onFailure { error -> println(CliMessages.failedToParse(file.path, error.message)) }
                .getOrDefault(emptyList())
        }
        val runtimeComponents = loadRuntimeComponents(options)

        if (options.runtimeMode != null) {
            println(CliMessages.loadedRuntimeComponents(runtimeComponents.sumOf { component -> component.countTree() }))
        }

        return CliAnalysisInput(
            xmlComponents = xmlComponents,
            composeComponents = composeComponents,
            runtimeComponents = runtimeComponents,
            composeFunctions = composeFunctions
        )
    }

    private fun loadRuntimeComponents(options: CliOptions): List<UiComponent> {
        return when {
            !options.ruleMode.includesRuntime -> emptyList()
            options.runtimeMode == null -> emptyList()
            options.runtimeMode == RUNTIME_ADB_ARGUMENT -> {
                println(CliMessages.capturingRuntimeWithAdb(options.adbSerial))
                runCatching { adbSnapshotProvider.capture(options.adbSerial) }
                    .onFailure { error -> println(error.message) }
                    .getOrDefault(emptyList())
            }
            else -> {
                File(options.runtimeMode)
                    .takeIf { file -> file.exists() && file.isFile }
                    ?.let { file ->
                        runCatching { runtimeSnapshotImporter.import(file) }
                            .onFailure { error -> println(CliMessages.failedToParse(file.path, error.message)) }
                            .getOrDefault(emptyList())
                    }
                    .orEmpty()
            }
        }
    }

    private fun UiComponent.countTree(): Int {
        return 1 + children.sumOf { child -> child.countTree() }
    }
}
