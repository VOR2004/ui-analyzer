package ru.itis.compose.style.role

import ru.itis.compose.style.signature.ComposePredictedTextRole
import ru.itis.model.UiComponent

interface ComposeTextRolePredictor {

    fun predict(component: UiComponent): ComposePredictedTextRole
}