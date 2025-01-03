package se.kth.trivia.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
        question?.incorrect_answers?.plus(
            question?.correct_answer
        )?.shuffled() ?: emptyList()
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
fun TriviaQuestionDisplay(question: TriviaQuestion, timeLeft: Int, shuffledAnswers: List<String?>, onAnswer: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = question.category,
            fontSize = 20.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = question.question,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        shuffledAnswers.forEach { answer ->
            AnswerButton(answer!!) {
                onAnswer(answer)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Time Left: $timeLeft seconds", fontSize = 18.sp, color = Color.Red)
    }
}

@Composable
fun AnswerButton(answer: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.LightGray, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = answer, fontSize = 18.sp, color = Color.Black)
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



