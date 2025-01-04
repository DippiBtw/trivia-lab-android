package se.kth.trivia.data.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import se.kth.trivia.data.db.TriviaDAO
import se.kth.trivia.data.model.Categories
import se.kth.trivia.data.model.CategoryQuestionCount
import se.kth.trivia.data.model.Trivia
import se.kth.trivia.data.service.TriviaService
import java.util.Base64

class TriviaRepository(
    private val triviaDao: TriviaDAO
) {

    private val api: TriviaService = Retrofit.Builder()
        .baseUrl("https://opentdb.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TriviaService::class.java)

    // Fetch trivia questions from the API
    suspend fun fetchTrivia(
        amount: Int = 5,
        category: Int? = null,
        difficulty: String? = null,
        type: String? = null
    ): Trivia = withContext(Dispatchers.IO) {
        val response = api.getTrivia(amount, category, difficulty?.lowercase(), type)

        for (result in response.results) {
            result.type = decode(result.type)
            result.difficulty = decode(result.difficulty)
            result.category = decode(result.category)
            result.question = decode(result.question)
            result.incorrect_answers = result.incorrect_answers.map { decode(it) }
            result.correct_answer = decode(result.correct_answer)
        }

        return@withContext response
    }

    // Save completed trivia session to the local database
    suspend fun saveCompletedTrivia(trivia: Trivia) = withContext(Dispatchers.IO) {
        triviaDao.insertTrivia(trivia)
    }


    // Get all completed trivia sessions from the local database
    suspend fun getCompletedTrivia(): List<Trivia> = withContext(Dispatchers.IO) {
        return@withContext triviaDao.getAllTrivia()
    }

    // Delete all trivia questions from the database
    suspend fun clearLocalTrivia() = withContext(Dispatchers.IO) {
        triviaDao.deleteAllTrivia()
    }

    suspend fun getTriviaGenres(): Categories = withContext(Dispatchers.IO) {
        val response = api.getCategories()

        return@withContext response
    }

    suspend fun getQuestionCount(category: Int): CategoryQuestionCount =
        withContext(Dispatchers.IO) {
            val response = api.getQuestionCount(category)

            return@withContext response
        }

    // Get the most common category played on the device
    suspend fun getFavouriteCategory() = withContext(Dispatchers.IO) {
        triviaDao.getMostCommonCategory()
    }

    // Get the most common difficulty played on the device
    suspend fun getFavouriteDifficulty() = withContext(Dispatchers.IO) {
        triviaDao.getMostCommonDifficulty()
    }

    // Get the overall avg question answer time on the device
    suspend fun getAvgAnswerTime() = withContext(Dispatchers.IO) {
        triviaDao.getOverallAvgTime()
    }

    suspend fun getAvgAccuracy() = withContext(Dispatchers.IO) {
        triviaDao.getOverallAvgAccuracy()
    }


    // Decode Base64 encoded string
    private fun decode(encoded: String): String {
        return try {
            val trimmedEncoded = encoded.trim()
            val decodedBytes = Base64.getDecoder().decode(trimmedEncoded)
            String(decodedBytes)
        } catch (e: Exception) {
            Log.e("TriviaRepository", "Error decoding string", e)
            encoded // Return the original string in case of error
        }
    }

}