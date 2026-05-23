package ru.itis.compose.source.role

import ru.itis.compose.source.model.ComposeFunction
import ru.itis.compose.source.model.ComposeFunctionRole

interface ComposeFunctionRolePredictor {

    fun predict(function: ComposeFunction): ComposeFunctionRole
}