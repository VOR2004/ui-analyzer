package ru.itis.analyzer.messages

object AnalyzerStrings {

    object RuleIds {
        const val ADAPTIVE_BUTTON_STYLE_OUTLIER = "adaptive-button-style-outlier"
        const val ADAPTIVE_SPACING_OUTLIER = "adaptive-spacing-outlier"
        const val ADAPTIVE_TEXT_STYLE_OUTLIER = "adaptive-text-style-outlier"
        const val ADAPTIVE_TEXT_SIZE_OUTLIER = "adaptive-text-size-outlier"
        const val TOO_MANY_TEXT_STYLES_ON_SCREEN = "too-many-text-styles-on-screen"
        const val BUTTON_COLOR_PER_LAYOUT_CONSISTENCY = "button-color-per-layout-consistency"
        const val BUTTON_COLOR_PROJECT_CONSISTENCY = "button-color-project-consistency"
        const val HARDCODED_COLOR = "hardcoded-color"
        const val NEAR_DUPLICATE_BUTTON_COLORS = "near-duplicate-button-colors"
        const val HARDCODED_DIMENSION = "hardcoded-dimension"
        const val MISSING_ID = "missing-id"
        const val IMAGE_WITHOUT_CONTENT_DESCRIPTION = "image-without-content-description"
        const val HARDCODED_TEXT = "hardcoded-text"
        const val SUSPICIOUS_TEXT_SIZE = "suspicious-text-size"
        const val TEXT_CONTRAST = "text-contrast"
        const val TEXT_SIZE_CONSISTENCY = "text-size-consistency"
        const val TOUCH_TARGET_TOO_SMALL = "touch-target-too-small"
        const val DEEP_LAYOUT_NESTING = "deep-layout-nesting"

        fun nearDuplicateCluster(baseId: String): String = "$baseId-near-duplicate-cluster"
    }

    object Cli {
        const val USAGE = "Usage: ui-analyzer <path-to-android-project> [output-file]"
        const val DEFAULT_OUTPUT_PATH = "analysis-report.json"
        const val ANALYSIS_COMPLETE = "Analysis complete"

        fun projectDirectoryDoesNotExist(path: String): String =
            "Project directory does not exist: $path"

        fun foundLayoutXmlFiles(count: Int): String =
            "Found $count layout XML files"

        fun failedToParse(path: String, message: String?): String =
            "Failed to parse $path: $message"

        fun componentsParsed(count: Int): String =
            "Components parsed: $count"

        fun issuesFound(count: Int): String =
            "Issues found: $count"

        fun reportWrittenTo(path: String): String =
            "Report written to: $path"
    }

    object PropertyNames {
        const val WIDTH = "width"
        const val HEIGHT = "height"
        const val BACKGROUND = "background"
        const val BACKGROUND_TINT = "backgroundTint"
        const val TINT = "tint"
        const val TEXT_COLOR = "textColor"
        const val TEXT_SIZE = "textSize"
        const val PADDING = "padding"
        const val MARGIN = "margin"
    }

    object Messages {
        fun tooManyTextStylesOnScreen(
            role: String,
            actualCount: Int,
            dominantSharePercent: Int
        ): String =
            "Для predicted text role $role на экране используется слишком много конкурирующих текстовых стилей: $actualCount. Доминирующий стиль покрывает только $dominantSharePercent% элементов этой роли."

        fun tooManyTextStylesOnScreenRecommendation(role: String): String =
            "Проверьте, должна ли predicted text role $role использовать более согласованный типографический стиль."

        fun touchTargetTooSmall(width: String?, height: String?): String =
            "Размер интерактивного элемента слишком мал для удобного нажатия: width=$width, height=$height."

        const val TOUCH_TARGET_TOO_SMALL_RECOMMENDATION =
            "Рекомендуется делать интерактивные элементы не меньше 48dp по ширине и высоте."

        fun adaptiveButtonStyleOutlier(differences: String): String =
            "Стиль кнопки отличается от доминирующего стиля кнопок на этом экране по признакам: $differences."

        fun adaptiveButtonStyleOutlierRecommendation(dominantStyle: String): String =
            "Проверьте, должна ли эта кнопка использовать доминирующий стиль экрана: $dominantStyle."

        fun adaptiveSpacingOutlier(propertyName: String, actualValue: Float): String =
            "Значение $propertyName = ${actualValue}dp выбивается из типичной шкалы отступов экрана."

        fun adaptiveSpacingOutlierRecommendation(expected: String): String =
            "Проверьте, должен ли элемент использовать один из типичных отступов: $expected."

        fun adaptiveTextSizeOutlier(actualValue: Float, predictedRole: String? = null): String {
            val roleSuffix = predictedRole?.let { " для predicted text role $it" }.orEmpty()
            return "Размер текста ${actualValue}sp выбивается из типичных размеров текста$roleSuffix на этом экране."
        }

        fun adaptiveTextSizeOutlierRecommendation(expected: String): String =
            "Проверьте, должен ли элемент использовать один из типичных размеров текста: $expected."

        fun adaptiveTextStyleOutlier(differences: String): String =
            "Стиль текста отличается от доминирующего predicted text role style на этом экране по признакам: $differences."

        fun adaptiveTextStyleOutlierRecommendation(dominantStyle: String): String =
            "Проверьте, должен ли элемент использовать доминирующий стиль для своей predicted text role: $dominantStyle."

        fun buttonColorPerLayoutNearDuplicate(
            color: String,
            canonicalColor: String,
            distance: String
        ): String =
            "В этом layout-файле цвет кнопки $color очень близок к $canonicalColor (distance=$distance), который чаще встречается среди похожих оттенков."

        fun buttonColorPerLayoutNearDuplicateRecommendation(canonicalColor: String): String =
            "Используйте $canonicalColor как единый оттенок для визуально одинаковых кнопок в этом экране."

        fun buttonColorPerLayoutNearDominant(
            color: String,
            dominantColor: String,
            distance: String
        ): String =
            "В layout-файле используется цвет кнопки $color, который почти совпадает с основным цветом кнопок $dominantColor, но отличается (distance=$distance)."

        fun buttonColorPerLayoutDifferent(color: String, dominantColor: String): String =
            "В layout-файле используется цвет кнопки $color, который отличается от наиболее частого цвета кнопок $dominantColor."

        const val BUTTON_COLOR_PER_LAYOUT_RECOMMENDATION =
            "Приведите цвета кнопок в этом layout-файле к одному согласованному стилю."

        fun buttonColorProjectNearDuplicate(
            color: String,
            canonicalColor: String,
            distance: String
        ): String =
            "Цвет кнопки $color очень близок к оттенку $canonicalColor (distance=$distance), который чаще встречается среди похожих цветов кнопок в проекте."

        fun buttonColorProjectNearDuplicateRecommendation(canonicalColor: String): String =
            "Используйте $canonicalColor как основной оттенок в этой группе близких цветов, чтобы избежать почти незаметных расхождений по всему проекту."

        fun buttonColorProjectNearDominant(
            color: String,
            dominantColor: String,
            distance: String
        ): String =
            "Цвет кнопки $color почти совпадает с наиболее распространенным цветом кнопок в проекте $dominantColor, но отличается (distance=$distance)."

        fun buttonColorProjectDifferent(color: String, dominantColor: String): String =
            "Цвет кнопки $color отличается от наиболее распространенного цвета кнопок в проекте $dominantColor."

        const val BUTTON_COLOR_PROJECT_RECOMMENDATION =
            "Проверьте, соответствует ли эта кнопка глобальному цветовому стилю проекта."

        fun hardcodedColor(propertyName: String, color: String): String =
            "Свойство $propertyName использует захардкоженный цвет $color."

        const val HARDCODED_COLOR_RECOMMENDATION =
            "Вынесите цвет в ресурс `colors.xml` и используйте ссылку вида `@color/...`."

        fun nearDuplicateButtonColors(firstColor: String, secondColor: String, distance: Double): String =
            "Цвет кнопки $firstColor почти совпадает с цветом $secondColor у другой кнопки (distance=%.2f).".format(distance)

        const val NEAR_DUPLICATE_BUTTON_COLORS_RECOMMENDATION =
            "Проверьте, не является ли это случайным отклонением от общего цветового стиля."

        fun hardcodedDimension(propertyName: String, value: String?): String =
            "Свойство $propertyName использует захардкоженный размер $value."

        const val HARDCODED_DIMENSION_RECOMMENDATION =
            "Вынесите размер в ресурс `dimens.xml` или используйте общий стиль."

        fun missingId(componentType: String): String =
            "Компонент $componentType не содержит android:id."

        const val MISSING_ID_RECOMMENDATION =
            "Добавьте идентификатор, если элемент используется в коде, тестах или должен быть однозначно различим."

        fun imageWithoutContentDescription(componentType: String): String =
            "Изображение $componentType не содержит android:contentDescription."

        const val IMAGE_WITHOUT_CONTENT_DESCRIPTION_RECOMMENDATION =
            "Добавьте contentDescription для улучшения доступности. Если изображение декоративное, явно укажите @null."

        fun hardcodedText(componentType: String, text: String): String =
            "Компонент $componentType использует захардкоженный текст: \"$text\"."

        const val HARDCODED_TEXT_RECOMMENDATION =
            "Вынесите текст в `strings.xml` и используйте ссылку вида `@string/...`."

        fun suspiciousTextSizeTooSmall(rawTextSize: String): String =
            "Подозрительно маленький размер текста: $rawTextSize."

        const val SUSPICIOUS_TEXT_SIZE_TOO_SMALL_RECOMMENDATION =
            "Проверьте значение android:textSize: возможно, здесь ошибка в разметке."

        fun suspiciousTextSizeTooLarge(rawTextSize: String): String =
            "Подозрительно большой размер текста: $rawTextSize."

        const val SUSPICIOUS_TEXT_SIZE_TOO_LARGE_RECOMMENDATION =
            "Проверьте, соответствует ли такой размер текста назначению элемента."

        fun insufficientTextContrast(
            textColor: String,
            backgroundColor: String,
            ratio: String,
            minContrast: Double
        ): String =
            "Недостаточный контраст текста ($textColor) и фона ($backgroundColor): ratio=$ratio (требуется >= $minContrast)."

        const val INSUFFICIENT_TEXT_CONTRAST_RECOMMENDATION =
            "Увеличьте контраст между текстом и фоном для лучшей читаемости."

        fun textSizeConsistency(size: String, dominantSize: String?): String =
            "Размер текста $size отличается от наиболее часто используемого размера $dominantSize."

        fun deepLayoutNesting(depth: Int): String =
            "Глубина вложенности layout-дерева слишком большая: $depth уровней."

        const val DEEP_LAYOUT_NESTING_RECOMMENDATION =
            "Проверьте структуру разметки: глубокую вложенность стоит упростить через ConstraintLayout, include/merge или более плоскую композицию."

        const val TEXT_SIZE_CONSISTENCY_RECOMMENDATION =
            "Проверьте, должен ли этот текст использовать общий типографический стиль."
    }
}
