package com.darkside.wallet.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Spend Heatmap Screen using native Jetpack Compose Canvas.
 * 
 * Architecture & State Flow:
 * 1. The ViewModel computes a `Map<LocalDate, Int>` representing daily transaction volumes.
 * 2. This map is passed to the HeatmapScreen as state.
 * 3. The Canvas iterates over a grid (e.g., 7 days x 12 weeks) and draws rounded rectangles.
 * 4. Color logic: 
 *    - 0 transactions: Light Grey / Surface Variant.
 *    - >0 transactions: MaterialTheme Primary color with varying alpha opacity based on volume intensity.
 * 
 * Why Canvas instead of third-party libraries?
 * Third-party charting libraries inflate APK size and memory footprint, risking OOM crashes on
 * 4GB RAM constraints. Canvas operations are native, lightweight, and extremely performant.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeatmapScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Spend Heatmap") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Transaction Activity",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Simulated transaction intensities for a GitHub-style grid
            // In a real app, calculate intensities based on max transactions in the dataset
            val intensities = List(7 * 12) { (0..4).random() }
            
            val primaryColor = MaterialTheme.colorScheme.primary
            val emptyColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(2f) // Maintains a rectangular grid shape
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f), shape = MaterialTheme.shapes.medium)
                    .padding(16.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val rows = 7
                    val columns = 12
                    
                    // Calculate spacing and cell size to fit the canvas bounds exactly
                    val spacing = 4.dp.toPx()
                    val cellWidth = (size.width - (spacing * (columns - 1))) / columns
                    val cellHeight = (size.height - (spacing * (rows - 1))) / rows

                    for (col in 0 until columns) {
                        for (row in 0 until rows) {
                            val index = (col * rows) + row
                            if (index >= intensities.size) continue

                            val intensity = intensities[index]
                            
                            // Determine cell color opacity based on intensity (0 to 4)
                            val cellColor = if (intensity == 0) {
                                emptyColor
                            } else {
                                primaryColor.copy(alpha = 0.2f + (intensity * 0.2f))
                            }

                            drawRoundRect(
                                color = cellColor,
                                topLeft = Offset(
                                    x = col * (cellWidth + spacing),
                                    y = row * (cellHeight + spacing)
                                ),
                                size = Size(cellWidth, cellHeight),
                                cornerRadius = CornerRadius(4.dp.toPx())
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Less", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.width(8.dp))
                
                // Draw mini legend boxes
                for (i in 0..4) {
                    val legendColor = if (i == 0) emptyColor else primaryColor.copy(alpha = 0.2f + (i * 0.2f))
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(legendColor, shape = MaterialTheme.shapes.extraSmall)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                
                Spacer(modifier = Modifier.width(4.dp))
                Text("More", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
