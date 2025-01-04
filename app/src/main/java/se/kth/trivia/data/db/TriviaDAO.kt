package se.kth.trivia.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import se.kth.trivia.data.model.Trivia

@Dao
interface TriviaDAO {

    // Insert a single Trivia object
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrivia(trivia: Trivia)

    // Insert a list of Trivia objects
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTrivia(triviaList: List<Trivia>)

    // Retrieve all Trivia records
    @Query("SELECT * FROM Trivia")
    suspend fun getAllTrivia(): List<Trivia>

    // Retrieve a single Trivia by its ID
    @Query("SELECT * FROM Trivia WHERE response_code = :id")
    suspend fun getTriviaById(id: Int): Trivia?

    // Delete a single Trivia object
    @Delete
    suspend fun deleteTrivia(trivia: Trivia)

    // Delete all Trivia records
    @Query("DELETE FROM Trivia")
    suspend fun deleteAllTrivia()

    // Retrieve the most common category
    @Query("SELECT category FROM Trivia GROUP BY category ORDER BY COUNT(category) DESC LIMIT 1")
    suspend fun getMostCommonCategory(): String?

    // Retrieve the most common difficulty
    @Query("SELECT difficulty FROM Trivia GROUP BY difficulty ORDER BY COUNT(difficulty) DESC LIMIT 1")
    suspend fun getMostCommonDifficulty(): String?

    // Calculate overall average answer time
    @Query("SELECT AVG(avgAnswerTime) FROM Trivia")
    suspend fun getOverallAvgTime(): Float?

}