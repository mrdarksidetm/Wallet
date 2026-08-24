package com.darkside.wallet.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkside.wallet.ui.components.AppBackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme

    val policySections = listOf(
        "1. Philosophy: Absolute Privacy by Design" to "Wallet is built from the ground up as a strictly offline-first, local-only personal finance management system. Our core architectural principle is simple: Your financial data belongs exclusively to you and must never leave your physical device without your explicit command.",
        "2. Information Storage & Processing" to "• Local Database Storage: All transactions, accounts, account balances, categorization rules, budgets, savings goals, peer-to-peer loan ledgers, and recurring subscriptions are stored exclusively on your device inside an embedded, high-performance Room Database.\n\n• Zero Remote Servers: We do not operate cloud database backends, analytics collection servers, or intermediary sync relays for user financial data.\n\n• Zero Telemetry & Tracking: The application contains no tracking pixels, ad networks, third-party analytics SDKs, or behavioral profiling mechanisms.",
        "3. Biometric & Device Security" to "• Biometric Lock Mechanism: When biometric authentication is enabled, Wallet utilizes your device's native hardware security layer (Android BiometricPrompt API).\n\n• Zero Biometric Access: The app never accesses, reads, collects, or transmits your actual fingerprint, facial geometry, or passcode data. Authentication verification is handled entirely within your operating system's Trusted Execution Environment (TEE).",
        "4. Data Backup, Export & Destruction" to "• User-Initiated Exports: You may export your financial data at any time in structured JSON, CSV spreadsheets, or full binary database archives (.zip). These files are generated locally and stored only in the directory or destination you select.\n\n• Data Shredder / Factory Reset: Wallet provides an integrated Data Shredder utility designed for complete on-device data destruction. Triggering a factory reset permanently purges all Room tables, secure preferences, and localized cache from disk storage.",
        "5. Diagnostics & Error Collector (LogCat)" to "• On-Demand Diagnostics: The application includes an optional diagnostic log collector designed to capture runtime crash logs and framework events for debugging purposes.\n\n• Local Volatile Storage: Diagnostic logs are stored only in temporary, volatile memory on your device. They are never transmitted automatically to any remote endpoint.\n\n• User Control: The Error Collector is deactivated by default and can be viewed, copied, cleared, or disabled at your discretion.",
        "6. Open Source Transparency & Auditing" to "Wallet is completely open-source. The entire application source code, database schemas, and cryptographic dependencies are publicly accessible and verifiable on GitHub."
    )

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        "Privacy Policy",
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
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerLow),
                    border = androidx.compose.foundation.BorderStroke(1.dp, colorScheme.outlineVariant.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Last updated: August 2026",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurfaceVariant
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        policySections.forEachIndexed { index, (heading, body) ->
                            Text(
                                text = heading,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = colorScheme.primary,
                                    letterSpacing = (-0.2).sp
                                )
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = body,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = colorScheme.onSurfaceVariant,
                                    lineHeight = 22.sp
                                )
                            )
                            if (index < policySections.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 16.dp),
                                    thickness = 0.5.dp,
                                    color = colorScheme.outlineVariant.copy(alpha = 0.35f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
