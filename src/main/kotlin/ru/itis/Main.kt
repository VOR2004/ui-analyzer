package ru.itis

import ru.itis.analyzer.Analyzer
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.analyzer.rules.adaptive.button.AdaptiveButtonStyleOutlierRule
import ru.itis.analyzer.rules.adaptive.layout.AdaptiveSpacingOutlierRule
import ru.itis.analyzer.rules.compose.accessibility.ComposeImageContentDescriptionRule
import ru.itis.analyzer.rules.compose.api.ComposeMissingModifierParameterRule
import ru.itis.analyzer.rules.compose.color.ComposeHardcodedColorRule
import ru.itis.analyzer.rules.compose.text.ComposeHardcodedTextRule
import ru.itis.source.xml.resource.ResourceRepository
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
import ru.itis.analyzer.rules.static.structure.DeepLayoutNestingRule
import ru.itis.analyzer.rules.static.text.HardcodedTextRule
import ru.itis.analyzer.rules.static.text.SuspiciousTextSizeRule
import ru.itis.analyzer.rules.static.text.TextContrastRule
import ru.itis.analyzer.rules.static.text.TextSizeConsistencyRule
import ru.itis.report.JsonReportGenerator
import ru.itis.source.compose.importer.ComposeProjectImporter
import ru.itis.source.compose.parser.ComposeFunctionParser
import ru.itis.source.compose.parser.ComposeLayoutParser
import ru.itis.source.xml.importer.XmlProjectImporter
import ru.itis.source.xml.parser.XmlLayoutParser
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

    val xmlImporter = XmlProjectImporter()
    val xmlParser = XmlLayoutParser()
    val composeImporter = ComposeProjectImporter()
    val composeParser = ComposeLayoutParser()
    val composeFunctionParser = ComposeFunctionParser()
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
        rules = listOf(
//            SuspiciousTextSizeRule(resourceRepository),
//            ButtonColorPerLayoutConsistencyRule(resourceRepository),
//            ButtonColorProjectConsistencyRule(resourceRepository),
//            TextContrastRule(resourceRepository),
//            TextSizeConsistencyRule(resourceRepository),
//            HardcodedColorRule(),
//            NearDuplicateButtonColorRule(resourceRepository),
//            MissingIdRule(),
//            HardcodedDimensionRule(),
//            ImageWithoutContentDescriptionRule(),
            ComposeImageContentDescriptionRule(),
            ComposeHardcodedTextRule(),
            ComposeHardcodedColorRule(),
            ComposeMissingModifierParameterRule(),
//            DeepLayoutNestingRule(),
//            HardcodedTextRule(),
//            TouchTargetTooSmallRule(resourceRepository),
//            AdaptiveTextStyleOutlierRule(),
//            TooManyTextStylesOnScreenRule(),
//            AdaptiveTextSizeOutlierRule(),
//            AdaptiveSpacingOutlierRule(),
//            AdaptiveButtonStyleOutlierRule()
        )
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

    val components = xmlComponents + composeComponents
    val issues = analyzer.analyze(components)
    reportGenerator.writeReport(File(outputPath), components, issues)

    println(AnalyzerStrings.Cli.ANALYSIS_COMPLETE)
    println(AnalyzerStrings.Cli.componentsParsed(components.size))
    println(AnalyzerStrings.Cli.issuesFound(issues.size))
    println(AnalyzerStrings.Cli.reportWrittenTo(outputPath))
}
