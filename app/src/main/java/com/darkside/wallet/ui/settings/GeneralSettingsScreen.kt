package com.darkside.wallet.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.darkside.wallet.ui.WalletViewModel
import com.darkside.wallet.ui.components.AppBackButton
import com.darkside.wallet.utils.FileUtils
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralSettingsScreen(
    viewModel: WalletViewModel,
    onBack: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToCurrencySelection: () -> Unit,
    onNavigateToCategories: () -> Unit,
    onNavigateToFeedback: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userPhoto by viewModel.userPhotoPath.collectAsStateWithLifecycle()
    val currencyCode by viewModel.currencyCode.collectAsStateWithLifecycle()

    val hasValidPhoto = FileUtils.fileExists(userPhoto)

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        "General",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 34.sp,
                            letterSpacing = (-0.5).sp
                        )
                    )
                },
                navigationIcon = {
                    AppBackButton(onBack = onBack, modifier = Modifier.padding(start = 12.dp))
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = colorScheme.surface
                )
            )
        },
        containerColor = colorScheme.surface
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Profile Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { onNavigateToEditProfile() },
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surfaceContainerLow
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        0.5.dp,
                        colorScheme.outlineVariant.copy(alpha = 0.35f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(colorScheme.surfaceContainerHighest)
                                .border(1.dp, colorScheme.outlineVariant.copy(alpha = 0.3f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (hasValidPhoto) {
                                AsyncImage(
                                    model = File(userPhoto!!),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.Person,
                                    contentDescription = null,
                                    tint = colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (userName.isBlank()) "Your Profile" else userName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                ),
                                color = colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tap to edit name and profile photo",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 13.sp,
                                    lineHeight = 16.sp
                                ),
                                color = colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        FilledTonalButton(
                            onClick = onNavigateToEditProfile,
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // General Settings Group
            item {
                SettingsSegmentedGroup {
                    SettingsActionTile(
                        icon = Icons.Rounded.CurrencyExchange,
                        title = "Currency Settings",
                        subtitle = "Current currency: $currencyCode",
                        showDivider = true,
                        onClick = onNavigateToCurrencySelection
                    )
                    SettingsActionTile(
                        icon = Icons.Rounded.Category,
                        title = "Categories",
                        subtitle = "Organize transaction labels & icons",
                        showDivider = true,
                        onClick = onNavigateToCategories
                    )
                    SettingsActionTile(
                        icon = Icons.Rounded.RateReview,
                        title = "Send Feedback",
                        subtitle = "Share your thoughts and ideas",
                        showDivider = false,
                        onClick = onNavigateToFeedback
                    )
                }
            }
        }
    }
}
