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
fun TermsOfUseScreen(onBack: () -> Unit) {
    val colorScheme = MaterialTheme.colorScheme

    val termsSections = listOf(
        "1. Acceptance of Terms" to "By downloading, installing, or using Wallet, you agree to be bound by these Terms of Use and our open-source software license agreements.",
        "2. Open Source Licensing" to "Wallet is distributed as free and open-source software under the Apache License 2.0. You are free to inspect, modify, fork, and distribute the codebase in accordance with the terms of the applicable licenses.",
        "3. Disclaimer of Financial Advice" to "Wallet is an offline personal budgeting tool designed for individual record-keeping and tracking. It does not provide financial, investment, legal, or tax advice. You are solely responsible for your financial decisions and calculations.",
        "4. Data Responsibility & Backup" to "Because Wallet is strictly offline-first and operates without central servers, you are responsible for maintaining your own data backups using the integrated export utilities (CSV, JSON, .zip archives).",
        "5. Limitation of Liability" to "The software is provided 'as is', without warranty of any kind, express or implied. In no event shall the authors or copyright holders be liable for any claim, damages, or other liability arising from the use of the application."
    )

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        "Terms of Use",
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

                        termsSections.forEachIndexed { index, (heading, body) ->
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
                            if (index < termsSections.size - 1) {
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
