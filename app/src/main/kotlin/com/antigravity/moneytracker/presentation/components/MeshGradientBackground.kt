package com.antigravity.moneytracker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.antigravity.moneytracker.ui.theme.LiquidPeach
import com.antigravity.moneytracker.ui.theme.LiquidPink
import com.antigravity.moneytracker.ui.theme.LiquidWarmWhite

@Composable
fun MeshGradientBackground(
    modifier: Modifier = Modifier
) {
    // Simulating Mesh Gradient with a complex Brush for now
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        LiquidPink,
                        LiquidPeach,
                        LiquidWarmWhite
                    ),
                    radius = 1500f
                )
            )
    )
}
