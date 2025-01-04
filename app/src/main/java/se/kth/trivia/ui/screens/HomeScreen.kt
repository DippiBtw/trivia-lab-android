package se.kth.trivia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import se.kth.trivia.ui.viewmodels.AuthState
import se.kth.trivia.ui.viewmodels.AuthViewModel
import se.kth.trivia.ui.viewmodels.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    vm: HomeViewModel = viewModel(),
    authVm: AuthViewModel,
    navigateLogin: () -> Unit,
    navigateLeaderboard: () -> Unit,
    navigateTriviaSetup: () -> Unit,
    navigateProfile: () -> Unit
) {
    val history by vm.history
    val loading by vm.loading
    val authState by authVm.authState.observeAsState()
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Unauthenticated -> navigateLogin()
            else -> Unit
        }
    }

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(
                text = "Welcome Back!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterStart)
            )

            IconButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    modifier = Modifier.size(30.dp),
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Profile Menu"
                )
            }

            // The dropdown menu is positioned below the icon button
            if (expanded) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 40.dp) // Adjust the padding to position below the icon
                ) {
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Profile") },
                            onClick = {
                                navigateProfile()
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                authVm.signout()
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        if (loading) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
            }

        } else {
            if (history.isEmpty()) {
                Text(
                    "No scores yet. Get started by playing!",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp)
                ) {
                    items(history) { trivia ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = formatter.format(Date(trivia.timestamp)),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = "${trivia.category} - ${trivia.difficulty}",
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = "${trivia.score} points",
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = navigateLeaderboard,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text("View Leaderboard")
        }

        OutlinedButton(
            onClick = navigateTriviaSetup,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text("Start New Trivia")
        }
    }
}


