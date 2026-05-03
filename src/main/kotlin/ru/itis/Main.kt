package ru.itis

import ru.itis.analyzer.Analyzer
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.xml.rules.adaptive.button.XmlAdaptiveButtonStyleOutlierRule
import ru.itis.xml.rules.adaptive.layout.XmlAdaptiveSpacingOutlierRule
import ru.itis.xml.rules.adaptive.layout.XmlNearDuplicateSpacingClusterRule
import ru.itis.compose.rules.accessibility.ComposeImageContentDescriptionRule
import ru.itis.compose.rules.accessibility.ComposeTouchTargetTooSmallRule
import ru.itis.compose.rules.api.ComposeMissingModifierParameterRule
import ru.itis.compose.rules.color.ComposeButtonColorPerFileConsistencyRule
import ru.itis.compose.rules.color.ComposeHardcodedColorRule
import ru.itis.compose.rules.layout.ComposeAdaptiveSpacingOutlierRule
import ru.itis.compose.rules.layout.ComposeNearDuplicateSpacingClusterRule
import ru.itis.compose.rules.text.ComposeAdaptiveTextStyleOutlierRule
import ru.itis.compose.rules.text.ComposeHardcodedTextRule
import ru.itis.compose.rules.text.ComposeTextSizeNearDuplicateClusterRule
import ru.itis.compose.rules.text.ComposeTooManyTextStylesOnScreenRule
import ru.itis.xml.source.resource.ResourceRepository
import ru.itis.xml.rules.adaptive.text.XmlAdaptiveTextSizeOutlierRule
import ru.itis.xml.rules.adaptive.text.XmlAdaptiveTextStyleOutlierRule
import ru.itis.xml.rules.adaptive.text.XmlTooManyTextStylesOnScreenRule
import ru.itis.xml.rules.adaptive.text.XmlTextSizeNearDuplicateClusterRule
import ru.itis.xml.rules.static.color.XmlButtonColorPerLayoutConsistencyRule
import ru.itis.xml.rules.static.color.XmlButtonColorProjectConsistencyRule
import ru.itis.xml.rules.static.accessibility.XmlTouchTargetTooSmallRule
import ru.itis.xml.rules.static.color.XmlHardcodedColorRule
import ru.itis.xml.rules.static.color.XmlNearDuplicateButtonColorRule
import ru.itis.xml.rules.static.common.XmlHardcodedDimensionRule
import ru.itis.xml.rules.static.common.XmlMissingIdRule
import ru.itis.xml.rules.static.image.XmlImageWithoutContentDescriptionRule
import ru.itis.xml.rules.static.structure.XmlDeepLayoutNestingRule
import ru.itis.xml.rules.static.text.XmlHardcodedTextRule
import ru.itis.xml.rules.static.text.XmlSuspiciousTextSizeRule
import ru.itis.xml.rules.static.text.XmlTextContrastRule
import ru.itis.xml.rules.static.text.XmlTextSizeConsistencyRule
import ru.itis.report.JsonReportGenerator
import ru.itis.compose.source.importer.ComposeProjectImporter
import ru.itis.compose.source.parser.ComposeFunctionParser
import ru.itis.compose.source.parser.ComposeLayoutParser
import ru.itis.xml.source.importer.XmlProjectImporter
import ru.itis.xml.source.parser.XmlLayoutParser
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
//            XmlSuspiciousTextSizeRule(resourceRepository),
//            XmlButtonColorPerLayoutConsistencyRule(resourceRepository),
//            XmlButtonColorProjectConsistencyRule(resourceRepository),
//            XmlTextContrastRule(resourceRepository),
//            XmlTextSizeConsistencyRule(resourceRepository),
//            XmlHardcodedColorRule(),
//            XmlNearDuplicateButtonColorRule(resourceRepository),
//            XmlMissingIdRule(),
//            XmlHardcodedDimensionRule(),
//            XmlImageWithoutContentDescriptionRule(),
            ComposeImageContentDescriptionRule(),
            ComposeTouchTargetTooSmallRule(),
            ComposeHardcodedTextRule(),
            ComposeTooManyTextStylesOnScreenRule(),
            ComposeAdaptiveTextStyleOutlierRule(),
            ComposeTextSizeNearDuplicateClusterRule(),
            ComposeHardcodedColorRule(),
            ComposeButtonColorPerFileConsistencyRule(),
            ComposeMissingModifierParameterRule(),
            ComposeAdaptiveSpacingOutlierRule(),
            ComposeNearDuplicateSpacingClusterRule(),
//            XmlDeepLayoutNestingRule(),
//            XmlHardcodedTextRule(),
//            XmlTouchTargetTooSmallRule(resourceRepository),
//            XmlAdaptiveTextStyleOutlierRule(),
//            XmlTooManyTextStylesOnScreenRule(),
//            XmlAdaptiveTextSizeOutlierRule(),
//            XmlTextSizeNearDuplicateClusterRule(),
//            XmlAdaptiveSpacingOutlierRule(),
//            XmlNearDuplicateSpacingClusterRule(),
//            XmlAdaptiveButtonStyleOutlierRule()
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
