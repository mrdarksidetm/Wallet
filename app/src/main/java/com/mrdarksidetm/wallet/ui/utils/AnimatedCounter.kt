package com.mrdarksidetm.wallet.ui.utils

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

/**
 * Phase 52: Dynamic Number Counting (Odometer Effect)
 * 
 * CRITICAL: Currency Formatting & Rapid State Changes
 * When the Total Balance changes, the digits slide vertically.
 * We parse the formatted currency string and animate each character independently.
 * Commas and decimal points remain static, while numbers roll.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedCounter(
    countString: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default
) {
    Row(modifier = modifier) {
        countString.forEachIndexed { index, char ->
            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInVertically { height -> height } with slideOutVertically { height -> -height }
                    } else {
                        slideInVertically { height -> -height } with slideOutVertically { height -> height }
                    }.using(
                        SizeTransform(clip = false)
                    )
                },
                label = "CounterAnimation_$index"
            ) { targetChar ->
                Text(text = targetChar.toString(), style = style, softWrap = false)
            }
        }
    }
}
