package com.mrdarksidetm.wallet.ui.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Phase 52: Container Transforms (Hero Animations)
 * 
 * CRITICAL: Shared Element Transitions
 * Uses Compose 1.7+ `SharedTransitionLayout`. When a user taps a card,
 * instead of pushing a new screen, the card bounds physically expand to
 * fill the screen. The background fades out smoothly, preserving spatial context.
 * 
 * Note: Requires wrapping the Navigation graph or the specific screen flow
 * in a SharedTransitionLayout.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedElementContainer(
    isExpanded: Boolean,
    sharedKey: String,
    onToggle: () -> Unit,
    closedContent: @Composable SharedTransitionScope.() -> Unit,
    expandedContent: @Composable SharedTransitionScope.() -> Unit
) {
    SharedTransitionLayout {
        AnimatedVisibility(
            visible = !isExpanded,
            label = "ClosedState"
        ) {
            Box(
                modifier = Modifier
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = sharedKey),
                        animatedVisibilityScope = this,
                        boundsTransform = { _, _ -> tween(500) }
                    )
                    .clickable { onToggle() }
            ) {
                closedContent()
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            label = "ExpandedState"
        ) {
            Box(
                modifier = Modifier
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = sharedKey),
                        animatedVisibilityScope = this,
                        boundsTransform = { _, _ -> tween(500) }
                    )
            ) {
                expandedContent()
            }
        }
    }
}
