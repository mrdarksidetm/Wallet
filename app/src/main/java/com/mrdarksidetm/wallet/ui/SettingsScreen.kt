package com.mrdarksidetm.wallet.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: WalletViewModel,
    onNavigateBack: () -> Unit
) {
    val userName by viewModel.userName.collectAsState()
    val userPhoto by viewModel.userPhotoPath.collectAsState()

    val primaryColor = Color(0xFFD2691E)
    val bgColor = Color(0xFF211811)
    val cardColor = Color(0xFF211811)
    val onBgColor = Color.White
    val onBgSecondary = Color(0xFF94A3B8)

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = { Text("Settings", color = onBgColor, fontSize = 20.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = onBgColor)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor.copy(alpha = 0.8f))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(80.dp)) {
                    Image(
                        painter = rememberAsyncImagePainter(model = userPhoto ?: "https://lh3.googleusercontent.com/aida-public/AB6AXuD0m0ChODA7kWlR5YBZxtWxLJksb_Q08ItwWYOyO6WF-z-mijz-8eVMaeMKE5I_57rJ9UzM0qqgbnp_67NILkeS2kOzxSV26IPrhYXua-sF-ZBnZZasWuHyksQAZPoc5yg-qt3zxyLMzEGXQAcnZfjucvhKDFaRrrX4sqRT45Wv48oTs6SQ4hcgLVlQNJOLD4vf4FSa8usb4RZwv3m5vu-tkp5P02WhKJDUIcDUBnVz8cl3JVYZDW6nrcb_5_Q0CGZz3Xyi6bVarG0"),
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(2.dp, primaryColor, CircleShape)
                    )
                    IconButton(
                        onClick = { /* Edit profile */ },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(primaryColor)
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = userName, color = onBgColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(text = "abhijeet.yadav@example.com", color = onBgSecondary, fontSize = 14.sp)
                }
            }

            Divider(color = primaryColor.copy(alpha = 0.2f))

            // General
            SettingsSectionTitle("General", primaryColor)
            SettingsItem(icon = Icons.Outlined.Palette, title = "App theme", subtitle = "System default", primaryColor = primaryColor)
            SettingsItem(icon = Icons.Outlined.Payments, title = "Currency", subtitle = "INR (₹)", primaryColor = primaryColor)
            SettingsItem(icon = Icons.Outlined.Language, title = "Language", subtitle = "English (US)", primaryColor = primaryColor)

            // Data
            SettingsSectionTitle("Data", primaryColor)
            SettingsItem(icon = Icons.Outlined.CloudUpload, title = "Backup & Restore", subtitle = "Last backup: 2 hours ago", primaryColor = primaryColor)
            SettingsItem(icon = Icons.Filled.ExitToApp, title = "Export Data", subtitle = "CSV, PDF, JSON", primaryColor = primaryColor)

            // Security
            SettingsSectionTitle("Security", primaryColor)
            SettingsSwitchItem(icon = Icons.Outlined.Lock, title = "App Lock", subtitle = "Secure your financial data", checked = true, onCheckedChange = {}, primaryColor = primaryColor)
            SettingsSwitchItem(icon = Icons.Outlined.Fingerprint, title = "Biometric login", subtitle = "Use Fingerprint or Face ID", checked = false, onCheckedChange = {}, primaryColor = primaryColor)

            // About
            SettingsSectionTitle("About", primaryColor)
            SettingsItem(icon = Icons.Outlined.Info, title = "App version", subtitle = "v2.4.0 (Stable)", showChevron = false, primaryColor = primaryColor)
            SettingsItem(icon = Icons.Outlined.Policy, title = "Privacy policy", showChevron = true, chevronIcon = Icons.Outlined.OpenInNew, primaryColor = primaryColor)

            // Logout
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedButton(
                onClick = { /* Sign out */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, primaryColor.copy(alpha = 0.2f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor)
            ) {
                Icon(Icons.Outlined.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out", fontWeight = FontWeight.Bold)
            }

            Text(
                text = "MADE WITH ❤️ FOR PAISA",
                color = onBgSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 24.dp)
            )
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String, primaryColor: Color) {
    Text(
        text = title.uppercase(),
        color = primaryColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    showChevron: Boolean = true,
    chevronIcon: ImageVector = Icons.Filled.KeyboardArrowRight,
    primaryColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(primaryColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = primaryColor)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 16.sp)
            if (subtitle != null) {
                Text(text = subtitle, color = Color(0xFF94A3B8), fontSize = 14.sp)
            }
        }
        if (showChevron) {
            Icon(chevronIcon, contentDescription = null, tint = Color(0xFF94A3B8))
        }
    }
}

@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    primaryColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(primaryColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = primaryColor)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 16.sp)
            Text(text = subtitle, color = Color(0xFF94A3B8), fontSize = 14.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = primaryColor)
        )
    }
}
