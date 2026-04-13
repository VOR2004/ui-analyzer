package ru.itis.analyzer.config

object ResourcePatterns {
    const val COLOR_REF_PREFIX = "@color/"
    const val ANDROID_COLOR_REF_PREFIX = "@android:color/"
    const val ATTR_REF_PREFIX = "?attr/"
    const val STRING_REF_PREFIX = "@string/"
    const val ANDROID_STRING_REF_PREFIX = "@android:string/"
    const val NULL_REF = "@null"

    const val COLOR_TAG = "color"
    const val NAME_ATTRIBUTE = "name"
    const val ANDROID_NEW_ID_PREFIX = "@+id/"
    const val ANDROID_ID_PREFIX = "@id/"
    const val SP_UNIT = "sp"
    const val DP_UNIT = "dp"
    const val HEX_RGB_PATTERN = "^#[0-9A-F]{6}$"
    const val HEX_ARGB_PATTERN = "^#[0-9A-F]{8}$"
}
