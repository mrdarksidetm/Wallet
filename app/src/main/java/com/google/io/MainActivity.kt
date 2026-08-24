package com.google.io

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Badge
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.expressive.MaterialExpressiveTheme
import androidx.compose.material3.expressive.MotionScheme
import androidx.compose.material3.expressive.SplitButton
import androidx.compose.material3.expressive.SplitButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * GOOGLE I/O ARCHIVE & FUTURE
 * 
 * This application demonstrates the implementation of Material 3 Expressive (M3E) design system.
 * Key highlights:
 * 1. MaterialExpressiveTheme: Utilizing the expressive motion scheme for physics-based springs.
 * 2. SplitButton: Implementing the new action paradigm for primary and secondary interactions.
 * 3. ElevatedCard: Using extra-large corner shapes for a modern, approachable look.
 * 4. Typography: Emphasized type scale for hierarchical clarity.
 */

// --- Data Models ---

/**
 * Represents the status of a Google I/O announcement.
 */
enum class RolloutStatus {
    Stable, // For past releases
    Beta,   // For current/near-term releases
    Upcoming // For future-focused speculation
}

/**
 * Core data class for a Google I/O Keynote entry.
 */
data class Keynote(
    val year: String,
    val date: String,
    val time: String,
    val location: String,
    val watchLink: String,
    val keyFeatures: List<String>,
    val realWorldImpact: String,
    val rolloutStatus: RolloutStatus
)

// --- Mock Data ---

val GoogleIoData = listOf(
    Keynote(
        year = "2026",
        date = "May 19",
        time = "10 AM PT",
        location = "Shoreline Amphitheatre",
        watchLink = "https://io.google/2026",
        keyFeatures = listOf("Gemini 3.5 Pro", "Android XR Audio Glasses", "Gemini Spark (Personal AI Agents)"),
        realWorldImpact = "Transitioned AI from chat to autonomous agents, redefining personal computing via Gemini Spark.",
        rolloutStatus = RolloutStatus.Upcoming
    ),
    Keynote(
        year = "2025",
        date = "May 20",
        time = "10 AM PT",
        location = "Shoreline Amphitheatre",
        watchLink = "https://io.google/2025",
        keyFeatures = listOf("Gemini 2.5", "Project Astra", "AI Overviews in Search"),
        realWorldImpact = "Multi-modal real-time video understanding via Project Astra became a reality for mobile users.",
        rolloutStatus = RolloutStatus.Beta
    ),
    Keynote(
        year = "2014",
        date = "June 25",
        time = "9 AM PT",
        location = "Moscone Center",
        watchLink = "https://io.google/archive/2014",
        keyFeatures = listOf("Material Design Launch", "Android Lollipop", "Android Wear"),
        realWorldImpact = "Standardized UI/UX across all Google platforms, introducing the world to tactile surfaces and bold graphics.",
        rolloutStatus = RolloutStatus.Stable
    )
)

// --- Main Activity ---

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Wrapping the app in the Expressive Theme
            GoogleIoExpressiveApp()
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GoogleIoExpressiveApp() {
    // 1. MaterialExpressiveTheme ensures we use the latest M3E color/type/motion tokens
    // MotionScheme.expressive() provides the spring-based physics for all animations
    MaterialExpressiveTheme(
        motionScheme = MotionScheme.expressive()
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                "Google I/O",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    )
                }
            ) { innerPadding ->
                KeynoteList(
                    keynotes = GoogleIoData,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
fun KeynoteList(
    keynotes: List<Keynote>,
    modifier: Modifier = Modifier
) {
    // LazyColumn leverages the expressive motion scheme for spring-y scroll behavior
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(keynotes) { keynote ->
            KeynoteCard(keynote = keynote)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun KeynoteCard(keynote: Keynote) {
    // 2. ElevatedCard with extraLarge shape as per M3E guidelines
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header Row: Year + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 3. Emphasized Typography: Using displaySmall for the year
                Text(
                    text = keynote.year,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )

                // 4. Status-coded Badges
                val badgeColor = when (keynote.rolloutStatus) {
                    RolloutStatus.Upcoming -> MaterialTheme.colorScheme.tertiary
                    RolloutStatus.Beta -> MaterialTheme.colorScheme.secondary
                    RolloutStatus.Stable -> MaterialTheme.colorScheme.surfaceVariant
                }
                
                Badge(
                    containerColor = badgeColor,
                    contentColor = if (keynote.rolloutStatus == RolloutStatus.Stable) 
                        MaterialTheme.colorScheme.onSurfaceVariant 
                    else MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(
                        keynote.rolloutStatus.name.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Logistics info
            Text(
                text = "${keynote.date} • ${keynote.time}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = keynote.location,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Impact Section
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerLowest,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "Real-world Impact",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = keynote.realWorldImpact,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. SplitButton Implementation
            // A primary action (Watch) paired with a secondary contextual action (Share)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                SplitButton(
                    onPrimaryButtonClick = { /* Navigate to watchLink */ },
                    primaryButton = {
                        SplitButtonDefaults.PrimaryButton(
                            onClick = { /* Primary Click */ },
                        ) {
                            Icon(
                                Icons.Default.PlayArrow, 
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.size(8.dp))
                            Text("Watch Keynote")
                        }
                    },
                    trailingButton = {
                        SplitButtonDefaults.TrailingButton(
                            onClick = { /* Share Click */ },
                        ) {
                            Icon(
                                Icons.Default.Share, 
                                contentDescription = "Share",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                )
            }
        }
    }
}
