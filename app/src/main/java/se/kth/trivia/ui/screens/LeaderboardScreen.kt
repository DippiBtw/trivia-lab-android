package se.kth.trivia.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.ListItemDefaults.contentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Player(val name: String, val points: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    navigateHome: () -> Unit,
) {
    // Sample static players data (for now)
    val players = listOf(
        Player("Alice", 120),
        Player("Bob", 110),
        Player("Charlie", 90),
        Player("David", 85),
        Player("Eve", 80),
        Player("Frank", 70),
        Player("Grace", 60),
        Player("Hank", 50),
        Player("Ivy", 45),
        Player("Jack", 40),
        Player("Tom", 30),
        Player("Jesse", 20),
        Player("MonkeyWoman", 15)
    )

    // Your static data for now
    val yourName = "YourName"
    val yourPoints = 5
    val yourRank = 52

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
                title = {
                    Text("Leaderboard", style = MaterialTheme.typography.titleLarge)
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
        content = { it ->
            Log.d("LeaderboardScreen", it.toString())

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = it.calculateTopPadding())
            ) {
                // Top 10 leaderboard
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(players.take(20)) { player ->
                        LeaderboardItem(player = player, rank = players.indexOf(player) + 1)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Divider(
                    modifier = Modifier
                        .padding(horizontal = 50.dp)
                        .align(Alignment.CenterHorizontally)
                        .height(4.dp)
                        .clip(MaterialTheme.shapes.small)

                )

                // Your stats at the bottom
                YourStats(
                    name = yourName,
                    points = yourPoints,
                    rank = yourRank
                )
            }
        }
    )
}

@Composable
fun LeaderboardItem(player: Player, rank: Int) {
    val rankColor = when (rank) {
        1 -> Color(0xFFFFD700) // Gold for 1st
        2 -> Color(0xFFC0C0C0) // Silver for 2nd
        3 -> Color(0xFFCD7F32) // Bronze for 3rd
        else -> MaterialTheme.colorScheme.onSurface // Default color
    }

    val rankIcon = when (rank) {
        1 -> "🏅" // Gold medal icon
        2 -> "🥈" // Silver medal icon
        3 -> "🥉" // Bronze medal icon
        else -> ""
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 16.dp)
            .background(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.shapes.medium
            )
            .padding(16.dp)
    ) {
        Text(
            text = "$rankIcon #$rank ${player.name}",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = if (rank <= 3) MaterialTheme.typography.bodyLarge.fontSize * (1.8f - rank * 0.2f) else MaterialTheme.typography.bodyLarge.fontSize
            ),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${player.points} pts",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = rankColor
        )
    }
}

@Composable
fun YourStats(name: String, points: Int, rank: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.shapes.medium
            )
            .padding(16.dp)
    ) {
        Text(
            "Your Stats",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("Name: $name", style = MaterialTheme.typography.bodyMedium)
        Text("Rank: #$rank", style = MaterialTheme.typography.bodyMedium)
        Text("Points: $points", style = MaterialTheme.typography.bodyMedium)
    }
}

