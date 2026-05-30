package ru.itis.report.markdown

internal class MarkdownWriter {
    private val builder = StringBuilder()

    fun heading(level: Int, text: String) {
        line("${"#".repeat(level)} $text")
        blankLine()
    }

    fun line(text: String = "") {
        builder.appendLine(text)
    }

    fun blankLine() {
        line()
    }

    fun horizontalRule() {
        line("---")
        blankLine()
    }

    fun bullet(text: String, indent: Int = 0) {
        line("${"  ".repeat(indent)}- $text")
    }

    fun table(
        headers: List<String>,
        alignment: List<String>,
        rows: List<List<String>>
    ) {
        line(headers.joinToString(prefix = "| ", separator = " | ", postfix = " |"))
        line(alignment.joinToString(prefix = "| ", separator = " | ", postfix = " |"))
        rows.forEach { row ->
            line(row.joinToString(prefix = "| ", separator = " | ", postfix = " |"))
        }
        blankLine()
    }

    fun anchor(id: String) {
        line("""<a id="$id"></a>""")
        blankLine()
    }

    fun details(
        summary: String,
        open: Boolean = false,
        content: MarkdownWriter.() -> Unit
    ) {
        line(if (open) "<details open>" else "<details>")
        line("<summary>$summary</summary>")
        blankLine()
        content()
        line("</details>")
        blankLine()
    }

    fun codeBlock(language: String, content: String) {
        line("```$language")
        line(content)
        line("```")
        blankLine()
    }

    override fun toString(): String = builder.toString()
}