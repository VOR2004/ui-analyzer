package ru.itis

import java.io.File
import ru.itis.analyzer.Analyzer
import ru.itis.analyzer.messages.cli.CliMessages
import ru.itis.analyzer.rules.base.Rule
import ru.itis.android.project.AndroidProjectPackageResolver
import ru.itis.android.runtime.adb.provider.AdbUiAutomatorSnapshotProvider
import ru.itis.compose.runtime.importer.ComposeRuntimeSnapshotImporter
import ru.itis.compose.rules.ComposeRuleSet
import ru.itis.model.AnalysisIssue
import ru.itis.model.UiComponent
import ru.itis.report.JsonReportGenerator
import ru.itis.report.ReportGenerator
import ru.itis.compose.source.importer.ComposeProjectImporter
import ru.itis.compose.source.parser.ComposeFunctionParser
import ru.itis.compose.source.psi.ComposePsiLayoutParser
import ru.itis.xml.rules.XmlRuleSet
import ru.itis.xml.source.importer.XmlProjectImporter
import ru.itis.xml.source.parser.XmlLayoutParser
import ru.itis.xml.source.resource.DefaultResourceRepository
import ru.itis.xml.source.resource.ResourceRepository

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println(CliMessages.USAGE)
        return
    }

    val options = CliOptions.parse(args)
    val projectPath = options.projectPath
    val outputPath = options.outputPath

    val projectRoot = File(projectPath)
    if (!projectRoot.exists() || !projectRoot.isDirectory) {
        println(CliMessages.projectDirectoryDoesNotExist(projectPath))
        return
    }

    val xmlImporter = XmlProjectImporter()
    val xmlParser = XmlLayoutParser()
    val composeImporter = ComposeProjectImporter()
    val composeParser = ComposePsiLayoutParser()
    val composeFunctionParser = ComposeFunctionParser()
    val runtimeSnapshotImporter = ComposeRuntimeSnapshotImporter()
    val adbSnapshotProvider = AdbUiAutomatorSnapshotProvider()
    val projectPackageResolver = AndroidProjectPackageResolver()
    val resourceRepository = DefaultResourceRepository.load(projectRoot)
    val expectedPackageName = projectPackageResolver.resolve(projectRoot)

    val composeFiles = if (options.ruleMode.includesCompose) {
        composeImporter.findComposeKotlinFiles(projectRoot)
    } else {
        emptyList()
    }
    val composeFunctions = if (options.ruleMode.includesCompose) {
        composeFiles.flatMap { file ->
            runCatching { composeFunctionParser.parse(file) }
                .onFailure { error ->
                    println(CliMessages.failedToParse(file.path, error.message))
                }
                .getOrDefault(emptyList())
        }
    } else {
        emptyList()
    }

    val reportGenerator: ReportGenerator = JsonReportGenerator()

    val xmlFiles = if (options.ruleMode.includesXml) {
        xmlImporter.findLayoutXmlFiles(projectRoot)
    } else {
        emptyList()
    }
    println(CliMessages.foundLayoutXmlFiles(xmlFiles.size))

    val xmlComponents = xmlFiles.mapNotNull { file ->
        runCatching { xmlParser.parse(file) }
            .onFailure { error ->
                println(CliMessages.failedToParse(file.path, error.message))
            }
            .getOrNull()
    }

    val composeComponents = composeFiles.flatMap { file ->
        runCatching { composeParser.parse(file) }
            .onFailure { error ->
                println(CliMessages.failedToParse(file.path, error.message))
            }
            .getOrDefault(emptyList())
    }

    val runtimeComponents = when {
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
                        .onFailure { error ->
                            println(CliMessages.failedToParse(file.path, error.message))
                        }
                        .getOrDefault(emptyList())
                }
                .orEmpty()
        }
    }

    if (options.runtimeMode != null) {
        println(CliMessages.loadedRuntimeComponents(runtimeComponents.sumOf { countComponents(it) }))
    }

    val components = componentsForReport(
        ruleMode = options.ruleMode,
        xmlComponents = xmlComponents,
        composeComponents = composeComponents,
        runtimeComponents = runtimeComponents
    )
    val issues = analyzeByRuleMode(
        ruleMode = options.ruleMode,
        xmlComponents = xmlComponents,
        composeComponents = composeComponents,
        runtimeComponents = runtimeComponents,
        resourceRepository = resourceRepository,
        composeFunctions = composeFunctions,
        expectedPackageName = expectedPackageName
    )
    reportGenerator.writeReport(File(outputPath), components, issues)

    println(CliMessages.ANALYSIS_COMPLETE)
    println(CliMessages.componentsParsed(components.size))
    println(CliMessages.issuesFound(issues.size))
    println(CliMessages.reportWrittenTo(outputPath))
}

private fun analyzeByRuleMode(
    ruleMode: RuleMode,
    xmlComponents: List<UiComponent>,
    composeComponents: List<UiComponent>,
    runtimeComponents: List<UiComponent>,
    resourceRepository: ResourceRepository,
    composeFunctions: List<ru.itis.compose.source.model.ComposeFunction>,
    expectedPackageName: String?
): List<AnalysisIssue> {
    val staticIssues = when (ruleMode) {
        RuleMode.ALL,
        RuleMode.STATIC -> analyzeStatic(
            components = xmlComponents + composeComponents,
            rules = XmlRuleSet.default(resourceRepository) + ComposeRuleSet.staticRules(),
            resourceRepository = resourceRepository,
            composeFunctions = composeFunctions
        )
        RuleMode.XML -> analyzeStatic(
            components = xmlComponents,
            rules = XmlRuleSet.default(resourceRepository),
            resourceRepository = resourceRepository
        )
        RuleMode.COMPOSE -> analyzeStatic(
            components = composeComponents,
            rules = ComposeRuleSet.staticRules(),
            composeFunctions = composeFunctions
        )
        RuleMode.RUNTIME -> emptyList()
    }

    val runtimeIssues = if (ruleMode.includesRuntime) {
        Analyzer(rules = ComposeRuleSet.runtimeRules(expectedPackageName)).analyze(runtimeComponents)
    } else {
        emptyList()
    }

    return staticIssues + runtimeIssues
}

private fun analyzeStatic(
    components: List<UiComponent>,
    rules: List<Rule>,
    resourceRepository: ResourceRepository = ResourceRepository.empty(),
    composeFunctions: List<ru.itis.compose.source.model.ComposeFunction> = emptyList()
): List<AnalysisIssue> {
    return Analyzer(
        resourceRepository = resourceRepository,
        composeFunctions = composeFunctions,
        rules = rules
    ).analyze(components)
}

private fun componentsForReport(
    ruleMode: RuleMode,
    xmlComponents: List<UiComponent>,
    composeComponents: List<UiComponent>,
    runtimeComponents: List<UiComponent>
): List<UiComponent> {
    return when (ruleMode) {
        RuleMode.ALL -> xmlComponents + composeComponents + runtimeComponents
        RuleMode.STATIC -> xmlComponents + composeComponents
        RuleMode.XML -> xmlComponents
        RuleMode.COMPOSE -> composeComponents
        RuleMode.RUNTIME -> runtimeComponents
    }
}

private fun countComponents(component: UiComponent): Int {
    return 1 + component.children.sumOf { child -> countComponents(child) }
}

private data class CliOptions(
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

private enum class RuleMode(
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

private const val ARGUMENT_PREFIX = "--"
private const val RULES_ARGUMENT_PREFIX = "--rules="
private const val RUNTIME_ADB_ARGUMENT = "--runtime-adb"
