package com.darkside.wallet.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.darkside.wallet.utils.FileUtils
import java.io.File
import androidx.compose.material.icons.filled.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: WalletViewModel,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val userName by viewModel.userName.collectAsState()
    val userPhotoPath by viewModel.userPhotoPath.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manage accounts") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Photo
            val hasValidPhoto = com.darkside.wallet.utils.FileUtils.fileExists(userPhotoPath)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                if (hasValidPhoto) {
                    AsyncImage(
                        model = java.io.File(userPhotoPath!!),
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Hi, $userName!",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = { showEditDialog = true },
                shape = CircleShape,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Manage your profile")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Options List
            Surface(
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    ListItem(
                        headlineContent = { Text("Add another account") },
                        leadingContent = { Icon(Icons.Default.Add, contentDescription = null) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier.fillMaxWidth()
                    )
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                    ListItem(
                        headlineContent = { Text("Manage accounts") },
                        leadingContent = { Icon(Icons.Default.ManageAccounts, contentDescription = null) },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    if (showEditDialog) {
        ManageProfileDialog(
            currentName = userName,
            currentPhotoPath = userPhotoPath,
            onDismiss = { showEditDialog = false },
            onSave = { newName, newPhotoUri ->
                viewModel.updateUserName(newName)
                newPhotoUri?.let { uri ->
                    // Handled in dialog to simplify, or could be here
                }
                showEditDialog = false
            },
            viewModel = viewModel
        )
    }
}

@Composable
fun ManageProfileDialog(
    currentName: String,
    currentPhotoPath: String?,
    onDismiss: () -> Unit,
    onSave: (String, Uri?) -> Unit,
    viewModel: WalletViewModel
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(currentName) }
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    
    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        tempPhotoUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .clickable { photoLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        tempPhotoUri != null -> {
                            AsyncImage(
                                model = tempPhotoUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        FileUtils.fileExists(currentPhotoPath) -> {
                            AsyncImage(
                                model = File(currentPhotoPath!!),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        else -> {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                tempPhotoUri?.let { uri ->
                    val permanentPath = FileUtils.saveImagePermanently(context, uri)
                    viewModel.updateUserPhoto(permanentPath)
                }
                onSave(name, tempPhotoUri)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
