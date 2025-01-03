package se.kth.trivia.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import se.kth.trivia.data.db.Converters

@Entity
@TypeConverters(Converters::class)
data class Trivia (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val response_code: Int,
    val results: List<TriviaQuestion>,
    var score: Int,
    var timestamp: Long,
)

data class TriviaQuestion (
    val type: String,
    val difficulty: String,
    var category: String,
    var question: String,
    var correct_answer: String,
    val incorrect_answers: List<String>,
    var correct: Boolean,
)