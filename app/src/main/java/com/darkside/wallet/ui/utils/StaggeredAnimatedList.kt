package com.darkside.wallet.ui.utils

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Phase 52: Staggered List Entrance Choreography
 * 
 * CRITICAL: Fluid Entrance Animations
 * Uses Compose Foundation `Modifier.animateItem()` to automatically animate
 * items when they enter, exit, or are repositioned. We apply this to a list
 * so transactions glide into view on the initial load and when deleted.
 * Avoids heavy custom logic to maintain 120Hz frame rates.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> StaggeredAnimatedList(
    items: List<T>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    key: ((item: T) -> Any)? = null,
    itemContent: @Composable (index: Int, item: T, itemModifier: Modifier) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding
    ) {
        itemsIndexed(items = items, key = { index, item -> key?.invoke(item) ?: index }) { index, item ->
            // Use animateItem for built-in placement/appearance animations
            val itemModifier = Modifier.animateItem(
                fadeInSpec = tween(durationMillis = 300, delayMillis = index * 30),
                placementSpec = tween(durationMillis = 300),
                fadeOutSpec = tween(durationMillis = 200)
            )
            itemContent(index, item, itemModifier)
        }
    }
}
