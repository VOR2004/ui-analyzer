package ru.itis.desktop.ui.app

data class DesktopTitleBarActions(
    val onOpenProject: () -> Unit = {},
    val onSelectReportOutput: () -> Unit = {},
    val onToggleRules: () -> Unit = {},
    val onRunAnalysis: () -> Unit = {},
    val isRunning: Boolean = false,
    val rulesPanelVisible: Boolean = true
)
