package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.History
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.apps.AppInfo
import com.example.ui.LauncherViewModel
import com.example.ui.ScreenState
import kotlinx.coroutines.launch

@Composable
fun AppDrawerScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val filteredApps by viewModel.filteredApps.collectAsState()
    val searchQuery by viewModel.appSearchQuery.collectAsState()

    // Sort apps alphabetically by label
    val sortedApps = remember(filteredApps) {
        filteredApps.sortedBy { it.label.lowercase() }
    }

    // Group apps alphabetically by first letter
    val groupedApps = remember(sortedApps) {
        sortedApps.groupBy { it.label.firstOrNull()?.uppercaseChar() ?: '#' }
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Calculate the LazyColumn index of each letter section header for fast scrolling jumps
    val headerIndexes = remember(groupedApps) {
        val indexes = mutableMapOf<Char, Int>()
        var currentIndex = 0
        groupedApps.forEach { (letter, appsInGroup) ->
            indexes[letter] = currentIndex
            val chunkCount = (appsInGroup.size + 3) / 4 // Rows count (chunked by 4)
            currentIndex += 1 + chunkCount
        }
        indexes
    }

    val alphabetList = remember(groupedApps) {
        groupedApps.keys.toList().sorted()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070709)) // Yin Dark deep background
            .padding(top = 16.dp, start = 16.dp, bottom = 16.dp)
    ) {
        // Search header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.setScreen(ScreenState.HOME) },
                modifier = Modifier.testTag("drawer_back_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Return home",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

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
                                tint = Color.White
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
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Title Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color(0xFFD4AF37), shape = CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ALPHABETICAL MANIFESTATIONS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC0C0C0),
                letterSpacing = 1.5.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // List of Apps and fast-scroller side-by-side
        if (sortedApps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(end = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "☯",
                        fontSize = 48.sp,
                        color = Color(0xFFD4AF37).copy(alpha = 0.2f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No manifestation found",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Main grid view
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    groupedApps.forEach { (letter, appsInGroup) ->
                        // Header
                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp, start = 8.dp, end = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(Color(0xFF141416), CircleShape)
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

                        // Grid rows chunked by 4
                        val chunkedApps = appsInGroup.chunked(4)
                        items(chunkedApps) { rowApps ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                for (i in 0 until 4) {
                                    if (i < rowApps.size) {
                                        val app = rowApps[i]
                                        AppGridItem(
                                            app = app,
                                            onClick = {
                                                viewModel.launchApp(app.packageName, app.label)
                                                viewModel.setAppSearchQuery("") // clear search on launch
                                                viewModel.setScreen(ScreenState.HOME)
                                            },
                                            modifier = Modifier.weight(1f)
                                        )
                                    } else {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }

                // Scrollbar sidebar index
                if (alphabetList.size > 1) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .wrapContentWidth()
                            .padding(horizontal = 4.dp)
                            .background(Color(0xFF0E0E10), RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                            .border(0.5.dp, Color(0xFFD4AF37).copy(alpha = 0.15f), RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                            .padding(vertical = 16.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        alphabetList.forEach { letter ->
                            Text(
                                text = letter.toString(),
                                color = if (letter.code % 2 == 0) Color(0xFFD4AF37) else Color(0xFFC0C0C0),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null
                                    ) {
                                        headerIndexes[letter]?.let { index ->
                                            scope.launch {
                                                listState.animateScrollToItem(index)
                                            }
                                        }
                                    }
                                    .padding(vertical = 3.dp, horizontal = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppGridItem(
    app: AppInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1.0f,
        label = "AppGridScale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 10.dp, horizontal = 2.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        DrawerGameStyleIcon(app = app, size = 48.dp)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = app.label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White.copy(alpha = 0.85f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
private fun DrawerGameStyleIcon(
    app: AppInfo,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 48.dp
) {
    val labelLower = app.label.lowercase()
    val pkgLower = app.packageName.lowercase()
    
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFD4AF37).copy(alpha = 0.15f),
                            Color(0xFFC0C0C0).copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
                .border(
                    width = 2.dp,
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color(0xFFD4AF37),
                            Color(0xFFC0C0C0),
                            Color(0xFFD4AF37).copy(alpha = 0.3f),
                            Color(0xFFD4AF37)
                        )
                    ),
                    shape = CircleShape
                )
                .padding(4.dp)
        ) {
            if (mappedIcon != null) {
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
