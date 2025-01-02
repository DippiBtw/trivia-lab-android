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
import se.kth.trivia.data.db.AppDatabase
import se.kth.trivia.data.repository.TriviaRepository
import se.kth.trivia.ui.screens.HomeScreen
import se.kth.trivia.ui.screens.LeaderboardScreen
import se.kth.trivia.ui.screens.TriviaScreen
import se.kth.trivia.ui.theme.TriviaLabAndroidTheme
import se.kth.trivia.ui.viewmodels.HomeViewModel
import se.kth.trivia.ui.viewmodels.TriviaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TriviaLabAndroidTheme {
                val db = AppDatabase.getDatabase(applicationContext)

                MainApp(db)
            }
        }
    }
}

@Composable
fun MainApp(db: AppDatabase) {

    val navController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            db
        )
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    db: AppDatabase,
) {

    val triviaRepository = TriviaRepository(db.triviaDao())

    val homeViewModel = HomeViewModel()
    val triviaViewModel = TriviaViewModel(triviaRepository)

    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable(route = "home") {
            HomeScreen(
                homeViewModel,
                navigateLeaderboard = { navController.navigate("leaderboard") },
                navigateTriviaSetup = { navController.navigate("trivia") }
            )
        }
        composable(route = "trivia") {
            TriviaScreen(
                triviaViewModel,
                navigateHome = { navController.navigate("home") }
            )
        }
        composable(route = "leaderboard") {
            LeaderboardScreen(
                navigateHome = { navController.navigate("home") }
            )
        }

    }
}