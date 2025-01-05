package se.kth.trivia.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import se.kth.trivia.data.db.StatisticsDAO
import se.kth.trivia.data.model.Statistics

class StatisticRepository(
    private val statisticsDao: StatisticsDAO
) {

    suspend fun saveStatistic(statistic: Statistics) = withContext(Dispatchers.IO) {
        statisticsDao.insertStatistics(statistic)
    }

    // Get the overall avg question answer time on the device
    suspend fun getAvgAnswerTime() = withContext(Dispatchers.IO) {
        statisticsDao.getAvgTimeOfLatest50()
    }

    suspend fun getAvgAccuracy() = withContext(Dispatchers.IO) {
        statisticsDao.getAvgAccuracyOfLatest50()
    }

    suspend fun getStats(samples: Int): List<Statistics> = withContext(Dispatchers.IO) {
        val rawStatistics = statisticsDao.getStatisticsSortedByDate(samples)

        var cumulativeSumTime = 0f
        var cumulativeSumAccuracy = 0f
        val gradualStatistics = mutableListOf<Statistics>()

        for ((index, stat) in rawStatistics.reversed().withIndex()) {
            cumulativeSumTime += stat.avgAnswerTime
            cumulativeSumAccuracy += stat.avgAccuracy
            gradualStatistics.add(
                stat.copy(
                    avgAnswerTime = cumulativeSumTime / (index + 1),
                    avgAccuracy = cumulativeSumAccuracy / (index + 1)
                )
            )
        }

        return@withContext gradualStatistics
    }

}