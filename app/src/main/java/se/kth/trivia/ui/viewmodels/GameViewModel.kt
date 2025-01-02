package se.kth.trivia.ui.viewmodels

import se.kth.trivia.data.model.TriviaCategory
import se.kth.trivia.data.repository.TriviaRepository

class GameViewModel(
    private val triviaRepository: TriviaRepository
) {

    var category: TriviaCategory? = null
    var difficulty: String? = null

    fun startGame(category: TriviaCategory, difficulty: String) {
        this.category = category
        this.difficulty = difficulty
        //fetchQuestions()
    }

    private fun fetchQuestions() {
        TODO()
    }


}