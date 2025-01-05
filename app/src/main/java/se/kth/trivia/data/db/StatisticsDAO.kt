package se.kth.trivia.data.db

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import se.kth.trivia.data.model.Statistics
import se.kth.trivia.data.model.Trivia

interface StatisticsDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatistics(trivia: Statistics)

    @Query("SELECT * FROM Statistics")
    suspend fun getAllStatistics(): List<Statistics>

    @Query(
        """
    SELECT AVG(avgAnswerTime) 
    FROM (
        SELECT avgAnswerTime 
        FROM Statistics 
        ORDER BY timestamp DESC 
        LIMIT 100
    )
"""
    )
    suspend fun getAvgTimeOfLatest100(): Float?

    @Query(
        """
    SELECT AVG(avgAccuracy) 
    FROM (
        SELECT avgAccuracy 
        FROM Statistics 
        ORDER BY timestamp DESC 
        LIMIT 100
    )
"""
    )
    suspend fun getAvgAccuracyOfLatest100(): Float?


    @Query("DELETE FROM Statistics")
    suspend fun deleteAllStatistics()

    @Query("SELECT * FROM Statistics WHERE id = :id")
    suspend fun getStatisticsById(id: Int): Statistics?

    @Query("SELECT * FROM Statistics ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestStatistics(): Statistics?

    @Query("SELECT COUNT(*) FROM Statistics")
    suspend fun getNrOfRows(): Int

    @Query("SELECT * FROM Statistics ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getStatisticsSortedByDate(limit: Int = 20): List<Statistics>


}