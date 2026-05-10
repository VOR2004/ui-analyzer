package ru.itis

import ru.itis.analyzer.Analyzer
import ru.itis.analyzer.messages.AnalyzerStrings
import ru.itis.compose.rules.ComposeRuleSet
import ru.itis.report.JsonReportGenerator
import ru.itis.compose.source.importer.ComposeProjectImporter
import ru.itis.compose.source.parser.ComposeFunctionParser
import ru.itis.compose.source.psi.ComposePsiLayoutParser
import ru.itis.xml.source.importer.XmlProjectImporter
import ru.itis.xml.source.parser.XmlLayoutParser
import ru.itis.xml.source.resource.ResourceRepository
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
    val composeParser = ComposePsiLayoutParser()
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
        rules = ComposeRuleSet.default()
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
