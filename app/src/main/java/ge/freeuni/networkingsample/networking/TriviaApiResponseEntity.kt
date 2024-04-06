package ge.freeuni.networkingsample.networking

import kotlinx.serialization.Serializable

@Serializable
data class TriviaApiResponseEntity(
    val responseCode: Int,
    val results : List<TriviaQuestionEntity>,
)

@Serializable
data class TriviaQuestionEntity(
    val type: String,
    val difficulty: String,
    val category: String,
    val question: String,
    val correctAnswer: String,
)