package com.darkside.wallet.ui.settings

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkside.wallet.ui.WalletViewModel
import com.darkside.wallet.ui.components.AppBackButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySecurityScreen(
    viewModel: WalletViewModel,
    onBack: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onNavigateToTermsOfUse: () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var isBiometricEnabled by remember {
        mutableStateOf(context.getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE).getBoolean("biometric_enabled", false))
    }

    val canCheckBiometrics = remember {
        val biometricManager = BiometricManager.from(context)
        biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS
    }

    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        "Privacy & Security",
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
            // Security Controls Group
            item {
                SettingsSegmentedGroup {
                    SettingsActionTile(
                        icon = if (isBiometricEnabled) Icons.Rounded.Fingerprint else Icons.Rounded.Lock,
                        title = "Biometric Lock",
                        subtitle = if (canCheckBiometrics) {
                            if (isBiometricEnabled) "App is protected by biometric lock"
                            else "Require authentication on launch"
                        } else {
                            "Biometrics not supported on this device"
                        },
                        showDivider = true,
                        trailing = {
                            Switch(
                                checked = isBiometricEnabled,
                                enabled = canCheckBiometrics,
                                onCheckedChange = { checked ->
                                    isBiometricEnabled = checked
                                    context.getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE)
                                        .edit().putBoolean("biometric_enabled", checked).apply()
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            if (checked) "Biometric lock enabled" else "Biometric lock disabled"
                                        )
                                    }
                                }
                            )
                        }
                    )

                    SettingsActionTile(
                        icon = Icons.Rounded.DeleteForever,
                        title = "Factory Reset",
                        subtitle = "Permanently wipe all data and reset app",
                        isDestructive = true,
                        showDivider = false,
                        onClick = { showResetDialog = true }
                    )
                }
            }

            // Privacy & Policy Group
            item {
                SettingsSegmentedGroup {
                    SettingsActionTile(
                        icon = Icons.Rounded.Shield,
                        title = "Privacy Policy",
                        subtitle = "Offline-first data philosophy",
                        showDivider = true,
                        onClick = onNavigateToPrivacyPolicy
                    )
                    SettingsActionTile(
                        icon = Icons.Rounded.Policy,
                        title = "Terms of Use",
                        subtitle = "Open source usage terms and conditions",
                        showDivider = false,
                        onClick = onNavigateToTermsOfUse
                    )
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            shape = RoundedCornerShape(28.dp),
            icon = {
                Icon(
                    Icons.Rounded.Warning,
                    contentDescription = null,
                    tint = colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    "Factory Reset?",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    "This will permanently delete all transactions, accounts, categories, and settings. This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        viewModel.factoryReset { success ->
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    if (success) "App data has been completely erased."
                                    else "Failed to reset data."
                                )
                            }
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = colorScheme.error)
                ) {
                    Text("Reset Everything", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
