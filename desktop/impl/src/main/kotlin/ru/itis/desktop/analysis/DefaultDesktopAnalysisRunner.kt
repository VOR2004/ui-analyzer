package ru.itis.desktop.analysis

import java.io.File
import ru.itis.analyzer.Analyzer
import ru.itis.android.project.AndroidProjectPackageResolver
import ru.itis.android.runtime.adb.provider.AdbUiAutomatorSnapshotProvider
import ru.itis.compose.runtime.RuntimePackageGuard
import ru.itis.compose.runtime.importer.ComposeRuntimeSnapshotImporter
import ru.itis.compose.source.importer.ComposeProjectImporter
import ru.itis.compose.source.model.ComposeFunction
import ru.itis.compose.source.parser.ComposeFunctionParser
import ru.itis.compose.source.psi.ComposePsiLayoutParser
import ru.itis.model.AnalysisIssue
import ru.itis.model.UiComponent
import ru.itis.report.AnalysisReportBuilder
import ru.itis.report.AutoReportGenerator
import ru.itis.report.ReportGenerator
import ru.itis.desktop.text.DesktopAnalysisText
import ru.itis.xml.source.importer.XmlProjectImporter
import ru.itis.xml.source.parser.XmlLayoutParser
import ru.itis.xml.source.resource.DefaultResourceRepository
import ru.itis.xml.source.resource.ResourceRepository

class DefaultDesktopAnalysisRunner(
    private val xmlImporter: XmlProjectImporter = XmlProjectImporter(),
    private val xmlParser: XmlLayoutParser = XmlLayoutParser(),
    private val composeImporter: ComposeProjectImporter = ComposeProjectImporter(),
    private val composeParser: ComposePsiLayoutParser = ComposePsiLayoutParser(),
    private val composeFunctionParser: ComposeFunctionParser = ComposeFunctionParser(),
    private val runtimeSnapshotImporter: ComposeRuntimeSnapshotImporter = ComposeRuntimeSnapshotImporter(),
    private val adbSnapshotProvider: AdbUiAutomatorSnapshotProvider = AdbUiAutomatorSnapshotProvider(),
    private val projectPackageResolver: AndroidProjectPackageResolver = AndroidProjectPackageResolver(),
    private val reportBuilder: AnalysisReportBuilder = AnalysisReportBuilder(),
    private val reportGenerator: ReportGenerator = AutoReportGenerator(),
    private val ruleRegistry: DefaultDesktopRuleRegistry = DefaultDesktopRuleRegistry()
) : DesktopAnalysisRunner {

    override fun run(request: DesktopAnalysisRequest): DesktopAnalysisResult {
        require(request.selectedRuleIds.isNotEmpty()) { DesktopAnalysisText.SELECT_RULE_ERROR }

        val projectRoot = File(request.projectPath)
        require(projectRoot.exists() && projectRoot.isDirectory) {
            DesktopAnalysisText.missingProjectDirectory(request.projectPath)
        }

        val outputFile = File(request.outputPath)
        outputFile.parentFile?.mkdirs()

        return when (request.mode) {
            DesktopAnalysisMode.STATIC -> runStatic(projectRoot, outputFile, request)
            DesktopAnalysisMode.RUNTIME -> runRuntime(projectRoot, outputFile, request)
        }
    }

    private fun runStatic(
        projectRoot: File,
        outputFile: File,
        request: DesktopAnalysisRequest
    ): DesktopAnalysisResult {
        val resourceRepository = DefaultResourceRepository.load(projectRoot)
        val xmlComponents = if (request.staticTarget.includesXml) {
            xmlImporter.findLayoutXmlFiles(projectRoot)
                .mapNotNull { file -> runCatching { xmlParser.parse(file) }.getOrNull() }
        } else {
            emptyList()
        }
        val composeFiles = if (request.staticTarget.includesCompose) {
            composeImporter.findComposeKotlinFiles(projectRoot)
        } else {
            emptyList()
        }
        val composeComponents = composeFiles.flatMap { file ->
            runCatching { composeParser.parse(file) }.getOrDefault(emptyList())
        }
        val composeFunctions = composeFiles.flatMap { file ->
            runCatching { composeFunctionParser.parse(file) }.getOrDefault(emptyList())
        }
        val components = xmlComponents + composeComponents
        val issues = analyzeStatic(
            components = components,
            resourceRepository = resourceRepository,
            composeFunctions = composeFunctions,
            request = request
        )
        val report = reportBuilder.build(components, issues)

        reportGenerator.writeReport(outputFile, components, report.issues)
        return DesktopAnalysisResult(
            componentCount = report.summary.totalComponents,
            issueCount = report.summary.totalIssues,
            outputPath = outputFile.path,
            issues = report.issues
        )
    }

    private fun runRuntime(
        projectRoot: File,
        outputFile: File,
        request: DesktopAnalysisRequest
    ): DesktopAnalysisResult {
        val runtimeComponents = when (request.runtimeSource) {
            RuntimeSnapshotSource.ADB -> adbSnapshotProvider.capture(request.adbSerial?.takeIf { it.isNotBlank() })
            RuntimeSnapshotSource.SNAPSHOT_FILE -> {
                val snapshotFile = File(request.runtimeSnapshotPath)
                require(snapshotFile.exists() && snapshotFile.isFile) {
                    DesktopAnalysisText.missingRuntimeSnapshot(request.runtimeSnapshotPath)
                }
                runtimeSnapshotImporter.import(snapshotFile)
            }
        }
        val expectedPackageName = projectPackageResolver.resolve(projectRoot)
        val rules = if (RuntimePackageGuard.hasPackageMismatch(runtimeComponents, expectedPackageName)) {
            ruleRegistry.runtimeDiagnosticRules(expectedPackageName, request.selectedRuleIds)
        } else {
            ruleRegistry.runtimeRules(expectedPackageName, request.selectedRuleIds)
        }
        val issues = Analyzer(rules = rules).analyze(runtimeComponents)
        val report = reportBuilder.build(runtimeComponents, issues)

        reportGenerator.writeReport(outputFile, runtimeComponents, report.issues)
        return DesktopAnalysisResult(
            componentCount = report.summary.totalComponents,
            issueCount = report.summary.totalIssues,
            outputPath = outputFile.path,
            issues = report.issues
        )
    }

    private fun analyzeStatic(
        components: List<UiComponent>,
        resourceRepository: ResourceRepository,
        composeFunctions: List<ComposeFunction>,
        request: DesktopAnalysisRequest
    ): List<AnalysisIssue> {
        val rules = ruleRegistry.staticRules(
            resourceRepository = resourceRepository,
            staticTarget = request.staticTarget,
            selectedRuleIds = request.selectedRuleIds
        )
        return Analyzer(
            resourceRepository = resourceRepository,
            composeFunctions = composeFunctions,
            rules = rules
        ).analyze(components)
    }

    private fun countComponents(component: UiComponent): Int {
        return 1 + component.children.sumOf { child -> countComponents(child) }
    }

    private val StaticSourceTarget.includesXml: Boolean
        get() = this == StaticSourceTarget.XML || this == StaticSourceTarget.BOTH

    private val StaticSourceTarget.includesCompose: Boolean
        get() = this == StaticSourceTarget.COMPOSE || this == StaticSourceTarget.BOTH
}
