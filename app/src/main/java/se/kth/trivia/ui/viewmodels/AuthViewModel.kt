package se.kth.trivia.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel: ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        if (auth.currentUser != null) {
            _authState.value = AuthState.Authenticated
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun login(email: String, pass: String) {

        if (email.isBlank() || pass.isBlank()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Unknown error")
                }
            }
    }

    fun signup(email: String, pass: String) {

        if (email.isBlank() || pass.isBlank()) {
            _authState.value = AuthState.Error("Email and password cannot be empty")
            return
        }

        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Unknown error")
                }
            }
    }

    fun signout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

}

sealed class AuthState{

    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()

    data class Error(val message: String) : AuthState()
}