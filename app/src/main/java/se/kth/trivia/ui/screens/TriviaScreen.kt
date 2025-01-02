package se.kth.trivia.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import se.kth.trivia.data.model.Score
import se.kth.trivia.data.model.TriviaCategory
import se.kth.trivia.ui.viewmodels.TriviaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TriviaScreen(
    vm: TriviaViewModel,
    navigateHome: () -> Unit,
    navigateToGame: (category: TriviaCategory, difficulty: String) -> Unit
) {

    val categories by vm.categories
    val loading by vm.loading
    val options = listOf("Easy", "Normal", "Hard", "Mixed")
    val selectedDifficulty = remember { mutableStateOf(options[0]) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    title = { },
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
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                ScoreCard(
                    Modifier.align(Alignment.CenterHorizontally),
                    Score("2025-01-01", 150)
                )

                Row(
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Text(
                        text = "Let's Play",
                        modifier = Modifier
                            .padding(16.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.titleLarge.fontSize
                    )

                    DifficultySelect(
                        options = options,
                        selectedDifficulty = selectedDifficulty,
                    )
                }

                when {
                    loading || categories == null -> {
                        Text(
                            text = "Loading...",
                            modifier = Modifier
                                .padding(16.dp),
                        )
                    }

                    else -> {
                        LazyColumn {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(), // Make sure Row takes full width
                                    horizontalArrangement = Arrangement.spacedBy(8.dp) // Space between columns
                                ) {
                                    val half: Int = categories!!.trivia_categories.size / 2 - 1
                                    val full: Int = categories!!.trivia_categories.size

                                    // First column of categories
                                    CategoryCards(
                                        modifier = Modifier.weight(1f), // Ensure equal width for each column
                                        categories = categories!!.trivia_categories.subList(0, half),
                                        difficulty = selectedDifficulty.value,
                                        navigateToGame = navigateToGame

                                    )
                                    // Second column of categories
                                    CategoryCards(
                                        modifier = Modifier.weight(1f),
                                        categories = categories!!.trivia_categories.subList(
                                            half,
                                            full
                                        ),
                                        difficulty = selectedDifficulty.value,
                                        navigateToGame = navigateToGame
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DifficultySelect(modifier: Modifier = Modifier, options: List<String>, selectedDifficulty: MutableState<String>) {
    val expanded = remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 48.dp, end = 8.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded.value,
            onExpandedChange = { expanded.value = !expanded.value }
        ) {
            TextField(
                modifier = Modifier.menuAnchor(),
                value = selectedDifficulty.value,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
                },
            )

            ExposedDropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                options.forEachIndexed { index, text ->
                    DropdownMenuItem(
                        text = { Text(text) },
                        onClick = {
                            selectedDifficulty.value = options[index]
                            expanded.value = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryCards(
    modifier: Modifier,
    categories: List<TriviaCategory>,
    difficulty: String = "easy",
    navigateToGame: (category: TriviaCategory, difficulty: String) -> Unit
) {
    Column(
        modifier = modifier
            .padding(vertical = 8.dp) // Add padding around the entire column
    ) {
        categories.forEach { category ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    navigateToGame(category, difficulty)
                }
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = category.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreCard(modifier: Modifier, score: Score) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        )
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            ) {
                Text("Ranking")
                Text(
                    text = "1",
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(1f)
            ) {
                Text("Points")
                Text(
                    text = "${score.points}",
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}