package ru.itis.analyzer.config.components

object ResourcePatterns {
    const val COLOR_REF_PREFIX = "@color/"
    const val DIMEN_REF_PREFIX = "@dimen/"
    const val STYLE_REF_PREFIX = "@style/"
    const val ANDROID_COLOR_REF_PREFIX = "@android:color/"
    const val ATTR_REF_PREFIX = "?attr/"
    const val ANDROID_ATTR_REF_PREFIX = "?android:attr/"
    const val STRING_REF_PREFIX = "@string/"
    const val ANDROID_STRING_REF_PREFIX = "@android:string/"
    const val NULL_REF = "@null"

    const val COLOR_TAG = "color"
    const val DIMEN_TAG = "dimen"
    const val STRING_TAG = "string"
    const val STYLE_TAG = "style"
    const val ITEM_TAG = "item"
    const val NAME_ATTRIBUTE = "name"
    const val PARENT_ATTRIBUTE = "parent"
    const val ANDROID_NEW_ID_PREFIX = "@+id/"
    const val ANDROID_ID_PREFIX = "@id/"
    const val SP_UNIT = "sp"
    const val DP_UNIT = "dp"
    const val HEX_RGB_PATTERN = "^#[0-9A-F]{6}$"
    const val HEX_ARGB_PATTERN = "^#[0-9A-F]{8}$"
}