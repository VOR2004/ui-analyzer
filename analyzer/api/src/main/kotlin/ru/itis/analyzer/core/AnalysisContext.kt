package ru.itis.analyzer.core

import ru.itis.xml.source.resource.ResourceRepository
import ru.itis.model.UiComponent
import ru.itis.xml.style.profile.ProjectStyleProfile
import ru.itis.xml.style.profile.ScreenStyleProfile

data class AnalysisContext(
    val components: List<UiComponent>,
    val resourceRepository: ResourceRepository,
    val projectStyleProfile: ProjectStyleProfile,
    val screenProfiles: Map<String, ScreenStyleProfile>
)
