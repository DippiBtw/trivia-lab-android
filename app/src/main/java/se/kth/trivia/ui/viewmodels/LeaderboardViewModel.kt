package se.kth.trivia.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import se.kth.trivia.data.repository.FirestoreRepository
import se.kth.trivia.data.repository.Player

class LeaderboardViewModel(
    private val firestoreRepository: FirestoreRepository
) : ViewModel() {

    private val _topUsers = mutableStateOf<List<Pair<String, Int>>>(emptyList())
    val topUsers: State<List<Pair<String, Int>>> = _topUsers

    private val _userScore = mutableStateOf<Pair<String, Int>?>(null)
    val userScore: State<Pair<String, Int>?> = _userScore

    init {
        fetchTopUsers()
    }

    fun fetchTopUsers() {
        viewModelScope.launch {
            val topUsers = firestoreRepository.fetchTopUsers()
            _topUsers.value = topUsers

            val user = firestoreRepository.fetchHighscore()
            _userScore.value = user
        }
    }

}
