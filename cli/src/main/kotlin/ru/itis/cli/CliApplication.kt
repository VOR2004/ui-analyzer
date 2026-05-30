package ru.itis.cli

import java.io.File
import ru.itis.analyzer.messages.cli.CliMessages
import ru.itis.android.project.AndroidProjectPackageResolver
import ru.itis.cli.analysis.CliAnalysisRunner
import ru.itis.cli.config.CliOptions
import ru.itis.cli.io.CliComponentLoader
import ru.itis.report.AutoReportGenerator
import ru.itis.report.ReportGenerator
import ru.itis.xml.source.resource.DefaultResourceRepository

class CliApplication(
    private val componentLoader: CliComponentLoader = CliComponentLoader(),
    private val analysisRunner: CliAnalysisRunner = CliAnalysisRunner(),
    private val projectPackageResolver: AndroidProjectPackageResolver = AndroidProjectPackageResolver(),
    private val reportGenerator: ReportGenerator = AutoReportGenerator()
) {

    fun run(args: Array<String>) {
        if (args.isEmpty()) {
            println(CliMessages.USAGE)
            return
        }

        val options = CliOptions.parse(args)
        val projectRoot = File(options.projectPath)
        if (!projectRoot.exists() || !projectRoot.isDirectory) {
            println(CliMessages.projectDirectoryDoesNotExist(options.projectPath))
            return
        }

        val resourceRepository = DefaultResourceRepository.load(projectRoot)
        val expectedPackageName = projectPackageResolver.resolve(projectRoot)
        val input = componentLoader.load(projectRoot, options)
        val components = input.componentsForReport(options.ruleMode)
        val issues = analysisRunner.analyze(
            ruleMode = options.ruleMode,
            input = input,
            resourceRepository = resourceRepository,
            expectedPackageName = expectedPackageName
        )

        reportGenerator.writeReport(File(options.outputPath), components, issues)

        println(CliMessages.ANALYSIS_COMPLETE)
        println(CliMessages.componentsParsed(components.size))
        println(CliMessages.issuesFound(issues.size))
        println(CliMessages.reportWrittenTo(options.outputPath))
    }
}
