package se.kth.trivia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import se.kth.trivia.data.model.TriviaQuestion
import se.kth.trivia.ui.viewmodels.GameViewModel

@Composable
fun GameScreen(vm: GameViewModel, navigateHome: () -> Unit) {
    val active by vm.active.observeAsState()
    val loading by vm.loading
    val question by vm.question.observeAsState()
    val shuffledAnswers = remember(question) {
        question?.incorrect_answers?.plus(question!!.correct_answer)?.filterNotNull()?.shuffled() ?: emptyList()
    }
    val score by vm.score

    var timeLeft by remember { mutableIntStateOf(15) }
    var answered by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = question) {
        timeLeft = 15
        answered = false
        while (timeLeft > 0 && !answered) {
            delay(1000L)
            timeLeft--
        }
        vm.answerQuestion(null)
    }

    if (loading) {
        LoadingScreen()
    } else if (active == true && question != null) {
        TriviaQuestionDisplay(
            question = question!!,
            timeLeft = timeLeft,
            shuffledAnswers = shuffledAnswers,
            onAnswer = { answer ->
                answered = true
                vm.answerQuestion(answer)
            }
        )
    } else {
        GameEndScreen(score, navigateHome)
    }
}

@Composable
fun TriviaQuestionDisplay(
    question: TriviaQuestion,
    timeLeft: Int,
    shuffledAnswers: List<String>,
    onAnswer: (String) -> Unit
) {
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }

    LaunchedEffect(selectedAnswer) {
        if (selectedAnswer != null) {
            showFeedback = true
            delay(500L) // Show feedback for 0.5 seconds
            onAnswer(selectedAnswer!!)
            showFeedback = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CategoryAndQuestionDisplay(
            category = question.category,
            questionText = question.question
        )

        Spacer(modifier = Modifier.height(16.dp))

        TimerDisplay(timeLeft = timeLeft, totalTime = 15)

        Spacer(modifier = Modifier.height(24.dp))

        shuffledAnswers.forEach { answer ->
            val feedbackColor = getFeedbackColor(answer, question.correct_answer, selectedAnswer, showFeedback)
            AnswerButton(answer, feedbackColor) {
                if (!showFeedback) {
                    selectedAnswer = answer
                }
            }
        }
    }
}

@Composable
fun CategoryAndQuestionDisplay(category: String, questionText: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = category,
            fontSize = 20.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = questionText,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun TimerDisplay(timeLeft: Int, totalTime: Int = 15) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(100.dp)
    ) {
        val progress = timeLeft.toFloat() / totalTime.toFloat()

        // Circular progress bar
        CircularProgressIndicator(
            progress = progress,
            strokeWidth = 8.dp,
            color = if (timeLeft > 5) Color.Green else Color.Red, // Changes color based on urgency
            modifier = Modifier.fillMaxSize()
        )

        // Time Left as Text
        Text(
            text = "$timeLeft",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun AnswerButton(answer: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(color, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = answer, fontSize = 18.sp, color = Color.Black)
    }
}

fun getFeedbackColor(
    answer: String,
    correctAnswer: String,
    selectedAnswer: String?,
    showFeedback: Boolean
): Color {
    return if (showFeedback) {
        when (answer) {
            correctAnswer -> Color.Green
            selectedAnswer -> Color.Red
            else -> Color.LightGray
        }
    } else {
        Color.LightGray
    }
}


@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun GameEndScreen(score: Int, navigateHome: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Game Over",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Your Score: $score",
                fontSize = 28.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = navigateHome,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Return", fontSize = 18.sp, color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}



