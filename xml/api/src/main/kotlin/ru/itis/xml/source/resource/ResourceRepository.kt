package ru.itis.xml.source.resource

interface ResourceRepository {

    fun resolveColor(value: String?): String?

    fun resolveDimension(value: String?): String?

    fun resolveString(value: String?): String?

    fun resolveStyleItem(styleName: String, itemName: String): String?

    fun resolveThemeAttribute(attributeReference: String?): String?

    companion object {
        fun empty(): ResourceRepository {
            return EmptyResourceRepository
        }
    }
}

private object EmptyResourceRepository : ResourceRepository {

    override fun resolveColor(value: String?): String? = null

    override fun resolveDimension(value: String?): String? = null

    override fun resolveString(value: String?): String? {
        if (value == null) return null
        return value.takeUnless { it.startsWith("@string/") || it.startsWith("@android:string/") }
    }

    override fun resolveStyleItem(styleName: String, itemName: String): String? = null

    override fun resolveThemeAttribute(attributeReference: String?): String? = null
}
