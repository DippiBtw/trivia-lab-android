package se.kth.trivia.ui.viewmodels

import android.annotation.SuppressLint
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import se.kth.trivia.data.model.Statistics
import se.kth.trivia.data.repository.StatisticRepository
import se.kth.trivia.data.repository.TriviaRepository
import java.util.Locale

class ProfileViewModel(
    private val triviaRepository: TriviaRepository,
    private val statsRepository: StatisticRepository
) : ViewModel() {

    private val _favouriteCategory = mutableStateOf("")
    val favouriteCategory: State<String> = _favouriteCategory

    private val _favouriteDifficulty = mutableStateOf("")
    val favouriteDifficulty: State<String> = _favouriteDifficulty

    private val _avgAnswerTime = mutableStateOf("")
    val avgAnswerTime: State<String> = _avgAnswerTime

    private val _avgAccuracy = mutableStateOf("")
    val avgAccuracy: State<String> = _avgAccuracy

    private val _latestStats = mutableStateOf<List<Statistics>>(emptyList())
    val latestStats: State<List<Statistics>> = _latestStats

    init {
        fetchStats()
    }

    fun fetchStats() {
        fetchFavouriteCategory()
        fetchFavouriteDifficulty()
        fetchAvgAnswerTime()
        fetchAvgAccuracy()
        fetchLatestStats()
    }

    private fun fetchFavouriteCategory() {
        viewModelScope.launch {
            _favouriteCategory.value = triviaRepository.getFavouriteCategory() ?: "No History Found"
        }
    }

    private fun fetchAvgAccuracy() {
        viewModelScope.launch {
            val result = statsRepository.getAvgAccuracy()
            _avgAccuracy.value =
                if (result != null) String.format(Locale.getDefault(),"%.2f", result)
                else "No History Found"
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
            val result = statsRepository.getAvgAnswerTime()
            _avgAnswerTime.value =
                if (result != null) String.format("%.2f", result)
                else "No History Found"
        }
    }

    private fun fetchLatestStats() {
        viewModelScope.launch {
            val result = statsRepository.getStats(50)
            _latestStats.value = result
        }
    }

}