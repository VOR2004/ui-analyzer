package ru.itis.analyzer.messages.analyzer

object AnalyzerMessages {
    fun tooManyTextStylesOnScreen(
        role: String,
        actualCount: Int,
        dominantSharePercent: Int
    ): String =
        "Р”Р»СЏ predicted text role $role РЅР° СЌРєСЂР°РЅРµ РёСЃРїРѕР»СЊР·СѓРµС‚СЃСЏ СЃР»РёС€РєРѕРј РјРЅРѕРіРѕ РєРѕРЅРєСѓСЂРёСЂСѓСЋС‰РёС… С‚РµРєСЃС‚РѕРІС‹С… СЃС‚РёР»РµР№: $actualCount. Р”РѕРјРёРЅРёСЂСѓСЋС‰РёР№ СЃС‚РёР»СЊ РїРѕРєСЂС‹РІР°РµС‚ С‚РѕР»СЊРєРѕ $dominantSharePercent% СЌР»РµРјРµРЅС‚РѕРІ СЌС‚РѕР№ СЂРѕР»Рё."

    fun tooManyTextStylesOnScreenRecommendation(role: String): String =
        "РџСЂРѕРІРµСЂСЊС‚Рµ, РґРѕР»Р¶РЅР° Р»Рё predicted text role $role РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ Р±РѕР»РµРµ СЃРѕРіР»Р°СЃРѕРІР°РЅРЅС‹Р№ С‚РёРїРѕРіСЂР°С„РёС‡РµСЃРєРёР№ СЃС‚РёР»СЊ."

    fun touchTargetTooSmall(width: String?, height: String?): String =
        "Р Р°Р·РјРµСЂ РёРЅС‚РµСЂР°РєС‚РёРІРЅРѕРіРѕ СЌР»РµРјРµРЅС‚Р° СЃР»РёС€РєРѕРј РјР°Р» РґР»СЏ СѓРґРѕР±РЅРѕРіРѕ РЅР°Р¶Р°С‚РёСЏ: width=$width, height=$height."

    const val TOUCH_TARGET_TOO_SMALL_RECOMMENDATION =
        "Р РµРєРѕРјРµРЅРґСѓРµС‚СЃСЏ РґРµР»Р°С‚СЊ РёРЅС‚РµСЂР°РєС‚РёРІРЅС‹Рµ СЌР»РµРјРµРЅС‚С‹ РЅРµ РјРµРЅСЊС€Рµ 48dp РїРѕ С€РёСЂРёРЅРµ Рё РІС‹СЃРѕС‚Рµ."

    fun adaptiveButtonStyleOutlier(differences: String): String =
        "РЎС‚РёР»СЊ РєРЅРѕРїРєРё РѕС‚Р»РёС‡Р°РµС‚СЃСЏ РѕС‚ РґРѕРјРёРЅРёСЂСѓСЋС‰РµРіРѕ СЃС‚РёР»СЏ РєРЅРѕРїРѕРє РЅР° СЌС‚РѕРј СЌРєСЂР°РЅРµ РїРѕ РїСЂРёР·РЅР°РєР°Рј: $differences."

    fun adaptiveButtonStyleOutlierRecommendation(dominantStyle: String): String =
        "РџСЂРѕРІРµСЂСЊС‚Рµ, РґРѕР»Р¶РЅР° Р»Рё СЌС‚Р° РєРЅРѕРїРєР° РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ РґРѕРјРёРЅРёСЂСѓСЋС‰РёР№ СЃС‚РёР»СЊ СЌРєСЂР°РЅР°: $dominantStyle."

    fun adaptiveSpacingOutlier(propertyName: String, actualValue: Float): String =
        "Р—РЅР°С‡РµРЅРёРµ $propertyName = ${actualValue}dp РІС‹Р±РёРІР°РµС‚СЃСЏ РёР· С‚РёРїРёС‡РЅРѕР№ С€РєР°Р»С‹ РѕС‚СЃС‚СѓРїРѕРІ СЌРєСЂР°РЅР°."

    fun adaptiveSpacingOutlierRecommendation(expected: String): String =
        "РџСЂРѕРІРµСЂСЊС‚Рµ, РґРѕР»Р¶РµРЅ Р»Рё СЌР»РµРјРµРЅС‚ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ РѕРґРёРЅ РёР· С‚РёРїРёС‡РЅС‹С… РѕС‚СЃС‚СѓРїРѕРІ: $expected."

    fun xmlNearDuplicateSpacingCluster(
        propertyName: String,
        value: Float,
        canonicalValue: Float
    ): String =
        "XML spacing-Р·РЅР°С‡РµРЅРёРµ $propertyName=${value}dp РѕС‡РµРЅСЊ Р±Р»РёР·РєРѕ Рє ${canonicalValue}dp, РєРѕС‚РѕСЂС‹Р№ С‡Р°С‰Рµ РёСЃРїРѕР»СЊР·СѓРµС‚СЃСЏ РІ СЌС‚РѕРј layout-С„Р°Р№Р»Рµ."

    fun xmlNearDuplicateSpacingClusterRecommendation(canonicalValue: Float): String =
        "РџСЂРѕРІРµСЂСЊС‚Рµ, РјРѕР¶РЅРѕ Р»Рё Р·Р°РјРµРЅРёС‚СЊ Р·РЅР°С‡РµРЅРёРµ РЅР° ${canonicalValue}dp РёР»Рё РѕР±С‰РёР№ @dimen-С‚РѕРєРµРЅ, С‡С‚РѕР±С‹ СѓРјРµРЅСЊС€РёС‚СЊ РґСЂРѕР±Р»РµРЅРёРµ spacing-С€РєР°Р»С‹."

    fun xmlTextSizeNearDuplicateCluster(
        value: Float,
        canonicalValue: Float,
        predictedRole: String
    ): String =
        "XML textSize=${value}sp РѕС‡РµРЅСЊ Р±Р»РёР·РѕРє Рє ${canonicalValue}sp РІРЅСѓС‚СЂРё predicted text role $predictedRole."

    fun xmlTextSizeNearDuplicateClusterRecommendation(
        canonicalValue: Float,
        predictedRole: String
    ): String =
        "РџСЂРѕРІРµСЂСЊС‚Рµ, РјРѕР¶РЅРѕ Р»Рё РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ ${canonicalValue}sp РёР»Рё РѕР±С‰РёР№ @dimen-С‚РѕРєРµРЅ РґР»СЏ predictedRole=$predictedRole, С‡С‚РѕР±С‹ СѓРјРµРЅСЊС€РёС‚СЊ РґСЂРѕР±Р»РµРЅРёРµ С‚РёРїРѕРіСЂР°С„РёС‡РµСЃРєРѕР№ С€РєР°Р»С‹."

    fun adaptiveTextSizeOutlier(actualValue: Float, predictedRole: String? = null): String {
        val roleSuffix = predictedRole?.let { " РґР»СЏ predicted text role $it" }.orEmpty()
        return "Р Р°Р·РјРµСЂ С‚РµРєСЃС‚Р° ${actualValue}sp РІС‹Р±РёРІР°РµС‚СЃСЏ РёР· С‚РёРїРёС‡РЅС‹С… СЂР°Р·РјРµСЂРѕРІ С‚РµРєСЃС‚Р°$roleSuffix РЅР° СЌС‚РѕРј СЌРєСЂР°РЅРµ."
    }

    fun adaptiveTextSizeOutlierRecommendation(expected: String): String =
        "РџСЂРѕРІРµСЂСЊС‚Рµ, РґРѕР»Р¶РµРЅ Р»Рё СЌР»РµРјРµРЅС‚ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ РѕРґРёРЅ РёР· С‚РёРїРёС‡РЅС‹С… СЂР°Р·РјРµСЂРѕРІ С‚РµРєСЃС‚Р°: $expected."

    fun adaptiveTextStyleOutlier(differences: String): String =
        "РЎС‚РёР»СЊ С‚РµРєСЃС‚Р° РѕС‚Р»РёС‡Р°РµС‚СЃСЏ РѕС‚ РґРѕРјРёРЅРёСЂСѓСЋС‰РµРіРѕ predicted text role style РЅР° СЌС‚РѕРј СЌРєСЂР°РЅРµ РїРѕ РїСЂРёР·РЅР°РєР°Рј: $differences."

    fun adaptiveTextStyleOutlierRecommendation(dominantStyle: String): String =
        "РџСЂРѕРІРµСЂСЊС‚Рµ, РґРѕР»Р¶РµРЅ Р»Рё СЌР»РµРјРµРЅС‚ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ РґРѕРјРёРЅРёСЂСѓСЋС‰РёР№ СЃС‚РёР»СЊ РґР»СЏ СЃРІРѕРµР№ predicted text role: $dominantStyle."

    fun buttonColorPerLayoutNearDuplicate(
        color: String,
        canonicalColor: String,
        distance: String
    ): String =
        "Р’ СЌС‚РѕРј layout-С„Р°Р№Р»Рµ С†РІРµС‚ РєРЅРѕРїРєРё $color РѕС‡РµРЅСЊ Р±Р»РёР·РѕРє Рє $canonicalColor (distance=$distance), РєРѕС‚РѕСЂС‹Р№ С‡Р°С‰Рµ РІСЃС‚СЂРµС‡Р°РµС‚СЃСЏ СЃСЂРµРґРё РїРѕС…РѕР¶РёС… РѕС‚С‚РµРЅРєРѕРІ."

    fun buttonColorPerLayoutNearDuplicateRecommendation(canonicalColor: String): String =
        "РСЃРїРѕР»СЊР·СѓР№С‚Рµ $canonicalColor РєР°Рє РµРґРёРЅС‹Р№ РѕС‚С‚РµРЅРѕРє РґР»СЏ РІРёР·СѓР°Р»СЊРЅРѕ РѕРґРёРЅР°РєРѕРІС‹С… РєРЅРѕРїРѕРє РІ СЌС‚РѕРј СЌРєСЂР°РЅРµ."

    fun buttonColorPerLayoutNearDominant(
        color: String,
        dominantColor: String,
        distance: String
    ): String =
        "Р’ layout-С„Р°Р№Р»Рµ РёСЃРїРѕР»СЊР·СѓРµС‚СЃСЏ С†РІРµС‚ РєРЅРѕРїРєРё $color, РєРѕС‚РѕСЂС‹Р№ РїРѕС‡С‚Рё СЃРѕРІРїР°РґР°РµС‚ СЃ РѕСЃРЅРѕРІРЅС‹Рј С†РІРµС‚РѕРј РєРЅРѕРїРѕРє $dominantColor, РЅРѕ РѕС‚Р»РёС‡Р°РµС‚СЃСЏ (distance=$distance)."

    fun buttonColorPerLayoutDifferent(color: String, dominantColor: String): String =
        "Р’ layout-С„Р°Р№Р»Рµ РёСЃРїРѕР»СЊР·СѓРµС‚СЃСЏ С†РІРµС‚ РєРЅРѕРїРєРё $color, РєРѕС‚РѕСЂС‹Р№ РѕС‚Р»РёС‡Р°РµС‚СЃСЏ РѕС‚ РЅР°РёР±РѕР»РµРµ С‡Р°СЃС‚РѕРіРѕ С†РІРµС‚Р° РєРЅРѕРїРѕРє $dominantColor."

    const val BUTTON_COLOR_PER_LAYOUT_RECOMMENDATION =
        "РџСЂРёРІРµРґРёС‚Рµ С†РІРµС‚Р° РєРЅРѕРїРѕРє РІ СЌС‚РѕРј layout-С„Р°Р№Р»Рµ Рє РѕРґРЅРѕРјСѓ СЃРѕРіР»Р°СЃРѕРІР°РЅРЅРѕРјСѓ СЃС‚РёР»СЋ."

    fun buttonColorProjectNearDuplicate(
        color: String,
        canonicalColor: String,
        distance: String
    ): String =
        "Р¦РІРµС‚ РєРЅРѕРїРєРё $color РѕС‡РµРЅСЊ Р±Р»РёР·РѕРє Рє РѕС‚С‚РµРЅРєСѓ $canonicalColor (distance=$distance), РєРѕС‚РѕСЂС‹Р№ С‡Р°С‰Рµ РІСЃС‚СЂРµС‡Р°РµС‚СЃСЏ СЃСЂРµРґРё РїРѕС…РѕР¶РёС… С†РІРµС‚РѕРІ РєРЅРѕРїРѕРє РІ РїСЂРѕРµРєС‚Рµ."

    fun buttonColorProjectNearDuplicateRecommendation(canonicalColor: String): String =
        "РСЃРїРѕР»СЊР·СѓР№С‚Рµ $canonicalColor РєР°Рє РѕСЃРЅРѕРІРЅРѕР№ РѕС‚С‚РµРЅРѕРє РІ СЌС‚РѕР№ РіСЂСѓРїРїРµ Р±Р»РёР·РєРёС… С†РІРµС‚РѕРІ, С‡С‚РѕР±С‹ РёР·Р±РµР¶Р°С‚СЊ РїРѕС‡С‚Рё РЅРµР·Р°РјРµС‚РЅС‹С… СЂР°СЃС…РѕР¶РґРµРЅРёР№ РїРѕ РІСЃРµРјСѓ РїСЂРѕРµРєС‚Сѓ."

    fun buttonColorProjectNearDominant(
        color: String,
        dominantColor: String,
        distance: String
    ): String =
        "Р¦РІРµС‚ РєРЅРѕРїРєРё $color РїРѕС‡С‚Рё СЃРѕРІРїР°РґР°РµС‚ СЃ РЅР°РёР±РѕР»РµРµ СЂР°СЃРїСЂРѕСЃС‚СЂР°РЅРµРЅРЅС‹Рј С†РІРµС‚РѕРј РєРЅРѕРїРѕРє РІ РїСЂРѕРµРєС‚Рµ $dominantColor, РЅРѕ РѕС‚Р»РёС‡Р°РµС‚СЃСЏ (distance=$distance)."

    fun buttonColorProjectDifferent(color: String, dominantColor: String): String =
        "Р¦РІРµС‚ РєРЅРѕРїРєРё $color РѕС‚Р»РёС‡Р°РµС‚СЃСЏ РѕС‚ РЅР°РёР±РѕР»РµРµ СЂР°СЃРїСЂРѕСЃС‚СЂР°РЅРµРЅРЅРѕРіРѕ С†РІРµС‚Р° РєРЅРѕРїРѕРє РІ РїСЂРѕРµРєС‚Рµ $dominantColor."

    const val BUTTON_COLOR_PROJECT_RECOMMENDATION =
        "РџСЂРѕРІРµСЂСЊС‚Рµ, СЃРѕРѕС‚РІРµС‚СЃС‚РІСѓРµС‚ Р»Рё СЌС‚Р° РєРЅРѕРїРєР° РіР»РѕР±Р°Р»СЊРЅРѕРјСѓ С†РІРµС‚РѕРІРѕРјСѓ СЃС‚РёР»СЋ РїСЂРѕРµРєС‚Р°."

    fun hardcodedColor(propertyName: String, color: String): String =
        "РЎРІРѕР№СЃС‚РІРѕ $propertyName РёСЃРїРѕР»СЊР·СѓРµС‚ Р·Р°С…Р°СЂРґРєРѕР¶РµРЅРЅС‹Р№ С†РІРµС‚ $color."

    const val HARDCODED_COLOR_RECOMMENDATION =
        "Р’С‹РЅРµСЃРёС‚Рµ С†РІРµС‚ РІ СЂРµСЃСѓСЂСЃ `colors.xml` Рё РёСЃРїРѕР»СЊР·СѓР№С‚Рµ СЃСЃС‹Р»РєСѓ РІРёРґР° `@color/...`."

    fun nearDuplicateButtonColors(firstColor: String, secondColor: String, distance: Double): String =
        "Р¦РІРµС‚ РєРЅРѕРїРєРё $firstColor РїРѕС‡С‚Рё СЃРѕРІРїР°РґР°РµС‚ СЃ С†РІРµС‚РѕРј $secondColor Сѓ РґСЂСѓРіРѕР№ РєРЅРѕРїРєРё (distance=%.2f).".format(distance)

    const val NEAR_DUPLICATE_BUTTON_COLORS_RECOMMENDATION =
        "РџСЂРѕРІРµСЂСЊС‚Рµ, РЅРµ СЏРІР»СЏРµС‚СЃСЏ Р»Рё СЌС‚Рѕ СЃР»СѓС‡Р°Р№РЅС‹Рј РѕС‚РєР»РѕРЅРµРЅРёРµРј РѕС‚ РѕР±С‰РµРіРѕ С†РІРµС‚РѕРІРѕРіРѕ СЃС‚РёР»СЏ."

    fun hardcodedDimension(propertyName: String, value: String?): String =
        "РЎРІРѕР№СЃС‚РІРѕ $propertyName РёСЃРїРѕР»СЊР·СѓРµС‚ Р·Р°С…Р°СЂРґРєРѕР¶РµРЅРЅС‹Р№ СЂР°Р·РјРµСЂ $value."

    const val HARDCODED_DIMENSION_RECOMMENDATION =
        "Р’С‹РЅРµСЃРёС‚Рµ СЂР°Р·РјРµСЂ РІ СЂРµСЃСѓСЂСЃ `dimens.xml` РёР»Рё РёСЃРїРѕР»СЊР·СѓР№С‚Рµ РѕР±С‰РёР№ СЃС‚РёР»СЊ."

    fun missingId(componentType: String): String =
        "РљРѕРјРїРѕРЅРµРЅС‚ $componentType РЅРµ СЃРѕРґРµСЂР¶РёС‚ android:id."

    const val MISSING_ID_RECOMMENDATION =
        "Р”РѕР±Р°РІСЊС‚Рµ РёРґРµРЅС‚РёС„РёРєР°С‚РѕСЂ, РµСЃР»Рё СЌР»РµРјРµРЅС‚ РёСЃРїРѕР»СЊР·СѓРµС‚СЃСЏ РІ РєРѕРґРµ, С‚РµСЃС‚Р°С… РёР»Рё РґРѕР»Р¶РµРЅ Р±С‹С‚СЊ РѕРґРЅРѕР·РЅР°С‡РЅРѕ СЂР°Р·Р»РёС‡РёРј."

    fun imageWithoutContentDescription(componentType: String): String =
        "РР·РѕР±СЂР°Р¶РµРЅРёРµ $componentType РЅРµ СЃРѕРґРµСЂР¶РёС‚ android:contentDescription."

    const val IMAGE_WITHOUT_CONTENT_DESCRIPTION_RECOMMENDATION =
        "Р”РѕР±Р°РІСЊС‚Рµ contentDescription РґР»СЏ СѓР»СѓС‡С€РµРЅРёСЏ РґРѕСЃС‚СѓРїРЅРѕСЃС‚Рё. Р•СЃР»Рё РёР·РѕР±СЂР°Р¶РµРЅРёРµ РґРµРєРѕСЂР°С‚РёРІРЅРѕРµ, СЏРІРЅРѕ СѓРєР°Р¶РёС‚Рµ @null."

    fun composeImageContentDescription(componentType: String, isInteractive: Boolean): String {
        val context = if (isInteractive) {
            "РІ РёРЅС‚РµСЂР°РєС‚РёРІРЅРѕРј Compose-РєРѕРЅС‚РµР№РЅРµСЂРµ"
        } else {
            "РІ Compose"
        }
        return "$componentType $context РЅРµ СЃРѕРґРµСЂР¶РёС‚ РѕСЃРјС‹СЃР»РµРЅРЅС‹Р№ contentDescription."
    }

    const val COMPOSE_IMAGE_CONTENT_DESCRIPTION_RECOMMENDATION =
        "Р”РѕР±Р°РІСЊС‚Рµ contentDescription РґР»СЏ СЃРјС‹СЃР»РѕРІРѕРіРѕ РёР·РѕР±СЂР°Р¶РµРЅРёСЏ РёР»Рё РёРєРѕРЅРєРё. Р”Р»СЏ РґРµРєРѕСЂР°С‚РёРІРЅРѕРіРѕ СЌР»РµРјРµРЅС‚Р° СЏРІРЅРѕ РѕСЃС‚Р°РІСЊС‚Рµ null; РґР»СЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЊСЃРєРѕРіРѕ С‚РµРєСЃС‚Р° РїСЂРµРґРїРѕС‡С‚РёС‚РµР»СЊРЅРѕ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ stringResource(R.string...)."

    fun composeHardcodedText(text: String): String =
        "Compose Text РёСЃРїРѕР»СЊР·СѓРµС‚ СЃС‚СЂРѕРєРѕРІС‹Р№ Р»РёС‚РµСЂР°Р»: \"$text\"."

    const val COMPOSE_HARDCODED_TEXT_RECOMMENDATION =
        "Р•СЃР»Рё СЌС‚Рѕ РїРѕР»СЊР·РѕРІР°С‚РµР»СЊСЃРєРёР№ С‚РµРєСЃС‚, РІС‹РЅРµСЃРёС‚Рµ РµРіРѕ РІ СЃС‚СЂРѕРєРѕРІС‹Рµ СЂРµСЃСѓСЂСЃС‹ Рё РёСЃРїРѕР»СЊР·СѓР№С‚Рµ stringResource(R.string...). Р”РёРЅР°РјРёС‡РµСЃРєРёРµ Р·РЅР°С‡РµРЅРёСЏ Рё С‚РµС…РЅРёС‡РµСЃРєРёРµ РїРѕРґРїРёСЃРё РІС‹РЅРѕСЃРёС‚СЊ РЅРµ С‚СЂРµР±СѓРµС‚СЃСЏ."

    fun composeHardcodedColor(propertyName: String, value: String): String =
        "Compose-СЃРІРѕР№СЃС‚РІРѕ $propertyName РёСЃРїРѕР»СЊР·СѓРµС‚ inline-С†РІРµС‚ $value."

    const val COMPOSE_HARDCODED_COLOR_RECOMMENDATION =
        "РСЃРїРѕР»СЊР·СѓР№С‚Рµ MaterialTheme.colorScheme, Compose theme token РёР»Рё colorResource(R.color...) РІРјРµСЃС‚Рѕ inline-С†РІРµС‚Р°, РµСЃР»Рё С†РІРµС‚ РѕС‚РЅРѕСЃРёС‚СЃСЏ Рє UI-СЃС‚РёР»СЋ."

    fun composeMissingModifierParameter(functionName: String, predictedRole: String): String =
        "Composable-С„СѓРЅРєС†РёСЏ $functionName РЅРµ РїСЂРёРЅРёРјР°РµС‚ modifier-РїР°СЂР°РјРµС‚СЂ. predictedRole=$predictedRole."

    const val COMPOSE_MISSING_MODIFIER_PARAMETER_RECOMMENDATION =
        "Р•СЃР»Рё С„СѓРЅРєС†РёСЏ СЏРІР»СЏРµС‚СЃСЏ РїРµСЂРµРёСЃРїРѕР»СЊР·СѓРµРјС‹Рј UI-РєРѕРјРїРѕРЅРµРЅС‚РѕРј, РґРѕР±Р°РІСЊС‚Рµ РїР°СЂР°РјРµС‚СЂ modifier: Modifier = Modifier Рё РїСЂРёРјРµРЅРёС‚Рµ РµРіРѕ Рє РєРѕСЂРЅРµРІРѕРјСѓ СЌР»РµРјРµРЅС‚Сѓ."

    fun composeAdaptiveSpacingOutlier(propertyName: String, actualValue: Float): String =
        "Compose-Р·РЅР°С‡РµРЅРёРµ $propertyName = ${actualValue}dp РІС‹Р±РёРІР°РµС‚СЃСЏ РёР· Р»РѕРєР°Р»СЊРЅРѕР№ С€РєР°Р»С‹ spacing-Р·РЅР°С‡РµРЅРёР№ СЌС‚РѕРіРѕ С„Р°Р№Р»Р°."

    fun composeAdaptiveSpacingOutlierRecommendation(expected: String): String =
        "РџСЂРѕРІРµСЂСЊС‚Рµ, РґРѕР»Р¶РµРЅ Р»Рё Compose-СЌР»РµРјРµРЅС‚ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ РѕРґРЅРѕ РёР· С‚РёРїРёС‡РЅС‹С… spacing-Р·РЅР°С‡РµРЅРёР№: $expected."

    fun composeTooManyTextStylesOnScreen(
        actualCount: Int,
        dominantSharePercent: Int
    ): String =
        "Р’ Compose-С„Р°Р№Р»Рµ РёСЃРїРѕР»СЊР·СѓРµС‚СЃСЏ СЃР»РёС€РєРѕРј РјРЅРѕРіРѕ РєРѕРЅРєСѓСЂРёСЂСѓСЋС‰РёС… С‚РµРєСЃС‚РѕРІС‹С… СЃС‚РёР»РµР№: $actualCount. Р”РѕРјРёРЅРёСЂСѓСЋС‰РёР№ СЃС‚РёР»СЊ РїРѕРєСЂС‹РІР°РµС‚ С‚РѕР»СЊРєРѕ $dominantSharePercent% Text-РєРѕРјРїРѕРЅРµРЅС‚РѕРІ."

    const val COMPOSE_TOO_MANY_TEXT_STYLES_ON_SCREEN_RECOMMENDATION =
        "РџСЂРѕРІРµСЂСЊС‚Рµ, РјРѕР¶РЅРѕ Р»Рё РїСЂРёРІРµСЃС‚Рё Text-РєРѕРјРїРѕРЅРµРЅС‚С‹ Рє РѕР±С‰РµР№ С‚РёРїРѕРіСЂР°С„РёС‡РµСЃРєРѕР№ СЃРёСЃС‚РµРјРµ: MaterialTheme.typography, РѕР±С‰РёРј style-РїР°СЂР°РјРµС‚СЂР°Рј РёР»Рё РїРµСЂРµРёСЃРїРѕР»СЊР·СѓРµРјС‹Рј С‚РµРєСЃС‚РѕРІС‹Рј РєРѕРјРїРѕРЅРµРЅС‚Р°Рј."

    fun composeAdaptiveTextStyleOutlier(differences: String, predictedRole: String): String =
        "Compose Text РѕС‚Р»РёС‡Р°РµС‚СЃСЏ РѕС‚ РґРѕРјРёРЅРёСЂСѓСЋС‰РµРіРѕ СЃС‚РёР»СЏ РІРЅСѓС‚СЂРё predicted text role $predictedRole РїРѕ РїСЂРёР·РЅР°РєР°Рј: $differences."

    fun composeAdaptiveTextStyleOutlierRecommendation(dominantStyle: String): String =
        "РџСЂРѕРІРµСЂСЊС‚Рµ, РґРѕР»Р¶РµРЅ Р»Рё Text РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ РґРѕРјРёРЅРёСЂСѓСЋС‰РёР№ Compose-СЃС‚РёР»СЊ: $dominantStyle."

    fun composeInsufficientTextContrast(
        textColor: String,
        backgroundColor: String,
        ratio: String,
        minContrast: Double
    ): String =
        "РќРµРґРѕСЃС‚Р°С‚РѕС‡РЅС‹Р№ РєРѕРЅС‚СЂР°СЃС‚ Compose Text ($textColor) Рё Р±Р»РёР¶Р°Р№С€РµРіРѕ С„РѕРЅР° ($backgroundColor): ratio=$ratio (С‚СЂРµР±СѓРµС‚СЃСЏ >= $minContrast)."

    const val COMPOSE_INSUFFICIENT_TEXT_CONTRAST_RECOMMENDATION =
        "РЈРІРµР»РёС‡СЊС‚Рµ РєРѕРЅС‚СЂР°СЃС‚ РјРµР¶РґСѓ Compose Text Рё С„РѕРЅРѕРј РёР»Рё РёСЃРїРѕР»СЊР·СѓР№С‚Рµ РїРѕРґС…РѕРґСЏС‰РёРµ MaterialTheme.colorScheme-С‚РѕРєРµРЅС‹."

    fun composeButtonColorPerFileNearDuplicate(
        color: String,
        canonicalColor: String,
        distance: String
    ): String =
        "Р’ Compose-С„Р°Р№Р»Рµ С†РІРµС‚ РєРЅРѕРїРєРё $color РѕС‡РµРЅСЊ Р±Р»РёР·РѕРє Рє $canonicalColor (distance=$distance), РєРѕС‚РѕСЂС‹Р№ С‡Р°С‰Рµ РІСЃС‚СЂРµС‡Р°РµС‚СЃСЏ СЃСЂРµРґРё РїРѕС…РѕР¶РёС… С†РІРµС‚РѕРІ РєРЅРѕРїРѕРє."

    fun composeButtonColorPerFileNearDuplicateRecommendation(canonicalColor: String): String =
        "РСЃРїРѕР»СЊР·СѓР№С‚Рµ $canonicalColor РєР°Рє РµРґРёРЅС‹Р№ РѕС‚С‚РµРЅРѕРє РґР»СЏ РІРёР·СѓР°Р»СЊРЅРѕ РѕРґРёРЅР°РєРѕРІС‹С… Compose-РєРЅРѕРїРѕРє РІ СЌС‚РѕРј С„Р°Р№Р»Рµ."

    fun composeButtonColorPerFileNearDominant(
        color: String,
        dominantColor: String,
        distance: String
    ): String =
        "Р’ Compose-С„Р°Р№Р»Рµ РёСЃРїРѕР»СЊР·СѓРµС‚СЃСЏ С†РІРµС‚ РєРЅРѕРїРєРё $color, РєРѕС‚РѕСЂС‹Р№ РїРѕС‡С‚Рё СЃРѕРІРїР°РґР°РµС‚ СЃ РѕСЃРЅРѕРІРЅС‹Рј С†РІРµС‚РѕРј РєРЅРѕРїРѕРє $dominantColor, РЅРѕ РѕС‚Р»РёС‡Р°РµС‚СЃСЏ (distance=$distance)."

    fun composeButtonColorPerFileDifferent(color: String, dominantColor: String): String =
        "Р’ Compose-С„Р°Р№Р»Рµ РёСЃРїРѕР»СЊР·СѓРµС‚СЃСЏ С†РІРµС‚ РєРЅРѕРїРєРё $color, РєРѕС‚РѕСЂС‹Р№ РѕС‚Р»РёС‡Р°РµС‚СЃСЏ РѕС‚ РЅР°РёР±РѕР»РµРµ С‡Р°СЃС‚РѕРіРѕ С†РІРµС‚Р° РєРЅРѕРїРѕРє $dominantColor."

    const val COMPOSE_BUTTON_COLOR_PER_FILE_RECOMMENDATION =
        "РџСЂРѕРІРµСЂСЊС‚Рµ, СЃРѕРѕС‚РІРµС‚СЃС‚РІСѓРµС‚ Р»Рё СЌС‚Р° Compose-РєРЅРѕРїРєР° Р»РѕРєР°Р»СЊРЅРѕР№ С†РІРµС‚РѕРІРѕР№ СЃРёСЃС‚РµРјРµ С„Р°Р№Р»Р°: MaterialTheme.colorScheme РёР»Рё РѕР±С‰РµРјСѓ buttonColors-С‚РѕРєРµРЅСѓ."

    fun composeComponentStyleOutlier(componentType: String, differences: String): String =
        "Compose-РєРѕРјРїРѕРЅРµРЅС‚ $componentType РѕС‚Р»РёС‡Р°РµС‚СЃСЏ РѕС‚ РґРѕРјРёРЅРёСЂСѓСЋС‰РµРіРѕ Р»РѕРєР°Р»СЊРЅРѕРіРѕ СЃС‚РёР»СЏ РїРѕС…РѕР¶РёС… РєРѕРјРїРѕРЅРµРЅС‚РѕРІ РїРѕ РїСЂРёР·РЅР°РєР°Рј: $differences."

    fun composeComponentStyleOutlierRecommendation(dominantStyle: String): String =
        "РџСЂРѕРІРµСЂСЊС‚Рµ, РґРѕР»Р¶РµРЅ Р»Рё РєРѕРјРїРѕРЅРµРЅС‚ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ РґРѕРјРёРЅРёСЂСѓСЋС‰РёР№ Compose-СЃС‚РёР»СЊ РґР»СЏ РїРѕС…РѕР¶РёС… РєРѕРјРїРѕРЅРµРЅС‚РѕРІ РІ СЌС‚РѕРј С„Р°Р№Р»Рµ: $dominantStyle."

    fun composeTouchTargetTooSmall(width: String?, height: String?): String =
        "Р Р°Р·РјРµСЂ РёРЅС‚РµСЂР°РєС‚РёРІРЅРѕРіРѕ Compose-СЌР»РµРјРµРЅС‚Р° СЃР»РёС€РєРѕРј РјР°Р» РґР»СЏ СѓРґРѕР±РЅРѕРіРѕ РЅР°Р¶Р°С‚РёСЏ: width=$width, height=$height."

    const val COMPOSE_TOUCH_TARGET_TOO_SMALL_RECOMMENDATION =
        "Р РµРєРѕРјРµРЅРґСѓРµС‚СЃСЏ РґРµР»Р°С‚СЊ РёРЅС‚РµСЂР°РєС‚РёРІРЅС‹Рµ Compose-СЌР»РµРјРµРЅС‚С‹ РЅРµ РјРµРЅСЊС€Рµ 48.dp РїРѕ С€РёСЂРёРЅРµ Рё РІС‹СЃРѕС‚Рµ РёР»Рё РѕР±РµСЃРїРµС‡РёС‚СЊ РјРёРЅРёРјР°Р»СЊРЅСѓСЋ РѕР±Р»Р°СЃС‚СЊ РЅР°Р¶Р°С‚РёСЏ С‡РµСЂРµР· Modifier.sizeIn/minimumInteractiveComponentSize."

    fun composeRuntimeOverlappingClickableComponents(
        firstComponent: String,
        secondComponent: String,
        overlapArea: String
    ): String =
        "Runtime Compose clickable-РєРѕРјРїРѕРЅРµРЅС‚С‹ РїРµСЂРµСЃРµРєР°СЋС‚СЃСЏ: $firstComponent Рё $secondComponent, overlapArea=$overlapArea."

    const val COMPOSE_RUNTIME_OVERLAPPING_CLICKABLE_COMPONENTS_RECOMMENDATION =
        "РџСЂРѕРІРµСЂСЊС‚Рµ С„Р°РєС‚РёС‡РµСЃРєРёРµ runtime bounds: РїРµСЂРµСЃРµРєР°СЋС‰РёРµСЃСЏ clickable-РѕР±Р»Р°СЃС‚Рё РјРѕРіСѓС‚ РІС‹Р·С‹РІР°С‚СЊ РЅРµРѕРґРЅРѕР·РЅР°С‡РЅС‹Рµ РЅР°Р¶Р°С‚РёСЏ Рё РїСЂРѕР±Р»РµРјС‹ РґРѕСЃС‚СѓРїРЅРѕСЃС‚Рё."

    fun composeRuntimeOffscreenOrClippedComponent(
        component: String,
        bounds: String,
        screenBounds: String,
        reason: String
    ): String =
        "Runtime-РєРѕРјРїРѕРЅРµРЅС‚ $component РёРјРµРµС‚ РїРѕРґРѕР·СЂРёС‚РµР»СЊРЅС‹Рµ bounds: bounds=$bounds, screenBounds=$screenBounds, reason=$reason."

    const val COMPOSE_RUNTIME_OFFSCREEN_OR_CLIPPED_COMPONENT_RECOMMENDATION =
        "РџСЂРѕРІРµСЂСЊС‚Рµ С„Р°РєС‚РёС‡РµСЃРєСѓСЋ runtime-РІРµСЂСЃС‚РєСѓ: РєРѕРјРїРѕРЅРµРЅС‚ РјРѕР¶РµС‚ Р±С‹С‚СЊ РЅРµРІРёРґРёРјС‹Рј, РѕР±СЂРµР·Р°РЅРЅС‹Рј, РІС‹РЅРµСЃРµРЅРЅС‹Рј Р·Р° СЌРєСЂР°РЅ РёР»Рё РёР·РјРµСЂРµРЅРЅС‹Рј СЃ РЅРµРєРѕСЂСЂРµРєС‚РЅС‹Рј СЂР°Р·РјРµСЂРѕРј."

    fun runtimeSystemAppSnapshotWarning(
        expectedPackage: String,
        actualPackage: String
    ): String =
        "Runtime snapshot СЃРЅСЏС‚ СЃ РїР°РєРµС‚Р° $actualPackage, С…РѕС‚СЏ РѕР¶РёРґР°РµС‚СЃСЏ РїР°РєРµС‚ РїСЂРѕРµРєС‚Р° $expectedPackage."

    const val RUNTIME_SYSTEM_APP_SNAPSHOT_WARNING_RECOMMENDATION =
        "РџРµСЂРµРґ runtime-Р°РЅР°Р»РёР·РѕРј РѕС‚РєСЂРѕР№С‚Рµ РїСЂРёР»РѕР¶РµРЅРёРµ РїСЂРѕРµРєС‚Р° РЅР° СЌРјСѓР»СЏС‚РѕСЂРµ/СѓСЃС‚СЂРѕР№СЃС‚РІРµ Рё РїРѕРІС‚РѕСЂРёС‚Рµ snapshot. РќР°РїСЂРёРјРµСЂ: adb shell monkey -p <applicationId> 1."

    fun runtimeDuplicateVisibleTextActions(
        label: String,
        count: Int,
        examples: String
    ): String =
        "РќР° runtime-СЌРєСЂР°РЅРµ РЅР°Р№РґРµРЅРѕ $count РєР»РёРєР°Р±РµР»СЊРЅС‹С… СЌР»РµРјРµРЅС‚РѕРІ СЃ РѕРґРёРЅР°РєРѕРІРѕР№ РІРёРґРёРјРѕР№ РїРѕРґРїРёСЃСЊСЋ \"$label\": $examples."

    const val RUNTIME_DUPLICATE_VISIBLE_TEXT_ACTIONS_RECOMMENDATION =
        "РџСЂРѕРІРµСЂСЊС‚Рµ, РґРѕСЃС‚Р°С‚РѕС‡РЅРѕ Р»Рё СЂР°Р·Р»РёС‡РёРјС‹ СЌС‚Рё РґРµР№СЃС‚РІРёСЏ РґР»СЏ РїРѕР»СЊР·РѕРІР°С‚РµР»СЏ, accessibility Рё UI-С‚РµСЃС‚РѕРІ. Р•СЃР»Рё СЌР»РµРјРµРЅС‚С‹ РїРѕРІС‚РѕСЂСЏСЋС‚СЃСЏ РІ СЃРїРёСЃРєРµ, РґРѕР±Р°РІСЊС‚Рµ Р±РѕР»РµРµ РєРѕРЅРєСЂРµС‚РЅС‹Р№ contentDescription РёР»Рё РґСЂСѓРіРѕР№ РґРѕСЃС‚СѓРїРЅС‹Р№ РєРѕРЅС‚РµРєСЃС‚."

    fun composeNearDuplicateSpacingCluster(
        propertyName: String,
        value: Float,
        canonicalValue: Float
    ): String =
        "Compose spacing-Р·РЅР°С‡РµРЅРёРµ $propertyName=${value}dp РѕС‡РµРЅСЊ Р±Р»РёР·РєРѕ Рє ${canonicalValue}dp, РєРѕС‚РѕСЂС‹Р№ С‡Р°С‰Рµ РёСЃРїРѕР»СЊР·СѓРµС‚СЃСЏ РІ СЌС‚РѕРј С„Р°Р№Р»Рµ."

    fun composeNearDuplicateSpacingClusterRecommendation(canonicalValue: Float): String =
        "РџСЂРѕРІРµСЂСЊС‚Рµ, РјРѕР¶РЅРѕ Р»Рё Р·Р°РјРµРЅРёС‚СЊ Р·РЅР°С‡РµРЅРёРµ РЅР° ${canonicalValue}dp, С‡С‚РѕР±С‹ СѓРјРµРЅСЊС€РёС‚СЊ РґСЂРѕР±Р»РµРЅРёРµ spacing-С€РєР°Р»С‹."

    fun composeTextSizeNearDuplicateCluster(
        value: Float,
        canonicalValue: Float,
        predictedRole: String
    ): String =
        "Compose textSize=${value}sp РѕС‡РµРЅСЊ Р±Р»РёР·РѕРє Рє ${canonicalValue}sp РІРЅСѓС‚СЂРё predicted text role $predictedRole."

    fun composeTextSizeNearDuplicateClusterRecommendation(
        canonicalValue: Float,
        predictedRole: String
    ): String =
        "РџСЂРѕРІРµСЂСЊС‚Рµ, РјРѕР¶РЅРѕ Р»Рё РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ ${canonicalValue}sp РґР»СЏ predictedRole=$predictedRole, С‡С‚РѕР±С‹ СѓРјРµРЅСЊС€РёС‚СЊ РґСЂРѕР±Р»РµРЅРёРµ С‚РёРїРѕРіСЂР°С„РёС‡РµСЃРєРѕР№ С€РєР°Р»С‹."

    fun hardcodedText(componentType: String, text: String): String =
        "РљРѕРјРїРѕРЅРµРЅС‚ $componentType РёСЃРїРѕР»СЊР·СѓРµС‚ Р·Р°С…Р°СЂРґРєРѕР¶РµРЅРЅС‹Р№ С‚РµРєСЃС‚: \"$text\"."

    const val HARDCODED_TEXT_RECOMMENDATION =
        "Р’С‹РЅРµСЃРёС‚Рµ С‚РµРєСЃС‚ РІ `strings.xml` Рё РёСЃРїРѕР»СЊР·СѓР№С‚Рµ СЃСЃС‹Р»РєСѓ РІРёРґР° `@string/...`."

    fun suspiciousTextSizeTooSmall(rawTextSize: String): String =
        "РџРѕРґРѕР·СЂРёС‚РµР»СЊРЅРѕ РјР°Р»РµРЅСЊРєРёР№ СЂР°Р·РјРµСЂ С‚РµРєСЃС‚Р°: $rawTextSize."

    const val SUSPICIOUS_TEXT_SIZE_TOO_SMALL_RECOMMENDATION =
        "РџСЂРѕРІРµСЂСЊС‚Рµ Р·РЅР°С‡РµРЅРёРµ android:textSize: РІРѕР·РјРѕР¶РЅРѕ, Р·РґРµСЃСЊ РѕС€РёР±РєР° РІ СЂР°Р·РјРµС‚РєРµ."

    fun suspiciousTextSizeTooLarge(rawTextSize: String): String =
        "РџРѕРґРѕР·СЂРёС‚РµР»СЊРЅРѕ Р±РѕР»СЊС€РѕР№ СЂР°Р·РјРµСЂ С‚РµРєСЃС‚Р°: $rawTextSize."

    const val SUSPICIOUS_TEXT_SIZE_TOO_LARGE_RECOMMENDATION =
        "РџСЂРѕРІРµСЂСЊС‚Рµ, СЃРѕРѕС‚РІРµС‚СЃС‚РІСѓРµС‚ Р»Рё С‚Р°РєРѕР№ СЂР°Р·РјРµСЂ С‚РµРєСЃС‚Р° РЅР°Р·РЅР°С‡РµРЅРёСЋ СЌР»РµРјРµРЅС‚Р°."

    fun insufficientTextContrast(
        textColor: String,
        backgroundColor: String,
        ratio: String,
        minContrast: Double
    ): String =
        "РќРµРґРѕСЃС‚Р°С‚РѕС‡РЅС‹Р№ РєРѕРЅС‚СЂР°СЃС‚ С‚РµРєСЃС‚Р° ($textColor) Рё С„РѕРЅР° ($backgroundColor): ratio=$ratio (С‚СЂРµР±СѓРµС‚СЃСЏ >= $minContrast)."

    const val INSUFFICIENT_TEXT_CONTRAST_RECOMMENDATION =
        "РЈРІРµР»РёС‡СЊС‚Рµ РєРѕРЅС‚СЂР°СЃС‚ РјРµР¶РґСѓ С‚РµРєСЃС‚РѕРј Рё С„РѕРЅРѕРј РґР»СЏ Р»СѓС‡С€РµР№ С‡РёС‚Р°РµРјРѕСЃС‚Рё."

    fun textSizeConsistency(size: String, dominantSize: String?): String =
        "Р Р°Р·РјРµСЂ С‚РµРєСЃС‚Р° $size РѕС‚Р»РёС‡Р°РµС‚СЃСЏ РѕС‚ РЅР°РёР±РѕР»РµРµ С‡Р°СЃС‚Рѕ РёСЃРїРѕР»СЊР·СѓРµРјРѕРіРѕ СЂР°Р·РјРµСЂР° $dominantSize."

    fun deepLayoutNesting(depth: Int): String =
        "Р“Р»СѓР±РёРЅР° РІР»РѕР¶РµРЅРЅРѕСЃС‚Рё layout-РґРµСЂРµРІР° СЃР»РёС€РєРѕРј Р±РѕР»СЊС€Р°СЏ: $depth СѓСЂРѕРІРЅРµР№."

    const val DEEP_LAYOUT_NESTING_RECOMMENDATION =
        "РџСЂРѕРІРµСЂСЊС‚Рµ СЃС‚СЂСѓРєС‚СѓСЂСѓ СЂР°Р·РјРµС‚РєРё: РіР»СѓР±РѕРєСѓСЋ РІР»РѕР¶РµРЅРЅРѕСЃС‚СЊ СЃС‚РѕРёС‚ СѓРїСЂРѕСЃС‚РёС‚СЊ С‡РµСЂРµР· ConstraintLayout, include/merge РёР»Рё Р±РѕР»РµРµ РїР»РѕСЃРєСѓСЋ РєРѕРјРїРѕР·РёС†РёСЋ."

    const val TEXT_SIZE_CONSISTENCY_RECOMMENDATION =
        "РџСЂРѕРІРµСЂСЊС‚Рµ, РґРѕР»Р¶РµРЅ Р»Рё СЌС‚РѕС‚ С‚РµРєСЃС‚ РёСЃРїРѕР»СЊР·РѕРІР°С‚СЊ РѕР±С‰РёР№ С‚РёРїРѕРіСЂР°С„РёС‡РµСЃРєРёР№ СЃС‚РёР»СЊ."
}