package se.kth.trivia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import se.kth.trivia.ui.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    vm: ProfileViewModel,
    navigateHome: () -> Unit
) {
    val favouriteCategory = vm.favouriteCategory.value
    val favouriteDifficulty = vm.favouriteDifficulty.value
    val avgAnswerTime = vm.avgAnswerTime.value
    val avgAccuracy = vm.avgAccuracy.value

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
                title = {
                    Text("Your Profile", style = MaterialTheme.typography.titleLarge)
                },
                navigationIcon = {
                    IconButton(onClick = navigateHome) {
                        Icon(
                            modifier = Modifier.size(32.dp),
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
            ) {
                YourStats(favouriteCategory, favouriteDifficulty, avgAnswerTime, avgAccuracy)
            }
        }
    )
}

@Composable
fun YourStats(category: String, difficulty: String, avgAnswerTime: String, avgAccuracy: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        StatCard(
            title = "Favourite Category",
            value = category
        )

        Spacer(modifier = Modifier.height(16.dp))

        StatCard(
            title = "Favourite Difficulty",
            value = difficulty
        )

        Spacer(modifier = Modifier.height(16.dp))


        StatCard(
            title = "Overall Accuracy",
            value = if (avgAccuracy == "No History Found") avgAccuracy else "${avgAccuracy}%"
        )



        Spacer(modifier = Modifier.height(16.dp))

        StatCard(
            title = "Average Answer Time",
            value = if(avgAnswerTime == "No History Found") avgAnswerTime else "${avgAnswerTime}s"
        )


    }
}

@Composable
fun StatCard(title: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = MaterialTheme.shapes.large.copy(CornerSize(16.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize * 1.2f
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}
