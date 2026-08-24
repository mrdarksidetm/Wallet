package com.darkside.wallet.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkside.wallet.ui.components.AppBackButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var feedbackText by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(5) }

    fun sendFeedbackEmail() {
        val subject = "Wallet Feedback (Rating: $rating/5)"
        val body = feedbackText.trim()
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:contact.dsidetm@gmail.com")
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            scope.launch {
                snackbarHostState.showSnackbar("No email app found to send feedback.")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        "Send Feedback",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "How is your experience with Wallet?",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Rating Stars Row
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (i in 1..5) {
                    IconButton(onClick = { rating = i }) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = "Star $i",
                            tint = if (i <= rating) colorScheme.primary else colorScheme.outlineVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = feedbackText,
                onValueChange = { feedbackText = it },
                label = { Text("Your thoughts, ideas or bug reports") },
                placeholder = { Text("Tell us what you love or what we can improve...") },
                minLines = 5,
                maxLines = 10,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { sendFeedbackEmail() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Rounded.Send, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit Feedback", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
