package ru.itis

import java.io.File
import ru.itis.analyzer.Analyzer
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.android.runtime.adb.AdbUiAutomatorSnapshotProvider
import ru.itis.compose.runtime.importer.ComposeRuntimeSnapshotImporter
import ru.itis.compose.rules.ComposeRuleSet
import ru.itis.report.JsonReportGenerator
import ru.itis.compose.source.importer.ComposeProjectImporter
import ru.itis.compose.source.parser.ComposeFunctionParser
import ru.itis.compose.source.psi.ComposePsiLayoutParser
import ru.itis.xml.rules.XmlRuleSet
import ru.itis.xml.source.importer.XmlProjectImporter
import ru.itis.xml.source.parser.XmlLayoutParser
import ru.itis.xml.source.resource.ResourceRepository

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println(AnalyzerStrings.Cli.USAGE)
        return
    }

    val projectPath = args[0]
    val outputPath = args.getOrNull(1) ?: AnalyzerStrings.Cli.DEFAULT_OUTPUT_PATH
    val runtimeMode = args.getOrNull(2)
    val adbSerial = args.getOrNull(3)

    val projectRoot = File(projectPath)
    if (!projectRoot.exists() || !projectRoot.isDirectory) {
        println(AnalyzerStrings.Cli.projectDirectoryDoesNotExist(projectPath))
        return
    }

    val xmlImporter = XmlProjectImporter()
    val xmlParser = XmlLayoutParser()
    val composeImporter = ComposeProjectImporter()
    val composeParser = ComposePsiLayoutParser()
    val composeFunctionParser = ComposeFunctionParser()
    val runtimeSnapshotImporter = ComposeRuntimeSnapshotImporter()
    val adbSnapshotProvider = AdbUiAutomatorSnapshotProvider()
    val resourceRepository = ResourceRepository.load(projectRoot)

    val composeFiles = composeImporter.findComposeKotlinFiles(projectRoot)
    val composeFunctions = composeFiles.flatMap { file ->
        runCatching { composeFunctionParser.parse(file) }
            .onFailure { error ->
                println(AnalyzerStrings.Cli.failedToParse(file.path, error.message))
            }
            .getOrDefault(emptyList())
    }

    val analyzer = Analyzer(
        resourceRepository = resourceRepository,
        composeFunctions = composeFunctions,
        rules = XmlRuleSet.default(resourceRepository) + ComposeRuleSet.default()
    )

    val reportGenerator = JsonReportGenerator()

    val xmlFiles = xmlImporter.findLayoutXmlFiles(projectRoot)
    println(AnalyzerStrings.Cli.foundLayoutXmlFiles(xmlFiles.size))

    val xmlComponents = xmlFiles.mapNotNull { file ->
        runCatching { xmlParser.parse(file) }
            .onFailure { error ->
                println(AnalyzerStrings.Cli.failedToParse(file.path, error.message))
            }
            .getOrNull()
    }

    val composeComponents = composeFiles.flatMap { file ->
        runCatching { composeParser.parse(file) }
            .onFailure { error ->
                println(AnalyzerStrings.Cli.failedToParse(file.path, error.message))
            }
            .getOrDefault(emptyList())
    }

    val runtimeComponents = when {
        runtimeMode == null -> emptyList()
        runtimeMode == RUNTIME_ADB_ARGUMENT -> {
            println(AnalyzerStrings.Cli.capturingRuntimeWithAdb(adbSerial))
            runCatching { adbSnapshotProvider.capture(adbSerial) }
                .onFailure { error -> println(error.message) }
                .getOrDefault(emptyList())
        }
        else -> {
            File(runtimeMode)
                .takeIf { file -> file.exists() && file.isFile }
                ?.let { file ->
                    runCatching { runtimeSnapshotImporter.import(file) }
                        .onFailure { error ->
                            println(AnalyzerStrings.Cli.failedToParse(file.path, error.message))
                        }
                        .getOrDefault(emptyList())
                }
                .orEmpty()
        }
    }

    if (runtimeMode != null) {
        println(AnalyzerStrings.Cli.loadedRuntimeComponents(runtimeComponents.sumOf { countComponents(it) }))
    }

    val components = xmlComponents + composeComponents + runtimeComponents
    val issues = analyzer.analyze(components)
    reportGenerator.writeReport(File(outputPath), components, issues)

    println(AnalyzerStrings.Cli.ANALYSIS_COMPLETE)
    println(AnalyzerStrings.Cli.componentsParsed(components.size))
    println(AnalyzerStrings.Cli.issuesFound(issues.size))
    println(AnalyzerStrings.Cli.reportWrittenTo(outputPath))
}

private fun countComponents(component: ru.itis.model.UiComponent): Int {
    return 1 + component.children.sumOf { child -> countComponents(child) }
}

private const val RUNTIME_ADB_ARGUMENT = "--runtime-adb"
