package se.kth.trivia.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import se.kth.trivia.data.db.TriviaDAO
import se.kth.trivia.data.model.Categories
import se.kth.trivia.data.model.CategoryQuestionCount
import se.kth.trivia.data.model.Trivia
import se.kth.trivia.data.service.TriviaService

class TriviaRepository(
    private val triviaDao: TriviaDAO
) {

    private val api: TriviaService = Retrofit.Builder()
        .baseUrl("https://opentdb.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TriviaService::class.java)

    // Fetch trivia questions from the API
    suspend fun fetchTriviaQuestions(
        amount: Int = 5,
        category: Int? = null,
        difficulty: String? = null,
        type: String? = null
    ): List<Trivia> = withContext(Dispatchers.IO) {
        // API call to get trivia
        val response = api.getTrivia(amount, category, difficulty, type)

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

    suspend fun getQuestionCount(category: Int): CategoryQuestionCount = withContext(Dispatchers.IO) {
        val response = api.getQuestionCount(category)

        return@withContext response
    }

}