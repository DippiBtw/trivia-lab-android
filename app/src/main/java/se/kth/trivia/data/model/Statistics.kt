package se.kth.trivia.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Statistics(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val avgAnswerTime: Float,
    val avgAccuracy: Float,
    val timestamp: Long
)
