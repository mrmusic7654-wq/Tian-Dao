package com.example.ui.screens

import android.text.format.DateFormat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.apps.AppInfo
import com.example.ui.LauncherViewModel
import com.example.ui.ScreenState
import com.example.ui.components.YinYangCore
import java.util.Calendar
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutQuad
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Contrast
import java.util.Date
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val dockApps by viewModel.dockApps.collectAsState()
    val currentQuote by viewModel.currentQuote.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val rotationDuration by viewModel.rotationDuration.collectAsState()
    val yinYangBalance by viewModel.yinYangBalance.collectAsState()

    // Real-time clock state
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        while (true) {
            val calendar = Calendar.getInstance()
            val timeFormat = DateFormat.getTimeFormat(context)
            val dateFormat = DateFormat.getDateFormat(context)
            currentTime = timeFormat.format(calendar.time)
            currentDate = dateFormat.format(calendar.time)
            delay(1000)
        }
    }

    // Dynamic Yin-Yang gradient background modulated by slider balance
    val bgGradient = if (isDarkTheme) {
        val darkColor = Color(0xFF070709)
        val midColor = Color(
            red = (0x0F * (0.5f + yinYangBalance * 0.5f) / 255f).coerceIn(0f, 1f),
            green = (0x0F * (0.5f + yinYangBalance * 0.5f) / 255f).coerceIn(0f, 1f),
            blue = (0x14 * (0.5f + yinYangBalance * 0.5f) / 255f).coerceIn(0f, 1f)
        )
        val lightColor = Color(
            red = (0x1B * (0.5f + yinYangBalance * 0.5f) / 255f).coerceIn(0f, 1f),
            green = (0x1B * (0.5f + yinYangBalance * 0.5f) / 255f).coerceIn(0f, 1f),
            blue = (0x22 * (0.5f + yinYangBalance * 0.5f) / 255f).coerceIn(0f, 1f)
        )
        Brush.verticalGradient(colors = listOf(darkColor, midColor, lightColor))
    } else {
        val lightColor = Color(0xFFF9FAFC)
        val midColor = Color(
            red = (0xEC - (yinYangBalance * 15f)) / 255f,
            green = (0xEF - (yinYangBalance * 15f)) / 255f,
            blue = (0xF4 - (yinYangBalance * 15f)) / 255f
        )
        val darkColor = Color(
            red = (0xE2 - (yinYangBalance * 20f)) / 255f,
            green = (0xE8 - (yinYangBalance * 20f)) / 255f,
            blue = (0xF0 - (yinYangBalance * 20f)) / 255f
        )
        Brush.verticalGradient(colors = listOf(lightColor, midColor, darkColor))
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        val parentHeight = maxHeight
        val collapsedHeight = 110.dp
        val expandedHeight = parentHeight * 0.85f

        var isMenuExpanded by remember { mutableStateOf(false) }
        val menuHeight by animateDpAsState(
            targetValue = if (isMenuExpanded) expandedHeight else collapsedHeight,
            animationSpec = spring(stiffness = Spring.StiffnessLow),
            label = "menu_height"
        )

        // Cosmic Circuit Background layer (centered around exact midpoint of constraints)
        CosmicCircuitBackground(modifier = Modifier.fillMaxSize())

        // Top Bar: Balanced cosmological status and Settings shortcut
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "TIAN DAO CORE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    letterSpacing = 2.sp
                )
                Text(
                    text = if (isDarkTheme) "Yin Mode • Resting Balance" else "Yang Mode • Radiant Motion",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Quick theme trigger
                IconButton(
                    onClick = { viewModel.toggleTheme() },
                    modifier = Modifier.testTag("theme_toggle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Balance energies",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = { viewModel.setScreen(ScreenState.SETTINGS) },
                    modifier = Modifier.testTag("settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Launcher settings",
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Clock at the Top Center
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 85.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentTime,
                fontSize = 42.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.SansSerif,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Text(
                text = currentDate,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }

        // Yin-Yang Logo and Animated Orbital System placed EXACTLY at the geometric center
        CenteredCosmicControlSystem(
            viewModel = viewModel,
            isGenerating = isGenerating,
            isDarkTheme = isDarkTheme,
            rotationDuration = rotationDuration,
            modifier = Modifier.align(Alignment.Center)
        )

        // Wisdom quote card placed at bottom center, shifted up so it does not conflict with the bottom slider
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 140.dp)
                .fillMaxWidth(0.9f),
            contentAlignment = Alignment.Center
        ) {
            Card(
                onClick = { viewModel.rotateQuote() },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.35f + (yinYangBalance * 0.15f))
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wisdom_quote_card"),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "“ $currentQuote ”",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                        textAlign = TextAlign.Center,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight.Normal,
                        fontFamily = FontFamily.Serif
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap Core for Gemini AI • Tap Card to Reflect",
                        fontSize = 8.5.sp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // Sliding App Drawer Panel (sliding style app menu)
        SlidingAppMenu(
            isExpanded = false, // Always collapsed to keep favorites dock clean
            onToggle = { viewModel.setScreen(ScreenState.APP_DRAWER) },
            viewModel = viewModel,
            modifier = Modifier.align(Alignment.BottomCenter),
            height = menuHeight,
            maxHeight = expandedHeight
        )
    }
}

@Composable
fun SlidingAppMenu(
    isExpanded: Boolean,
    onToggle: () -> Unit,
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp,
    maxHeight: androidx.compose.ui.unit.Dp
) {
    val filteredApps by viewModel.filteredApps.collectAsState()
    val searchQuery by viewModel.appSearchQuery.collectAsState()
    val dockApps by viewModel.dockApps.collectAsState()

    // Sort apps alphabetically by label
    val sortedApps = remember(filteredApps) {
        filteredApps.sortedBy { it.label.lowercase() }
    }

    // Group apps alphabetically by first letter
    val groupedApps = remember(sortedApps) {
        sortedApps.groupBy { it.label.firstOrNull()?.uppercaseChar() ?: '#' }
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0C0C0E).copy(alpha = 0.96f) // Glassmorphic deep dark slate
        ),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFD4AF37).copy(alpha = 0.5f), // Gold circuit border on top
                    Color(0xFFC0C0C0).copy(alpha = 0.1f)  // Silver blend down
                )
            )
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .testTag("sliding_app_menu"),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Slider/Drag Handle with circuit nodes
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() }
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Modern Circuit Slide Handle
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // Left Node
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFFD4AF37), shape = CircleShape)
                        )
                        // Connecting gold line
                        Spacer(
                            modifier = Modifier
                                .width(30.dp)
                                .height(2.dp)
                                .background(Color(0xFFD4AF37))
                        )
                        // Middle arrow icon
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                            contentDescription = if (isExpanded) "Collapse" else "Expand",
                            tint = Color(0xFFD4AF37),
                            modifier = Modifier.size(20.dp)
                        )
                        // Connecting silver line
                        Spacer(
                            modifier = Modifier
                                .width(30.dp)
                                .height(2.dp)
                                .background(Color(0xFFC0C0C0))
                        )
                        // Right Node
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(Color(0xFFC0C0C0), shape = CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isExpanded) "SLIDE DOWN TO COLLAPSE" else "TAP OR SLIDE UP FOR APPS",
                        fontSize = 9.sp,
                        color = Color(0xFFC0C0C0).copy(alpha = 0.5f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (!isExpanded) {
                // Collapsed State: Show the favorites/dock apps inside the sliding panel
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    if (dockApps.isEmpty()) {
                        Text(
                            text = "Favorites Dock Empty • Expand to explore",
                            color = Color(0xFFC0C0C0).copy(alpha = 0.4f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Light
                        )
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(dockApps) { app ->
                                DockAppIcon(
                                    app = app,
                                    onClick = { viewModel.launchApp(app.packageName, app.label) }
                                )
                            }
                        }
                    }
                }
            } else {
                // Expanded State: Show full search field and alphabetical list
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setAppSearchQuery(it) },
                    placeholder = { Text("Search creations...", color = Color(0xFFC0C0C0).copy(alpha = 0.4f)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search icon",
                            tint = Color(0xFFD4AF37)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setAppSearchQuery("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear search",
                                    tint = Color(0xFFC0C0C0)
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFD4AF37),
                        unfocusedBorderColor = Color(0xFFC0C0C0).copy(alpha = 0.2f),
                        focusedContainerColor = Color(0xFF141418),
                        unfocusedContainerColor = Color(0xFF141418).copy(alpha = 0.6f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("app_search_field")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // List style in alphabetical order with sticky/grouped letter headers
                if (sortedApps.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No apps match search term.",
                            color = Color(0xFFC0C0C0).copy(alpha = 0.5f),
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .testTag("alphabetical_apps_list"),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        groupedApps.forEach { (letter, appsInGroup) ->
                            item {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                ) {
                                    // High-tech circular gold or silver index shield
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(
                                                color = Color(0xFF141416),
                                                shape = CircleShape
                                            )
                                            .border(
                                                width = 1.dp,
                                                color = if (letter.code % 2 == 0) Color(0xFFD4AF37) else Color(0xFFC0C0C0),
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = letter.toString(),
                                            color = if (letter.code % 2 == 0) Color(0xFFD4AF37) else Color(0xFFC0C0C0),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    // Accent circuit line extending right from the letter
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(1.dp)
                                            .background(
                                                brush = Brush.horizontalGradient(
                                                    colors = listOf(
                                                        if (letter.code % 2 == 0) Color(0xFFD4AF37).copy(alpha = 0.4f) else Color(0xFFC0C0C0).copy(alpha = 0.4f),
                                                        Color.Transparent
                                                    )
                                                )
                                            )
                                    )
                                }
                            }
                            items(appsInGroup) { app ->
                                AlphabeticalAppRow(
                                    app = app,
                                    onClick = {
                                        viewModel.launchApp(app.packageName, app.label)
                                        // Auto collapse on app launch
                                        onToggle()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlphabeticalAppRow(
    app: AppInfo,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF141416).copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (Math.abs(app.label.hashCode()) % 2 == 0) Color(0xFFD4AF37).copy(alpha = 0.15f) else Color(0xFFC0C0C0).copy(alpha = 0.15f)
        ),
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("app_row_${app.packageName}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Elegant Metallic Circuit Coin Icon
            GameStyleIcon(app = app, size = 42.dp)
            
            Spacer(modifier = Modifier.width(14.dp))
            
            // App labels
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = app.label,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = app.packageName,
                    fontSize = 10.sp,
                    color = Color(0xFFC0C0C0).copy(alpha = 0.5f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Interactive glowing runic launch trigger or play arrow on far right
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Launch",
                tint = if (Math.abs(app.label.hashCode()) % 2 == 0) Color(0xFFD4AF37) else Color(0xFFC0C0C0),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun DockAppIcon(
    app: AppInfo,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(60.dp)
            .fillMaxHeight()
            .clip(CircleShape)
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        GameStyleIcon(app = app, size = 44.dp)
    }
}

@Composable
private fun GameStyleIcon(
    app: AppInfo,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 48.dp
) {
    val labelLower = app.label.lowercase()
    val pkgLower = app.packageName.lowercase()
    
    // Map apps to standard Material Icons for our high-tech circuit icon pack
    val mappedIcon = when {
        labelLower.contains("chrome") || labelLower.contains("browser") || labelLower.contains("internet") || labelLower.contains("web") -> Icons.Default.Search
        labelLower.contains("setting") || labelLower.contains("config") || pkgLower.contains("settings") -> Icons.Default.Settings
        labelLower.contains("chat") || labelLower.contains("messag") || labelLower.contains("whatsapp") || labelLower.contains("telegr") || labelLower.contains("sms") -> Icons.Default.Send
        labelLower.contains("mail") || labelLower.contains("gmail") || labelLower.contains("email") -> Icons.Default.Email
        labelLower.contains("phone") || labelLower.contains("dialer") || labelLower.contains("call") -> Icons.Default.Phone
        labelLower.contains("camera") -> Icons.Default.Star
        labelLower.contains("music") || labelLower.contains("play") || labelLower.contains("video") || labelLower.contains("youtube") -> Icons.Default.PlayArrow
        labelLower.contains("note") || labelLower.contains("memo") || labelLower.contains("todo") || labelLower.contains("edit") -> Icons.Default.Edit
        labelLower.contains("history") || labelLower.contains("chronicle") || labelLower.contains("log") -> Icons.Default.History
        else -> null
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
    ) {
        // Celestial outer ring / runic circle
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
                .border(
                    width = 2.dp,
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color(0xFFD4AF37), // Gold circuit sweep
                            Color(0xFFC0C0C0), // Silver circuit sweep
                            Color(0xFFD4AF37).copy(alpha = 0.3f),
                            Color(0xFFD4AF37)
                        )
                    ),
                    shape = CircleShape
                )
                .padding(4.dp)
        ) {
            if (mappedIcon != null) {
                // Customized High-Tech Circuit Coin Icon
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF141416),
                                    Color(0xFF09090A)
                                )
                            ),
                            shape = CircleShape
                        )
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val iconColor = if (Math.abs(app.label.hashCode()) % 2 == 0) Color(0xFFD4AF37) else Color(0xFFC0C0C0)
                    Icon(
                        imageVector = mappedIcon,
                        contentDescription = app.label,
                        tint = iconColor,
                        modifier = Modifier.fillMaxSize(0.7f)
                    )
                }
            } else {
                val imageBitmap = app.imageBitmap
                if (imageBitmap != null) {
                    androidx.compose.foundation.Image(
                        bitmap = imageBitmap,
                        contentDescription = app.label,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF141416),
                                        Color(0xFF09090A)
                                    )
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = app.label.take(1),
                            fontWeight = FontWeight.Bold,
                            color = if (Math.abs(app.label.hashCode()) % 2 == 0) Color(0xFFD4AF37) else Color(0xFFC0C0C0),
                            fontSize = (size.value * 0.4f).sp
                        )
                    }
                }
            }
        }

        // Mini cosmic celestial badge on top-right of the item
        Box(
            modifier = Modifier
                .size((size.value * 0.38f).dp)
                .align(Alignment.TopEnd)
                .background(Color(0xFF0A0A0C), shape = CircleShape)
                .border(1.dp, if (Math.abs(app.label.hashCode()) % 2 == 0) Color(0xFFD4AF37) else Color(0xFFC0C0C0), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            val runes = listOf("☯", "巽", "震", "乾", "坤", "坎", "离", "艮", "兑")
            val rune = runes[Math.abs(app.label.hashCode()) % runes.size]
            Text(
                text = rune,
                color = if (Math.abs(app.label.hashCode()) % 2 == 0) Color(0xFFD4AF37) else Color(0xFFC0C0C0),
                fontSize = (size.value * 0.20f).sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CosmicCircuitBackground(modifier: Modifier = Modifier) {
    val goldColor = Color(0xFFD4AF37)
    val silverColor = Color(0xFFC0C0C0)
    
    val infiniteTransition = rememberInfiniteTransition(label = "CircuitBackground")
    val pulsePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulsePhase"
    )

    // Breathing intensity for glows
    val breathingAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BreathingAlpha"
    )

    // We draw on Canvas to make it incredibly detailed and responsive to all screen sizes
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        val flowPhaseForward = pulsePhase * 450f
        val flowPhaseBackward = -pulsePhase * 450f
        
        // Dynamic path effects for flowing circuit energy pulses
        val pulseEffectLeft = PathEffect.dashPathEffect(floatArrayOf(50f, 180f), flowPhaseForward)
        val pulseEffectRight = PathEffect.dashPathEffect(floatArrayOf(50f, 180f), flowPhaseBackward)
        val pulseEffectFrame = PathEffect.dashPathEffect(floatArrayOf(60f, 140f), flowPhaseForward)
        val pulseEffectBraid1 = PathEffect.dashPathEffect(floatArrayOf(50f, 150f), flowPhaseBackward)
        val pulseEffectBraid2 = PathEffect.dashPathEffect(floatArrayOf(50f, 150f), flowPhaseForward)
        
        // 1. DENSE SIDE COLUMNS (Motherboard background traces)
        // Draw 3 layers of vertical step-circuit lines on the left and right margins
        val sideTracesCount = 4
        for (i in 0 until sideTracesCount) {
            val offsetLeft = 16.dp.toPx() + i * 14.dp.toPx()
            val offsetRight = w - 16.dp.toPx() - i * 14.dp.toPx()
            val isGold = i % 2 == 0
            val traceColor = (if (isGold) goldColor else silverColor).copy(alpha = 0.12f)
            val highlightColor = if (isGold) goldColor else silverColor
            val pulseColor = highlightColor.copy(alpha = 0.45f)
            
            // Left track with 45-degree bypass bends around the central core (cy)
            val pathLeft = androidx.compose.ui.graphics.Path().apply {
                moveTo(offsetLeft, 0f)
                lineTo(offsetLeft, cy - 140.dp.toPx() - i * 10.dp.toPx())
                // Bend outward/inward to frame the center beautifully
                lineTo(offsetLeft - 8.dp.toPx(), cy - 110.dp.toPx() - i * 10.dp.toPx())
                lineTo(offsetLeft - 8.dp.toPx(), cy + 110.dp.toPx() + i * 10.dp.toPx())
                lineTo(offsetLeft, cy + 140.dp.toPx() + i * 10.dp.toPx())
                lineTo(offsetLeft, h)
            }
            drawPath(path = pathLeft, color = traceColor, style = Stroke(width = 1.2.dp.toPx()))
            // Flow pulse left
            drawPath(
                path = pathLeft,
                color = pulseColor,
                style = Stroke(width = 1.6.dp.toPx(), pathEffect = pulseEffectLeft)
            )

            // Right track (mirrored)
            val pathRight = androidx.compose.ui.graphics.Path().apply {
                moveTo(offsetRight, 0f)
                lineTo(offsetRight, cy - 140.dp.toPx() - i * 10.dp.toPx())
                lineTo(offsetRight + 8.dp.toPx(), cy - 110.dp.toPx() - i * 10.dp.toPx())
                lineTo(offsetRight + 8.dp.toPx(), cy + 110.dp.toPx() + i * 10.dp.toPx())
                lineTo(offsetRight, cy + 140.dp.toPx() + i * 10.dp.toPx())
                lineTo(offsetRight, h)
            }
            drawPath(path = pathRight, color = traceColor, style = Stroke(width = 1.2.dp.toPx()))
            // Flow pulse right
            drawPath(
                path = pathRight,
                color = pulseColor,
                style = Stroke(width = 1.6.dp.toPx(), pathEffect = pulseEffectRight)
            )
            
            // Draw random tiny glowing nodes on some side tracks to mimic active state
            if (i == 1) {
                val baseHighlight = (if (isGold) goldColor else Color.White).copy(alpha = 0.25f + 0.3f * breathingAlpha)
                drawCircle(color = baseHighlight, radius = 2.5.dp.toPx(), center = Offset(offsetLeft - 8.dp.toPx(), cy - 40.dp.toPx()))
                drawCircle(color = baseHighlight, radius = 2.5.dp.toPx(), center = Offset(offsetRight + 8.dp.toPx(), cy + 50.dp.toPx()))
            }
        }

        // 2. GLOWING CLOCK FRAME TRACES (framing the Clock and Date beautifully)
        val clockFrameYBottom = 162.dp.toPx()
        val clockFrameYTop = 80.dp.toPx()
        val frameWidthHalf = 95.dp.toPx()
        
        // --- Bottom frame line (bracket underneath the clock) ---
        val bottomFramePath = androidx.compose.ui.graphics.Path().apply {
            // Left node bend
            moveTo(cx - frameWidthHalf - 15.dp.toPx(), clockFrameYBottom - 15.dp.toPx())
            lineTo(cx - frameWidthHalf, clockFrameYBottom)
            // Horizontal straight
            lineTo(cx + frameWidthHalf, clockFrameYBottom)
            // Right node bend
            lineTo(cx + frameWidthHalf + 15.dp.toPx(), clockFrameYBottom - 15.dp.toPx())
        }
        drawPath(path = bottomFramePath, color = goldColor.copy(alpha = 0.35f), style = Stroke(width = 1.8.dp.toPx()))
        drawPath(
            path = bottomFramePath,
            color = goldColor.copy(alpha = 0.75f),
            style = Stroke(width = 2.2.dp.toPx(), pathEffect = pulseEffectFrame)
        )
        
        // Glow pads on bottom frame ends with breathing glow aura
        drawCircle(
            brush = Brush.radialGradient(colors = listOf(goldColor.copy(alpha = breathingAlpha * 0.45f), Color.Transparent)),
            radius = (7.dp + 4.dp * breathingAlpha).toPx(),
            center = Offset(cx - frameWidthHalf - 15.dp.toPx(), clockFrameYBottom - 15.dp.toPx())
        )
        drawCircle(color = goldColor, radius = 3.dp.toPx(), center = Offset(cx - frameWidthHalf - 15.dp.toPx(), clockFrameYBottom - 15.dp.toPx()))
        drawCircle(color = Color.White, radius = 1.2.dp.toPx(), center = Offset(cx - frameWidthHalf - 15.dp.toPx(), clockFrameYBottom - 15.dp.toPx()))

        drawCircle(
            brush = Brush.radialGradient(colors = listOf(goldColor.copy(alpha = breathingAlpha * 0.45f), Color.Transparent)),
            radius = (7.dp + 4.dp * breathingAlpha).toPx(),
            center = Offset(cx + frameWidthHalf + 15.dp.toPx(), clockFrameYBottom - 15.dp.toPx())
        )
        drawCircle(color = goldColor, radius = 3.dp.toPx(), center = Offset(cx + frameWidthHalf + 15.dp.toPx(), clockFrameYBottom - 15.dp.toPx()))
        drawCircle(color = Color.White, radius = 1.2.dp.toPx(), center = Offset(cx + frameWidthHalf + 15.dp.toPx(), clockFrameYBottom - 15.dp.toPx()))

        // --- Top frame line (bracket above the clock) ---
        val topFramePath = androidx.compose.ui.graphics.Path().apply {
            moveTo(cx - frameWidthHalf + 15.dp.toPx(), clockFrameYTop + 15.dp.toPx())
            lineTo(cx - frameWidthHalf + 30.dp.toPx(), clockFrameYTop)
            lineTo(cx + frameWidthHalf - 30.dp.toPx(), clockFrameYTop)
            lineTo(cx + frameWidthHalf - 15.dp.toPx(), clockFrameYTop + 15.dp.toPx())
        }
        drawPath(path = topFramePath, color = silverColor.copy(alpha = 0.25f), style = Stroke(width = 1.5.dp.toPx()))
        drawPath(
            path = topFramePath,
            color = silverColor.copy(alpha = 0.65f),
            style = Stroke(width = 1.8.dp.toPx(), pathEffect = pulseEffectFrame)
        )
        
        drawCircle(color = silverColor, radius = 2.5.dp.toPx(), center = Offset(cx - frameWidthHalf + 15.dp.toPx(), clockFrameYTop + 15.dp.toPx()))
        drawCircle(color = silverColor, radius = 2.5.dp.toPx(), center = Offset(cx + frameWidthHalf - 15.dp.toPx(), clockFrameYTop + 15.dp.toPx()))


        // 3. TOP BUS LANES (Branching traces rising from central sphere)
        // Mirrored vertical branches that split horizontally
        val topYStart = cy - 85.dp.toPx()
        val branchingAngles = listOf(-30.dp.toPx(), -15.dp.toPx(), 15.dp.toPx(), 30.dp.toPx())
        branchingAngles.forEachIndexed { index, startOffsetX ->
            val isLeft = index < 2
            val col = if (isLeft) goldColor else silverColor
            val branchPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(cx + startOffsetX, topYStart)
                lineTo(cx + startOffsetX, topYStart - 40.dp.toPx())
                // Split outwards
                val splitX = if (isLeft) cx + startOffsetX - 45.dp.toPx() else cx + startOffsetX + 45.dp.toPx()
                val splitY = topYStart - 75.dp.toPx()
                lineTo(splitX, splitY)
                // Continue vertically up
                lineTo(splitX, splitY - 50.dp.toPx())
            }
            drawPath(path = branchPath, color = col.copy(alpha = 0.18f), style = Stroke(width = 1.5.dp.toPx()))
            drawPath(
                path = branchPath,
                color = col.copy(alpha = 0.55f),
                style = Stroke(width = 1.8.dp.toPx(), pathEffect = if (isLeft) pulseEffectLeft else pulseEffectRight)
            )
            
            // Terminate branches with custom pads
            val endX = if (isLeft) cx + startOffsetX - 45.dp.toPx() else cx + startOffsetX + 45.dp.toPx()
            val endY = topYStart - 125.dp.toPx()
            drawCircle(color = col.copy(alpha = 0.25f + 0.15f * breathingAlpha), radius = 4.dp.toPx(), center = Offset(endX, endY), style = Stroke(width = 1.2.dp.toPx()))
            drawCircle(color = col, radius = 1.5.dp.toPx(), center = Offset(endX, endY))
        }


        // 4. BOTTOM BRAIDED BUS (The spectacular DNA-helix/infinity braid flow lines)
        // These start right below the central YinYang core and crossover in a stunning pattern, descending straight down
        val bottomYStart = cy + 85.dp.toPx()
        val crossYMid = cy + 130.dp.toPx()
        val crossYEnd = cy + 165.dp.toPx()
        
        // Path 1 (Inner Left -> Crosses Over to Right)
        val pathBraid1 = androidx.compose.ui.graphics.Path().apply {
            moveTo(cx - 16.dp.toPx(), bottomYStart)
            lineTo(cx - 16.dp.toPx(), bottomYStart + 15.dp.toPx())
            // Cubic bezier/curve crossover to (cx + 16)
            cubicTo(
                cx - 16.dp.toPx(), crossYMid - 10.dp.toPx(),
                cx + 16.dp.toPx(), crossYMid - 10.dp.toPx(),
                cx + 16.dp.toPx(), crossYMid + 15.dp.toPx()
            )
            lineTo(cx + 16.dp.toPx(), h)
        }
        drawPath(path = pathBraid1, color = goldColor.copy(alpha = 0.35f), style = Stroke(width = 2.5.dp.toPx()))
        drawPath(
            path = pathBraid1,
            color = goldColor.copy(alpha = 0.8f),
            style = Stroke(width = 2.8.dp.toPx(), pathEffect = pulseEffectBraid1)
        )

        // Path 2 (Inner Right -> Crosses Over to Left)
        val pathBraid2 = androidx.compose.ui.graphics.Path().apply {
            moveTo(cx + 16.dp.toPx(), bottomYStart)
            lineTo(cx + 16.dp.toPx(), bottomYStart + 15.dp.toPx())
            // Cubic bezier/curve crossover to (cx - 16)
            cubicTo(
                cx + 16.dp.toPx(), crossYMid - 10.dp.toPx(),
                cx - 16.dp.toPx(), crossYMid - 10.dp.toPx(),
                cx - 16.dp.toPx(), crossYMid + 15.dp.toPx()
            )
            lineTo(cx - 16.dp.toPx(), h)
        }
        drawPath(path = pathBraid2, color = silverColor.copy(alpha = 0.35f), style = Stroke(width = 2.5.dp.toPx()))
        drawPath(
            path = pathBraid2,
            color = silverColor.copy(alpha = 0.8f),
            style = Stroke(width = 2.8.dp.toPx(), pathEffect = pulseEffectBraid2)
        )

        // Path 3 (Outer Left -> Hugs inwards and runs parallel to Path 2 on the left)
        val pathBraid3 = androidx.compose.ui.graphics.Path().apply {
            moveTo(cx - 32.dp.toPx(), bottomYStart)
            lineTo(cx - 32.dp.toPx(), bottomYStart + 12.dp.toPx())
            cubicTo(
                cx - 32.dp.toPx(), crossYMid - 15.dp.toPx(),
                cx - 28.dp.toPx(), crossYMid - 15.dp.toPx(),
                cx - 28.dp.toPx(), crossYMid + 15.dp.toPx()
            )
            lineTo(cx - 28.dp.toPx(), h)
        }
        drawPath(path = pathBraid3, color = goldColor.copy(alpha = 0.25f), style = Stroke(width = 1.8.dp.toPx()))
        drawPath(
            path = pathBraid3,
            color = goldColor.copy(alpha = 0.6f),
            style = Stroke(width = 2.0.dp.toPx(), pathEffect = pulseEffectBraid1)
        )

        // Path 4 (Outer Right -> Hugs inwards and runs parallel to Path 1 on the right)
        val pathBraid4 = androidx.compose.ui.graphics.Path().apply {
            moveTo(cx + 32.dp.toPx(), bottomYStart)
            lineTo(cx + 32.dp.toPx(), bottomYStart + 12.dp.toPx())
            cubicTo(
                cx + 32.dp.toPx(), crossYMid - 15.dp.toPx(),
                cx + 28.dp.toPx(), crossYMid - 15.dp.toPx(),
                cx + 28.dp.toPx(), crossYMid + 15.dp.toPx()
            )
            lineTo(cx + 28.dp.toPx(), h)
        }
        drawPath(path = pathBraid4, color = silverColor.copy(alpha = 0.25f), style = Stroke(width = 1.8.dp.toPx()))
        drawPath(
            path = pathBraid4,
            color = silverColor.copy(alpha = 0.6f),
            style = Stroke(width = 2.0.dp.toPx(), pathEffect = pulseEffectBraid2)
        )
    }
}

@Composable
fun CenteredCosmicControlSystem(
    viewModel: LauncherViewModel,
    isGenerating: Boolean,
    isDarkTheme: Boolean,
    rotationDuration: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "CosmicSystem")
    
    // 1. Breathing Glow Scale and Alpha
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowScale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.20f,
        targetValue = 0.50f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowAlpha"
    )

    // 2. Slow counter-rotating background rings
    val ringRotation1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -360f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RingRotation1"
    )
    val ringRotation2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RingRotation2"
    )

    Box(
        modifier = modifier.size(260.dp),
        contentAlignment = Alignment.Center
    ) {
        // A. Breathing Halo Aura (glowing behind the logo)
        Box(
            modifier = Modifier
                .size(150.dp)
                .graphicsLayer {
                    scaleX = glowScale
                    scaleY = glowScale
                    alpha = glowAlpha
                }
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            if (isDarkTheme) Color(0xFFD4AF37).copy(alpha = 0.5f) else Color(0xFFC0C0C0).copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // B. Counter-Rotating Orbit Rings drawn on Canvas
        Canvas(
            modifier = Modifier
                .size(195.dp)
                .graphicsLayer { rotationZ = ringRotation1 }
        ) {
            drawCircle(
                color = Color(0xFFD4AF37).copy(alpha = 0.15f),
                radius = size.width / 2f,
                style = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f))
            )
            drawCircle(
                color = Color(0xFFD4AF37),
                radius = 3.dp.toPx(),
                center = Offset(size.width / 2f, 0f)
            )
        }

        Canvas(
            modifier = Modifier
                .size(215.dp)
                .graphicsLayer { rotationZ = ringRotation2 }
        ) {
            drawCircle(
                color = Color(0xFFC0C0C0).copy(alpha = 0.12f),
                radius = size.width / 2f,
                style = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 20f), 0f))
            )
            drawCircle(
                color = Color(0xFFC0C0C0),
                radius = 2.5.dp.toPx(),
                center = Offset(0f, size.height / 2f)
            )
        }

        // C. Central Yin-Yang Core (clickable)
        Box(
            modifier = Modifier
                .size(130.dp)
                .testTag("yinyang_core_container")
        ) {
            YinYangCore(
                isThinking = isGenerating,
                isDarkTheme = isDarkTheme,
                baseRotationDurationMillis = rotationDuration.toInt(),
                onClick = {
                    viewModel.setScreen(ScreenState.AI_CORE)
                }
            )
        }

        // D. Symmetrically Placed Orbital Quick Action Buttons
        // 1. Top-Right (45°): AI Chat
        OrbitalButton(
            icon = Icons.Default.AutoAwesome,
            label = "AI Chat",
            description = "Conscious Core AI",
            color = Color(0xFFD4AF37),
            onClick = { viewModel.setScreen(ScreenState.AI_CORE) },
            modifier = Modifier.offset(x = 68.dp, y = -68.dp)
        )

        // 2. Top-Left (135°): Settings
        OrbitalButton(
            icon = Icons.Default.Settings,
            label = "Settings",
            description = "Launcher Variables",
            color = Color(0xFFC0C0C0),
            onClick = { viewModel.setScreen(ScreenState.SETTINGS) },
            modifier = Modifier.offset(x = -68.dp, y = -68.dp)
        )

        // 3. Bottom-Left (225°): Theme Toggle (Yin/Yang balance)
        OrbitalButton(
            icon = Icons.Default.Contrast,
            label = "Balance",
            description = "Toggle Yin/Yang Mode",
            color = Color(0xFFD4AF37),
            onClick = { viewModel.toggleTheme() },
            modifier = Modifier.offset(x = -68.dp, y = 68.dp)
        )

        // 4. Bottom-Right (315°): App Drawer
        OrbitalButton(
            icon = Icons.Default.Apps,
            label = "Apps",
            description = "App Manifest",
            color = Color(0xFFC0C0C0),
            onClick = { viewModel.setScreen(ScreenState.APP_DRAWER) },
            modifier = Modifier.offset(x = 68.dp, y = 68.dp)
        )
    }
}

@Composable
fun OrbitalButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    description: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.2f else 1.0f,
        label = "OrbitalButtonScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .background(
                    color = Color(0xFF0C0C0E).copy(alpha = 0.9f),
                    shape = CircleShape
                )
                .border(
                    width = 1.dp,
                    color = color.copy(alpha = 0.6f),
                    shape = CircleShape
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = description,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(2.dp))
        
        Text(
            text = label.uppercase(),
            fontSize = 7.5.sp,
            color = Color.White.copy(alpha = 0.7f),
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}
