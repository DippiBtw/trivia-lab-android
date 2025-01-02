package se.kth.trivia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import se.kth.trivia.data.db.AppDatabase
import se.kth.trivia.data.repository.TriviaRepository
import se.kth.trivia.ui.screens.GameScreen
import se.kth.trivia.ui.screens.HomeScreen
import se.kth.trivia.ui.screens.LeaderboardScreen
import se.kth.trivia.ui.screens.LoginScreen
import se.kth.trivia.ui.screens.SignUpScreen
import se.kth.trivia.ui.screens.TriviaScreen
import se.kth.trivia.ui.theme.TriviaLabAndroidTheme
import se.kth.trivia.ui.viewmodels.AuthViewModel
import se.kth.trivia.ui.viewmodels.GameViewModel
import se.kth.trivia.ui.viewmodels.HomeViewModel
import se.kth.trivia.ui.viewmodels.TriviaViewModel

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        enableEdgeToEdge()
        setContent {
            TriviaLabAndroidTheme {
                val db = AppDatabase.getDatabase(applicationContext)

                MainApp(db, auth)
            }
        }
    }
}

@Composable
fun MainApp(db: AppDatabase, auth: FirebaseAuth) {
    // Observe Firebase authentication state (e.g., logged in or not)
    val user = auth.currentUser

    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            db,
            auth,
            user
        )
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    db: AppDatabase,
    auth: FirebaseAuth,
    user: FirebaseUser?
) {

    val triviaRepository = TriviaRepository(db.triviaDao())

    val authViewModel = AuthViewModel()
    val homeViewModel = HomeViewModel()
    val triviaViewModel = TriviaViewModel(triviaRepository)
    val gameViewModel = GameViewModel(triviaRepository)

    NavHost(
        navController = navController,
        startDestination = if (user != null) "home" else "login",
        modifier = modifier
    ) {
        composable(route = "login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home")
                },
                onSignUp = {
                    navController.navigate("signup")
                },
                vm = authViewModel
            )
        }
        composable(route = "signup") {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate("home")
                },
                onLogIn = {
                    navController.navigate("login")
                },
                vm = authViewModel
            )
        }
        composable(route = "home") {
            HomeScreen(
                homeViewModel,
                authViewModel,
                navigateLogin = { navController.navigate("login") },
                navigateLeaderboard = { navController.navigate("leaderboard") },
                navigateTriviaSetup = { navController.navigate("trivia") }
            )
        }
        composable(route = "trivia") {
            TriviaScreen(
                triviaViewModel,
                navigateHome = { navController.navigate("home") },
                navigateToGame = { category, difficulty ->
                    gameViewModel.startGame(category, difficulty)
                    navController.navigate("game")
                }
            )
        }
        composable(route = "leaderboard") {
            LeaderboardScreen(
                navigateHome = { navController.navigate("home") }
            )
        }
        composable(route = "game") {
            GameScreen(gameViewModel)
        }
    }
}
