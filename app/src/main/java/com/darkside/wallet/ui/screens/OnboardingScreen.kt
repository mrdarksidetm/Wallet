package com.darkside.wallet.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darkside.wallet.utils.BackupRestoreUtil
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 4 })
    val snackbarHostState = remember { SnackbarHostState() }

    var name by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf<String?>(null) }
    var useDynamicTheme by remember { mutableStateOf(true) }

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

    val nextPage = {
        if (pagerState.currentPage == 1 && name.isBlank()) {
            scope.launch { snackbarHostState.showSnackbar("Please enter your name to continue") }
            Unit
        } else if (pagerState.currentPage == 2 && selectedCurrency == null) {
            scope.launch { snackbarHostState.showSnackbar("Please select a currency") }
            Unit
        } else {
            scope.launch {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            }
            Unit
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            MorpheBackground(scrollPosition = pagerState.currentPageOffsetFraction + pagerState.currentPage)

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) { page ->
                when (page) {
                    0 -> WelcomeScreen(onNext = { nextPage() })
                    1 -> OnboardingProfileStep(name = name, onNameChange = { name = it }, onNext = { nextPage() })
                    2 -> CurrencyScreen(selectedCurrency = selectedCurrency, onCurrencySelect = { selectedCurrency = it }, onNext = { nextPage() })
                    3 -> ThemeAndPrivacyScreen(
                        useDynamicTheme = useDynamicTheme,
                        onThemeChange = { useDynamicTheme = it },
                        onRestore = { restoreLauncher.launch(arrayOf("application/zip", "application/octet-stream")) },
                        onFinish = onComplete
                    )
                }
            }

            // Glassmorphic Back Button
            if (pagerState.currentPage > 0 || pagerState.currentPageOffsetFraction > 0.1f) {
                Box(
                    modifier = Modifier
                        .padding(top = 60.dp, start = 20.dp)
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .graphicsLayer {
                            renderEffect = BlurEffect(10f, 10f)
                            alpha = 0.8f
                        }
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f))
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                        .clickable {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        }
    }
}

@Composable
fun MorpheBackground(scrollPosition: Float) {
    val infiniteTransition = rememberInfiniteTransition()
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = Math.PI.toFloat() * 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val primaryColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    val secondaryColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
    val tertiaryColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f)
    val gridColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)

    Box(modifier = Modifier.fillMaxSize()) {
        // Blurred Blobs
        Box(modifier = Modifier.fillMaxSize().graphicsLayer {
            renderEffect = BlurEffect(80f, 80f)
        }) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val parallaxX = scrollPosition * 100f
                
                // Blob 1
                val x1 = (size.width / 2) + sin(time * 0.8f) * (size.width / 3) + parallaxX
                val y1 = (size.height / 2) + cos(time * 1.2f) * (size.height / 4)
                drawCircle(color = primaryColor, radius = 250.dp.toPx(), center = Offset(x1, y1))

                // Blob 2
                val x2 = (size.width / 2) + sin(time * 1.1f * 0.7f) * (size.width / 3) - parallaxX * 0.5f
                val y2 = (size.height / 2) + cos(time * 0.9f * 0.7f) * (size.height / 4)
                drawCircle(color = secondaryColor, radius = 300.dp.toPx(), center = Offset(x2, y2))

                // Blob 3
                val x3 = (size.width / 2) + sin(time * 0.6f * 1.3f) * (size.width / 3) + parallaxX * 1.5f
                val y3 = (size.height / 2) + cos(time * 1.4f * 1.3f) * (size.height / 4)
                drawCircle(color = tertiaryColor, radius = 220.dp.toPx(), center = Offset(x3, y3))
            }
        }

        // 3D Morphing Grid
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val parallaxX = scrollPosition * 100f
            
            val rows = 12
            val cols = 8
            val cellWidth = size.width / cols
            val cellHeight = size.height / rows

            // Horizontal
            for (i in 0..rows) {
                val points = mutableListOf<Offset>()
                for (j in 0..cols) {
                    val x = j * cellWidth
                    val y = i * cellHeight
                    val distToCenter = (Offset(x, y) - center).getDistance()
                    val wave = sin(time + distToCenter * 0.005f - scrollPosition * 2f) * 20f
                    
                    val px = x + cos(time * 0.5f + i) * 10f - parallaxX * 0.2f
                    val py = y + wave + sin(time * 0.8f + j) * 10f
                    points.add(Offset(px, py))
                }
                drawPoints(points, PointMode.Polygon, color = gridColor, strokeWidth = 2f)
            }

            // Vertical
            for (j in 0..cols) {
                val points = mutableListOf<Offset>()
                for (i in 0..rows) {
                    val x = j * cellWidth
                    val y = i * cellHeight
                    val distToCenter = (Offset(x, y) - center).getDistance()
                    val wave = sin(time + distToCenter * 0.005f - scrollPosition * 2f) * 20f
                    
                    val px = x + cos(time * 0.5f + i) * 10f - parallaxX * 0.2f
                    val py = y + wave + sin(time * 0.8f + j) * 10f
                    points.add(Offset(px, py))
                }
                drawPoints(points, PointMode.Polygon, color = gridColor, strokeWidth = 2f)
            }
        }
    }
}

@Composable
fun WelcomeScreen(onNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.weight(2f))
        
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(40.dp))
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        Text("Welcome.", style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.Black, letterSpacing = (-2).sp))
        Spacer(modifier = Modifier.height(16.dp))
        Text("A premium, offline-first personal finance dashboard. Open source and secure.", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Light, color = MaterialTheme.colorScheme.onSurfaceVariant))
        
        Spacer(modifier = Modifier.weight(3f))
        ActionButton("Get Started", onNext)
    }
}

@Composable
fun OnboardingProfileStep(name: String, onNameChange: (String) -> Unit, onNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        Text("Who are you?", style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black, letterSpacing = (-1).sp))
        Spacer(modifier = Modifier.height(8.dp))
        Text("Personalize your local dashboard.", style = MaterialTheme.typography.bodyLarge)
        
        Spacer(modifier = Modifier.weight(1f))
        
        Center {
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .border(4.dp, MaterialTheme.colorScheme.surface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            placeholder = { Text("Display Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            ),
            textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        )
        
        Spacer(modifier = Modifier.weight(2f))
        ActionButton("Continue", onNext, Icons.AutoMirrored.Filled.ArrowForward)
    }
}

val currencies = listOf(
    "USD" to "US Dollar ($)", "EUR" to "Euro (€)", "GBP" to "British Pound (£)", "INR" to "Indian Rupee (₹)",
    "JPY" to "Japanese Yen (¥)", "CAD" to "Japanese Yen (¥)", "AUD" to "Australian Dollar ($)"
)

@Composable
fun CurrencyScreen(selectedCurrency: String?, onCurrencySelect: (String) -> Unit, onNext: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        Text("Language of Money", style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black))
        Spacer(modifier = Modifier.height(8.dp))
        Text("Select your primary currency.", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(32.dp))
        
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            LazyColumn(contentPadding = PaddingValues(16.dp)) {
                items(currencies) { (code, name) ->
                    val isSelected = selectedCurrency == code
                    ListItem(
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                            .clickable { onCurrencySelect(code) },
                        headlineContent = { Text(name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        supportingContent = { Text(code) },
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(name.last().toString().replace(")", ""), color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                            }
                        },
                        trailingContent = {
                            if (isSelected) Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        ActionButton("Continue", onNext, Icons.AutoMirrored.Filled.ArrowForward)
    }
}

@Composable
fun ThemeAndPrivacyScreen(useDynamicTheme: Boolean, onThemeChange: (Boolean) -> Unit, onRestore: () -> Unit, onFinish: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        Text("Your App, Your Rules", style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Black))
        Spacer(modifier = Modifier.height(12.dp))
        Text("Open source and 100% offline. No data ever leaves your device.", style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.weight(1f))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .graphicsLayer { renderEffect = BlurEffect(15f, 15f) }
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), RoundedCornerShape(32.dp))
                .padding(24.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Dynamic Theme", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Switch(checked = useDynamicTheme, onCheckedChange = onThemeChange)
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        Center {
            TextButton(onClick = onRestore) {
                Icon(Icons.Default.Restore, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Restore from Backup", fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        ActionButton("Finish Setup", onFinish, Icons.Default.CheckCircle)
    }
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit, icon: androidx.compose.ui.graphics.vector.ImageVector? = null) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(64.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(text, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        if (icon != null) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(icon, contentDescription = null)
        }
    }
}

@Composable
fun Center(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        content()
    }
}
