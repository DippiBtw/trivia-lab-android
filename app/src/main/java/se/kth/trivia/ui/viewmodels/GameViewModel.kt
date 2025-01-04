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
import se.kth.trivia.data.repository.FirestoreRepository
import se.kth.trivia.data.repository.TriviaRepository

class GameViewModel(
    private val triviaRepository: TriviaRepository,
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

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
    private var nrOfQuestions: Int? = null
    private var avgTime: Float = 0f
    private var correctGuesses: Int = 0

    private var answered = false

    fun startGame(category: TriviaCategory, difficulty: String, nrOfQuestions: Int) {
        _active.value = true
        questionIndex = 0
        avgTime = 0f
        correctGuesses = 0
        trivia = null
        answered = false
        this.category = category
        this.difficulty = difficulty
        this.nrOfQuestions = nrOfQuestions
        fetchQuestions()
    }

    fun answerQuestion(answer: String?, timeLeft: Int) {
        if (answered) return

        viewModelScope.launch {
            answered = true
            _question.value?.correct = answer != null && answer == _question.value?.correct_answer
            avgTime += timeLeft

            if (_question.value?.correct == true) {
                correctGuesses++
                _score.value += when (_question.value?.difficulty) {
                    "easy" -> 10
                    "medium" -> 20
                    "hard" -> 30
                    else -> 0
                }
            }

            if (questionIndex < (trivia?.results?.size ?: 0)) {
                _question.value = trivia?.results?.get(questionIndex++)
                answered = false
            } else {
                _active.value = false
                trivia?.score = _score.value
                trivia?.timestamp = System.currentTimeMillis()
                trivia?.avgAnswerTime = avgTime / nrOfQuestions!!
                trivia?.avgAccuracy = (correctGuesses.toFloat() / nrOfQuestions!!) * 100
                triviaRepository.saveCompletedTrivia(trivia!!)
                firestoreRepository.saveHighscore(_score.value)
            }
        }
    }

    private fun fetchQuestions() {
        _loading.value = true
        viewModelScope.launch {
            try {
                _score.value = 0
                trivia = triviaRepository.fetchTrivia(
                    amount = nrOfQuestions ?: 5,
                    category = category?.id,
                    difficulty = difficulty,
                )

                if (trivia?.response_code != 0) {
                    _active.value = false
                    Log.d("GameViewModel", "Error fetching, code: ${trivia?.response_code}")
                } else {
                    trivia?.category = category?.name ?: ""
                    trivia?.difficulty = difficulty ?: ""
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