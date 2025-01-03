package se.kth.trivia.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import se.kth.trivia.data.model.Trivia
import se.kth.trivia.data.model.TriviaCategory
import se.kth.trivia.data.model.TriviaQuestion
import se.kth.trivia.data.repository.TriviaRepository

class GameViewModel(
    private val triviaRepository: TriviaRepository
): ViewModel() {

    private var trivia: Trivia? = null

    private var questionIndex = 0
    private val _question = MutableLiveData<TriviaQuestion>()
    val question: LiveData<TriviaQuestion> = _question

    private val _active = MutableLiveData(false)
    val active: LiveData<Boolean> = _active

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _score = mutableStateOf(0)
    val score: State<Int> = _score

    private var category: TriviaCategory? = null
    private var difficulty: String? = null
    private var nrOfQuestions: String? = null

    fun startGame(category: TriviaCategory, difficulty: String, nrOfQuestions: String) {
        _active.value = true
        questionIndex = 0
        this._score.value = 0
        this.trivia = null
        this.category = category
        this.difficulty = difficulty
        this.nrOfQuestions = nrOfQuestions
        fetchQuestions()
    }

    fun answerQuestion(answer: String?) {
        viewModelScope.launch {
            _question.value?.correct = answer != null && answer == _question.value?.correct_answer

            if (_question.value?.correct == true) {
                _score.value += when(_question.value?.difficulty) {
                    "easy" -> 10
                    "medium" -> 20
                    "hard" -> 30
                    else -> 0
                }
            }

            if (questionIndex < (trivia?.results?.size ?: 0)) {
                _question.value = trivia?.results?.get(questionIndex++)
            } else {
                _active.value = false
                trivia?.score = _score.value
                trivia?.timestamp = System.currentTimeMillis()
                triviaRepository.saveCompletedTrivia(trivia!!)
            }
        }
    }

    private fun fetchQuestions() {
        _loading.value = true
        viewModelScope.launch {
            try {
                trivia = triviaRepository.fetchTrivia(
                    amount = nrOfQuestions?.toInt() ?: 5,
                    category = category?.id,
                    difficulty = difficulty,
                )

                if (trivia?.response_code != 0) {
                    _active.value = false
                    Log.d("GameViewModel", "Error fetching, code: ${trivia?.response_code}")
                } else {
                    _question.value = trivia?.results?.get(questionIndex++)
                }
            } catch (e: Exception) {
                _active.value = false
                Log.d("GameViewModel", "Error fetching trivia: ${e.message}")
            }
            _loading.value = false
        }
    }


}