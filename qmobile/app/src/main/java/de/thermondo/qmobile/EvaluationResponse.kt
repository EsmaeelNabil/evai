package de.thermondo.qmobile

import kotlinx.serialization.Serializable

@Serializable
data class EvaluationResponse(
    val response: String = "",
    val score: Int = 0
)
