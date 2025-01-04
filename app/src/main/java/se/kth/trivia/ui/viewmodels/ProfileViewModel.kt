package se.kth.trivia.ui.viewmodels

import android.annotation.SuppressLint
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import se.kth.trivia.data.repository.TriviaRepository

class ProfileViewModel(
    private val triviaRepository: TriviaRepository
) : ViewModel() {

    private val _favouriteCategory = mutableStateOf("")
    val favouriteCategory: State<String> = _favouriteCategory

    private val _favouriteDifficulty = mutableStateOf("")
    val favouriteDifficulty: State<String> = _favouriteDifficulty

    private val _avgAnswerTime = mutableStateOf("")
    val avgAnswerTime: State<String> = _avgAnswerTime

    init {
        fetchStats()
    }

    fun fetchStats() {
        fetchFavouriteCategory()
        fetchFavouriteDifficulty()
        fetchAvgAnswerTime()
    }

    private fun fetchFavouriteCategory() {
        viewModelScope.launch {
            _favouriteCategory.value = triviaRepository.getFavouriteCategory() ?: "No History Found"
        }
    }

    private fun fetchFavouriteDifficulty() {
        viewModelScope.launch {
            _favouriteDifficulty.value =
                triviaRepository.getFavouriteDifficulty() ?: "No History Found"
        }
    }

    @SuppressLint("DefaultLocale")
    private fun fetchAvgAnswerTime() {
        viewModelScope.launch {
            val result = triviaRepository.getAvgAnswerTime()
            _avgAnswerTime.value =
                if (result != null) String.format("%.2f", result)
                else "No History Found"
        }
    }

}