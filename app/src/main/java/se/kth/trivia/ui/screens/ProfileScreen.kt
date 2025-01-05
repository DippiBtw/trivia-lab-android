package se.kth.trivia.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import se.kth.trivia.data.model.Statistics
import se.kth.trivia.ui.viewmodels.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    vm: ProfileViewModel,
    navigateHome: () -> Unit
) {
    val favouriteCategory by vm.favouriteCategory
    val favouriteDifficulty by vm.favouriteDifficulty
    val avgAnswerTime by vm.avgAnswerTime
    val avgAccuracy by vm.avgAccuracy
    val latestStats by vm.latestStats

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
                YourStats(
                    favouriteCategory,
                    favouriteDifficulty,
                    avgAnswerTime,
                    avgAccuracy,
                    stats = latestStats
                )
            }
        }
    )
}

@Composable
fun YourStats(
    category: String,
    difficulty: String,
    avgAnswerTime: String,
    avgAccuracy: String,
    stats: List<Statistics>? = null
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        item {
            StatCard(
                title = "Favourite Category",
                value = category
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            StatCard(
                title = "Favourite Difficulty",
                value = difficulty
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            StatCard(
                title = "Overall Accuracy",
                value = if (avgAccuracy == "No History Found") avgAccuracy else "${avgAccuracy}%",
                stats = stats?.map { it.avgAccuracy },
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            StatCard(
                title = "Average Answer Time",
                value = if (avgAnswerTime == "No History Found") avgAnswerTime else "${avgAnswerTime}s",
                stats = stats?.map { it.avgAnswerTime },
                max = 15f
            )
        }
    }
}


@Composable
fun StatCard(title: String, value: String, stats: List<Float>? = null, max: Float = 100f) {
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
            if (stats != null) {
                Spacer(modifier = Modifier.height(2.dp))
                StatChart(stats, max)
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun StatChart(
    stats: List<Float>,
    maxValue: Float = 100f
) {
    if (stats.isEmpty()) return
    val steps = 5

    val pointsData: List<Point> = if (stats.size == 1) {
        listOf(Point(0f, stats[0]), Point(1f, stats[0]))
    } else {
        listOf(
            Point(0f, 0f),
            Point(1f, maxValue)
        ) +
        stats.mapIndexed { index, stat -> Point((index + 2).toFloat(), stat) }
    }

    val xAxisData = AxisData.Builder()
        .axisStepSize(100.dp)
        .backgroundColor(Color.Transparent)
        .steps(pointsData.size - 1)
        .labelData { i -> i.toString() }
        .labelAndAxisLinePadding(15.dp)
        .axisLineColor(MaterialTheme.colorScheme.onBackground)
        .axisLabelColor(MaterialTheme.colorScheme.onBackground)
        .build()

    val yAxisData = AxisData.Builder()
        .steps(steps)
        .backgroundColor(Color.Transparent)
        .labelAndAxisLinePadding(20.dp)
        .labelData { i ->
            val yScale = maxValue / steps
            String.format("%.0f", i * yScale)
        }
        .axisLineColor(MaterialTheme.colorScheme.onBackground)
        .axisLabelColor(MaterialTheme.colorScheme.onBackground)
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    LineStyle(
                        color = MaterialTheme.colorScheme.primary,
                        lineType = LineType.SmoothCurve(isDotted = false)
                    ),
                    IntersectionPoint(color = MaterialTheme.colorScheme.primary),
                    SelectionHighlightPoint(color = MaterialTheme.colorScheme.primary),
                    ShadowUnderLine(
                        alpha = 0.5f,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.inversePrimary,
                                Color.Transparent
                            )
                        )
                    ),
                    SelectionHighlightPopUp()
                )
            ),
        ),
        backgroundColor = MaterialTheme.colorScheme.surface,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
        )
    )

    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        lineChartData = lineChartData
    )
}