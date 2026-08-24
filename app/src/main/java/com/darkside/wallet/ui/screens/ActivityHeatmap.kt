package com.darkside.wallet.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.*

@Composable
fun ActivityHeatmap(
    modifier: Modifier = Modifier,
    heatmapData: Map<Long, Int>,
    baseColor: Color = MaterialTheme.colorScheme.primary
) {
    Column(modifier = modifier) {
        Text(
            text = "Activity Heatmap",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(140.dp)) {
            val width = constraints.maxWidth.toFloat()
            val height = constraints.maxHeight.toFloat()
            
            // 7 rows (days), roughly 12 columns (weeks ~ 90 days)
            val columns = 12
            val rows = 7
            
            val squareSize = (width - (columns - 1) * 8f) / columns
            val actualHeight = rows * squareSize + (rows - 1) * 8f
            
            // Generate last 84 days
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                add(Calendar.DAY_OF_YEAR, -(columns * rows) + 1)
            }
            
            val daysList = mutableListOf<Long>()
            for (i in 0 until (columns * rows)) {
                daysList.add(calendar.timeInMillis)
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            
            val maxCount = heatmapData.values.maxOrNull()?.coerceAtLeast(1) ?: 1
            
            val emptyColor = MaterialTheme.colorScheme.surfaceVariant
            val context = androidx.compose.ui.platform.LocalContext.current
            val density = context.resources.displayMetrics.density
            
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((actualHeight / density).dp)
            ) {
                var dayIndex = 0
                for (col in 0 until columns) {
                    for (row in 0 until rows) {
                        if (dayIndex >= daysList.size) break
                        
                        val date = daysList[dayIndex]
                        val count = heatmapData[date] ?: 0
                        
                        val x = col * (squareSize + 8f)
                        val y = row * (squareSize + 8f)
                        
                        val alpha = if (count == 0) 0f else 0.2f + (0.8f * (count.toFloat() / maxCount.toFloat()))
                        val color = if (count == 0) emptyColor else baseColor.copy(alpha = alpha)
                        
                        drawRoundRect(
                            color = color,
                            topLeft = Offset(x, y),
                            size = Size(squareSize, squareSize),
                            cornerRadius = CornerRadius(8f, 8f)
                        )
                        dayIndex++
                    }
                }
            }
        }
    }
}
