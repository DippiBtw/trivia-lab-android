package se.kth.trivia.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import se.kth.trivia.data.model.Categories
import se.kth.trivia.data.model.CategoryQuestionCount
import se.kth.trivia.data.repository.TriviaRepository

class TriviaViewModel(
    private val triviaRepository: TriviaRepository
): ViewModel() {

    private val _triviaGenres = mutableStateOf<Categories?>(null)
    val triviaGenres: State<Categories?> = _triviaGenres

    private val _loading = mutableStateOf(true)
    val loading: State<Boolean> = _loading

    init {
        fetchGenres()
    }

    private fun fetchGenres() {
        // Simulate Firebase fetch
        viewModelScope.launch {
            _loading.value = true
            delay(500)  // Simulate network delay
            _triviaGenres.value = triviaRepository.getTriviaGenres()
            _loading.value = false
        }
    }

}