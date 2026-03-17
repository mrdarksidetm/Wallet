package com.mrdarksidetm.wallet.ui.utils

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

@Composable
fun AnimatedCounter(
    countString: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default
) {
    AnimatedContent(
        targetState = countString,
        transitionSpec = {
            (slideInVertically(animationSpec = tween(300)) { height -> height } + fadeIn(animationSpec = tween(300))) togetherWith
            (slideOutVertically(animationSpec = tween(300)) { height -> -height } + fadeOut(animationSpec = tween(300)))
        },
        label = "CounterAnimation",
        modifier = modifier
    ) { targetString ->
        Text(text = targetString, style = style, softWrap = false)
    }
}
