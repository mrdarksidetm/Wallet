package com.darkside.wallet.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A Material 3 Expressive segmented card group that nests multiple action tiles inside a 24dp squircle container.
 */
@Composable
fun SettingsSegmentedGroup(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surfaceContainerLow
        ),
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp,
            colorScheme.outlineVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

/**
 * An individual action tile inside a segmented group or standalone card.
 */
@Composable
fun SettingsActionTile(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    customLeading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    iconColor: Color? = null,
    iconBackgroundColor: Color? = null,
    isDestructive: Boolean = false,
    showDivider: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val colorScheme = MaterialTheme.colorScheme

    val effectiveIconColor = if (isDestructive) colorScheme.error else (iconColor ?: colorScheme.primary)
    val effectiveIconBg = if (isDestructive) colorScheme.errorContainer.copy(alpha = 0.5f)
    else (iconBackgroundColor ?: colorScheme.surfaceContainerHighest)
    val effectiveTitleColor = if (isDestructive) colorScheme.error else colorScheme.onSurface

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (customLeading != null) {
                customLeading()
                Spacer(modifier = Modifier.width(14.dp))
            } else if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(effectiveIconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = effectiveIconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        letterSpacing = (-0.1).sp
                    ),
                    color = effectiveTitleColor
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        lineHeight = 16.sp
                    ),
                    color = colorScheme.onSurfaceVariant
                )
            }

            if (trailing != null) {
                Spacer(modifier = Modifier.width(8.dp))
                trailing()
            } else if (onClick != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = null,
                    tint = colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 76.dp, end = 18.dp),
                thickness = 0.5.dp,
                color = colorScheme.outlineVariant.copy(alpha = 0.35f)
            )
        }
    }
}
