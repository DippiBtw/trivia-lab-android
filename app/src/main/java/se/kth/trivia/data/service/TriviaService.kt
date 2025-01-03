package se.kth.trivia.data.service

import retrofit2.http.GET
import retrofit2.http.Query
import se.kth.trivia.data.model.Categories
import se.kth.trivia.data.model.CategoryQuestionCount
import se.kth.trivia.data.model.Trivia

interface TriviaService {

    @GET("api.php")
    suspend fun getTrivia(
        @Query("amount") amount: Int = 5,
        @Query("category") category: Int? = null,
        @Query("difficulty") difficulty: String? = null,
        @Query("type") type: String? = null,
        @Query("encode") encode: String = "base64"
    ): Trivia

    @GET("api_category.php")
    suspend fun getCategories(): Categories

    @GET("api_count.php")
    suspend fun getQuestionCount(
        @Query("category") category: Int,
    ): CategoryQuestionCount

}