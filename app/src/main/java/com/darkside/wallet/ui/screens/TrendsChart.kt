package com.darkside.wallet.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.darkside.wallet.ui.theme.Income
import com.darkside.wallet.ui.theme.Expense

@Composable
fun TrendsChart(
    modifier: Modifier = Modifier,
    incomeTrends: List<Pair<Long, Double>>,
    expenseTrends: List<Pair<Long, Double>>
) {
    Column(modifier = modifier) {
        Text(
            text = "Trends (Last 30 Days)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            if (incomeTrends.isEmpty() && expenseTrends.isEmpty()) {
                Text(
                    text = "No data available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                )
                return@Box
            }

            val maxAmount = (incomeTrends.maxOfOrNull { it.second } ?: 0.0)
                .coerceAtLeast(expenseTrends.maxOfOrNull { it.second } ?: 0.0)
                .coerceAtLeast(1.0)
                .toFloat()

            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                fun drawTrendLine(data: List<Pair<Long, Double>>, color: Color) {
                    if (data.isEmpty()) return
                    if (data.size == 1) {
                        drawCircle(
                            color = color,
                            radius = 4.dp.toPx(),
                            center = androidx.compose.ui.geometry.Offset(width / 2, height - (data[0].second.toFloat() / maxAmount * height))
                        )
                        return
                    }

                    val path = Path()
                    val stepX = width / (data.size - 1)

                    data.forEachIndexed { index, pair ->
                        val x = index * stepX
                        val y = height - (pair.second.toFloat() / maxAmount * height)
                        if (index == 0) {
                            path.moveTo(x, y)
                        } else {
                            val prevX = (index - 1) * stepX
                            val prevY = height - (data[index - 1].second.toFloat() / maxAmount * height)
                            // Smooth bezier curve
                            val controlX1 = prevX + stepX / 2
                            val controlY1 = prevY
                            val controlX2 = prevX + stepX / 2
                            val controlY2 = y
                            path.cubicTo(controlX1, controlY1, controlX2, controlY2, x, y)
                        }
                    }

                    // Draw gradient under the line
                    val gradientPath = Path().apply {
                        addPath(path)
                        lineTo(width, height)
                        lineTo(0f, height)
                        close()
                    }
                    
                    drawPath(
                        path = gradientPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(color.copy(alpha = 0.2f), Color.Transparent),
                            startY = 0f,
                            endY = height
                        )
                    )

                    // Draw the line itself
                    drawPath(
                        path = path,
                        color = color,
                        style = Stroke(
                            width = 3.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }

                drawTrendLine(expenseTrends, Expense)
                drawTrendLine(incomeTrends, Income)
            }
        }
    }
}
