package se.kth.trivia.ui.viewmodels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import se.kth.trivia.data.model.Score
import se.kth.trivia.data.model.Trivia
import se.kth.trivia.data.repository.TriviaRepository

// ViewModel class
class HomeViewModel(
    private val triviaRepository: TriviaRepository
) : ViewModel() {
    private val _history = mutableStateOf(listOf<Trivia>())
    val history: State<List<Trivia>> = _history

    private val _loading = mutableStateOf(true)
    val loading: State<Boolean> = _loading

    init {
        fetchScores()
    }

    fun fetchScores() {
        // Simulate Firebase fetch
        viewModelScope.launch {
            _loading.value = true
            delay(500)  // Simulate network delay
            try {
                _history.value = triviaRepository.getCompletedTrivia()
            } catch (e: Exception) {
                Log.d("HomeViewModel", "Error fetching scores: ${e.message}")
            }
            _loading.value = false
        }
    }
}