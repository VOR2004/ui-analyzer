package ru.itis.compose.source.psi

import java.io.File
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreApplicationEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreApplicationEnvironmentMode
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinFileType
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.parsing.KotlinParserDefinition
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory

internal object ComposePsiEnvironment {

    private val disposable = Disposer.newDisposable("compose-psi-environment")

    private val project: Project by lazy {
        ensureIdeaHomePath()
        configurePsiSystemProperties()

        val configuration = CompilerConfiguration().apply {
            put(CommonConfigurationKeys.MODULE_NAME, MODULE_NAME)
        }

        val applicationEnvironment = KotlinCoreApplicationEnvironment.create(
            disposable,
            KotlinCoreApplicationEnvironmentMode.fromUnitTestModeFlag(true)
        ).apply {
            registerFileType(KotlinFileType.INSTANCE, KotlinFileType.EXTENSION)
            registerParserDefinition(KotlinLanguage.INSTANCE, KotlinParserDefinition())
        }
        val projectEnvironment = KotlinCoreEnvironment.ProjectEnvironment(
            disposable,
            applicationEnvironment,
            configuration
        )

        projectEnvironment.project
    }

    fun createKtFile(fileName: String, source: String): KtFile {
        return KtPsiFactory(project, markGenerated = false)
            .createFile(fileName, source)
    }

    private fun ensureIdeaHomePath() {
        if (System.getProperty(IDEA_HOME_PATH_PROPERTY) != null) return

        val ideaHome = File(
            System.getProperty(JAVA_IO_TMPDIR_PROPERTY),
            IDEA_HOME_DIRECTORY_NAME
        )
        val binDirectory = File(ideaHome, IDEA_BIN_DIRECTORY_NAME)
        binDirectory.mkdirs()
        File(binDirectory, IDEA_PROPERTIES_FILE_NAME).createNewFile()

        System.setProperty(IDEA_HOME_PATH_PROPERTY, ideaHome.absolutePath)
    }

    private fun configurePsiSystemProperties() {
        setDefaultSystemProperty(
            name = PSI_SLEEP_IN_VALIDITY_CHECK_PROPERTY,
            value = PSI_SLEEP_IN_VALIDITY_CHECK_DEFAULT
        )
        setDefaultSystemProperty(
            name = PSI_INCREMENTAL_REPARSE_DEPTH_LIMIT_PROPERTY,
            value = PSI_INCREMENTAL_REPARSE_DEPTH_LIMIT_DEFAULT
        )
    }

    private fun setDefaultSystemProperty(name: String, value: String) {
        if (System.getProperty(name) == null) {
            System.setProperty(name, value)
        }
    }

    private const val MODULE_NAME = "ui-analyzer-compose-psi"
    private const val IDEA_HOME_PATH_PROPERTY = "idea.home.path"
    private const val JAVA_IO_TMPDIR_PROPERTY = "java.io.tmpdir"
    private const val IDEA_HOME_DIRECTORY_NAME = "ui-analyzer-idea-home"
    private const val IDEA_BIN_DIRECTORY_NAME = "bin"
    private const val IDEA_PROPERTIES_FILE_NAME = "idea.properties"
    private const val PSI_SLEEP_IN_VALIDITY_CHECK_PROPERTY = "psi.sleep.in.validity.check"
    private const val PSI_SLEEP_IN_VALIDITY_CHECK_DEFAULT = "false"
    private const val PSI_INCREMENTAL_REPARSE_DEPTH_LIMIT_PROPERTY = "psi.incremental.reparse.depth.limit"
    private const val PSI_INCREMENTAL_REPARSE_DEPTH_LIMIT_DEFAULT = "1000"
}
