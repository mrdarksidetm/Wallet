package com.darkside.wallet.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.darkside.wallet.utils.BackupRestoreUtil
import kotlinx.coroutines.launch

/**
 * Phase 40: Interactive Offline Onboarding
 * 
 * Teaches the user how to use the app fluidly without loading a webview.
 * Uses Material 3 HorizontalPager for native performance.
 * Now includes "Restore from Backup" option.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 3 })
    val snackbarHostState = remember { SnackbarHostState() }

    // Restore Launcher
    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            scope.launch {
                val success = BackupRestoreUtil.restoreFullBackup(context, it)
                if (success) {
                    snackbarHostState.showSnackbar("Restore successful! Please restart the app.")
                } else {
                    snackbarHostState.showSnackbar("Restore failed!")
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = when(page) {
                            0 -> "Welcome to Wallet"
                            1 -> "Track Your Expenses Offline"
                            else -> "Keep Your Privacy Secure"
                        },
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Swipe to learn more.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    if (page == 0) {
                        Spacer(modifier = Modifier.height(32.dp))
                        TextButton(
                            onClick = { restoreLauncher.launch(arrayOf("application/zip")) },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Restore from Backup")
                        }
                    }
                }
            }

            // Pager indicators
            Row(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(8.dp)
                    )
                }
            }

            if (pagerState.currentPage == pagerState.pageCount - 1) {
                Button(
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
                ) {
                    Text("Get Started")
                }
            }
        }
    }
}
