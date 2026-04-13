package ru.itis

import ru.itis.analyzer.Analyzer
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.analyzer.rules.adaptive.button.AdaptiveButtonStyleOutlierRule
import ru.itis.analyzer.rules.adaptive.layout.AdaptiveSpacingOutlierRule
import ru.itis.analyzer.resource.ResourceRepository
import ru.itis.analyzer.rules.adaptive.text.AdaptiveTextSizeOutlierRule
import ru.itis.analyzer.rules.adaptive.text.AdaptiveTextStyleOutlierRule
import ru.itis.analyzer.rules.adaptive.text.TooManyTextStylesOnScreenRule
import ru.itis.analyzer.rules.static.color.ButtonColorPerLayoutConsistencyRule
import ru.itis.analyzer.rules.static.color.ButtonColorProjectConsistencyRule
import ru.itis.analyzer.rules.static.accessibility.TouchTargetTooSmallRule
import ru.itis.analyzer.rules.static.color.HardcodedColorRule
import ru.itis.analyzer.rules.static.color.NearDuplicateButtonColorRule
import ru.itis.analyzer.rules.static.common.HardcodedDimensionRule
import ru.itis.analyzer.rules.static.common.MissingIdRule
import ru.itis.analyzer.rules.static.image.ImageWithoutContentDescriptionRule
import ru.itis.analyzer.rules.static.text.HardcodedTextRule
import ru.itis.analyzer.rules.static.text.SuspiciousTextSizeRule
import ru.itis.analyzer.rules.static.text.TextContrastRule
import ru.itis.analyzer.rules.static.text.TextSizeConsistencyRule
import ru.itis.importer.ProjectImporter
import ru.itis.parser.XmlLayoutParser
import ru.itis.report.JsonReportGenerator
import java.io.File

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println(AnalyzerStrings.Cli.USAGE)
        return
    }

    val projectPath = args[0]
    val outputPath = args.getOrNull(1) ?: AnalyzerStrings.Cli.DEFAULT_OUTPUT_PATH

    val projectRoot = File(projectPath)
    if (!projectRoot.exists() || !projectRoot.isDirectory) {
        println(AnalyzerStrings.Cli.projectDirectoryDoesNotExist(projectPath))
        return
    }

    val importer = ProjectImporter()
    val parser = XmlLayoutParser()
    val resourceRepository = ResourceRepository.load(projectRoot)

    val analyzer = Analyzer(
        resourceRepository = resourceRepository,
        rules = listOf(
            SuspiciousTextSizeRule(resourceRepository),
            ButtonColorPerLayoutConsistencyRule(resourceRepository),
            ButtonColorProjectConsistencyRule(resourceRepository),
            TextContrastRule(resourceRepository),
            TextSizeConsistencyRule(resourceRepository),
            HardcodedColorRule(),
            NearDuplicateButtonColorRule(resourceRepository),
            MissingIdRule(),
            HardcodedDimensionRule(),
            ImageWithoutContentDescriptionRule(),
            HardcodedTextRule(),
            TouchTargetTooSmallRule(resourceRepository),
            AdaptiveTextStyleOutlierRule(),
            TooManyTextStylesOnScreenRule(),
            AdaptiveTextSizeOutlierRule(),
            AdaptiveSpacingOutlierRule(),
            AdaptiveButtonStyleOutlierRule()
        )
    )

    val reportGenerator = JsonReportGenerator()

    val xmlFiles = importer.findLayoutXmlFiles(projectRoot)
    println(AnalyzerStrings.Cli.foundLayoutXmlFiles(xmlFiles.size))

    val components = xmlFiles.mapNotNull { file ->
        runCatching { parser.parse(file) }
            .onFailure { error ->
                println(AnalyzerStrings.Cli.failedToParse(file.path, error.message))
            }
            .getOrNull()
    }

    val issues = analyzer.analyze(components)
    reportGenerator.writeReport(File(outputPath), components, issues)

    println(AnalyzerStrings.Cli.ANALYSIS_COMPLETE)
    println(AnalyzerStrings.Cli.componentsParsed(components.size))
    println(AnalyzerStrings.Cli.issuesFound(issues.size))
    println(AnalyzerStrings.Cli.reportWrittenTo(outputPath))
}
