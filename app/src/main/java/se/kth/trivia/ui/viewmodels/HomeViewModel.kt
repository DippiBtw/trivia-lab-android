package se.kth.trivia.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ViewModel class
class HomeViewModel : ViewModel() {
    private val _scores = mutableStateOf(listOf<Score>())
    val scores: State<List<Score>> = _scores

    private val _loading = mutableStateOf(true)
    val loading: State<Boolean> = _loading

    init {
        fetchScores()
    }

    private fun fetchScores() {
        // Simulate Firebase fetch
        viewModelScope.launch {
            _loading.value = true
            delay(500)  // Simulate network delay
            _scores.value = listOf(
                Score("2025-01-01", 150),
                Score("2025-01-02", 200),
                Score("2025-01-03", 250)
            )
            _loading.value = false
        }
    }
}

// Sample data class
data class Score(val date: String, val points: Int)