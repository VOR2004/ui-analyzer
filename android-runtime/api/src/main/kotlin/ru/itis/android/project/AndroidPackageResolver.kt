package ru.itis.android.project

import java.io.File

interface AndroidPackageResolver {
    fun resolve(projectRoot: File): String?
}
