package com.darkside.wallet.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkside.wallet.ui.WalletViewModel
import com.darkside.wallet.ui.components.AppBackButton
import com.darkside.wallet.utils.BackupRestoreUtil
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupRestoreScreen(
    viewModel: WalletViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Zip Backup Launcher
    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip")
    ) { uri ->
        uri?.let {
            scope.launch {
                val success = BackupRestoreUtil.createFullBackup(context, it)
                snackbarHostState.showSnackbar(
                    if (success) "Backup created successfully!" else "Backup failed!"
                )
            }
        }
    }

    // Zip Restore Launcher
    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            scope.launch {
                val success = BackupRestoreUtil.restoreFullBackup(context, it)
                snackbarHostState.showSnackbar(
                    if (success) "Restore successful! Please restart the app." else "Restore failed!"
                )
            }
        }
    }

    // CSV Import Launcher
    val csvImportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            scope.launch {
                viewModel.importTransactions(context, it) { success ->
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            if (success) "CSV imported successfully!" else "CSV import failed!"
                        )
                    }
                }
            }
        }
    }

    // JSON Import Launcher
    val jsonImportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            scope.launch {
                viewModel.importData(context, it) { success ->
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            if (success) "JSON data imported successfully!" else "JSON import failed!"
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            MediumTopAppBar(
                title = {
                    Text(
                        "Backup & Restore",
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
            // Database Archives Section
            item {
                Column {
                    SectionHeaderTitle("DATABASE ARCHIVES")
                    Spacer(modifier = Modifier.height(10.dp))
                    SettingsSegmentedGroup {
                        SettingsActionTile(
                            icon = Icons.Rounded.Backup,
                            title = "Backup Database",
                            subtitle = "Complete archive of data, settings & attachments (.zip)",
                            showDivider = true,
                            onClick = {
                                backupLauncher.launch("wallet_backup_${System.currentTimeMillis()}.zip")
                            }
                        )
                        SettingsActionTile(
                            icon = Icons.Rounded.Restore,
                            title = "Restore Backup",
                            subtitle = "Restore all data from a .zip backup archive",
                            showDivider = false,
                            onClick = {
                                restoreLauncher.launch(arrayOf("application/zip", "application/octet-stream"))
                            }
                        )
                    }
                }
            }

            // CSV Spreadsheets Section
            item {
                Column {
                    SectionHeaderTitle("CSV SPREADSHEETS")
                    Spacer(modifier = Modifier.height(10.dp))
                    SettingsSegmentedGroup {
                        SettingsActionTile(
                            icon = Icons.Rounded.UploadFile,
                            title = "Export to CSV",
                            subtitle = "Export transactions to a CSV spreadsheet",
                            showDivider = true,
                            onClick = {
                                viewModel.exportTransactions(context) { success ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            if (success) "Exported CSV successfully!" else "CSV export failed!"
                                        )
                                    }
                                }
                            }
                        )
                        SettingsActionTile(
                            icon = Icons.Rounded.DownloadForOffline,
                            title = "Import from CSV",
                            subtitle = "Import transactions from an existing CSV file",
                            showDivider = false,
                            onClick = {
                                csvImportLauncher.launch(arrayOf("text/comma-separated-values", "text/csv", "application/csv"))
                            }
                        )
                    }
                }
            }

            // JSON Structured Data Section
            item {
                Column {
                    SectionHeaderTitle("JSON STRUCTURED DATA")
                    Spacer(modifier = Modifier.height(10.dp))
                    SettingsSegmentedGroup {
                        SettingsActionTile(
                            icon = Icons.Rounded.Storage,
                            title = "Export to JSON",
                            subtitle = "Backup your transactions in portable JSON format",
                            showDivider = true,
                            onClick = {
                                viewModel.exportData(context) { success ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            if (success) "Exported JSON successfully!" else "JSON export failed!"
                                        )
                                    }
                                }
                            }
                        )
                        SettingsActionTile(
                            icon = Icons.Rounded.FolderOpen,
                            title = "Import from JSON",
                            subtitle = "Restore transactions from a JSON data file",
                            showDivider = false,
                            onClick = {
                                jsonImportLauncher.launch(arrayOf("application/json"))
                            }
                        )
                    }
                }
            }
        }
    }
}
