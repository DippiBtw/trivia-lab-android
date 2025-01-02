package se.kth.trivia.data.service

import retrofit2.http.GET
import retrofit2.http.Query
import se.kth.trivia.data.model.Trivia

interface TriviaService {

    @GET("api.php")
    suspend fun getTrivia(
        @Query("amount") amount: Int = 5,
        @Query("category") category: Int? = null,
        @Query("difficulty") difficulty: String? = null,
        @Query("type") type: String? = null,
    ): List<Trivia>


}