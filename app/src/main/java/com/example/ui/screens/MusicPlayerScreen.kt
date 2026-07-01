package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.LauncherViewModel
import com.example.ui.ScreenState
import com.example.ui.components.YinYangCore
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MusicPlayerScreen(
    viewModel: LauncherViewModel,
    modifier: Modifier = Modifier
) {
    val isPlaying by viewModel.isMusicPlaying.collectAsState()
    val progress by viewModel.musicProgress.collectAsState()
    val currentTrackIndex by viewModel.currentTrackIndex.collectAsState()
    val track = viewModel.musicTracks[currentTrackIndex]

    val goldColor = Color(0xFFD4AF37)
    val silverColor = Color(0xFFC0C0C0)
    val deepDark = Color(0xFF070709)

    // Rotating sound core animation
    val infiniteTransition = rememberInfiniteTransition(label = "MusicPlayerRotation")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isPlaying) 10000 else 40000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RotationAngle"
    )

    // Pulse animation for glow aura
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isPlaying) 1.12f else 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isPlaying) 1500 else 3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    // Bouncing visualizer bars simulation
    val visualizerBarsCount = 20
    val visualizerStates = List(visualizerBarsCount) { i ->
        infiniteTransition.animateFloat(
            initialValue = 0.1f,
            targetValue = if (isPlaying) (0.3f + (i % 7) * 0.09f) else 0.15f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 250 + (i % 5) * 80,
                    easing = EaseInOutSine
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = "VisualizerBar_$i"
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(deepDark)
            .testTag("music_player_screen")
    ) {
        // High-Tech Ambient Matrix/Circuit lines in Background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h * 0.4f

            // Decorative background orbit circles
            drawCircle(
                color = goldColor.copy(alpha = 0.05f),
                radius = 160.dp.toPx(),
                center = Offset(cx, cy),
                style = Stroke(width = 1.dp.toPx())
            )
            drawCircle(
                color = silverColor.copy(alpha = 0.03f),
                radius = 210.dp.toPx(),
                center = Offset(cx, cy),
                style = Stroke(width = 1.dp.toPx())
            )

            // Technical crosshairs
            drawLine(
                color = silverColor.copy(alpha = 0.08f),
                start = Offset(cx - 240.dp.toPx(), cy),
                end = Offset(cx + 240.dp.toPx(), cy),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = silverColor.copy(alpha = 0.08f),
                start = Offset(cx, cy - 240.dp.toPx()),
                end = Offset(cx, cy + 240.dp.toPx()),
                strokeWidth = 1.dp.toPx()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { viewModel.setScreen(ScreenState.HOME) },
                    modifier = Modifier
                        .background(Color(0xFF141419), shape = CircleShape)
                        .border(1.dp, silverColor.copy(alpha = 0.15f), CircleShape)
                        .testTag("back_home_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Return home",
                        tint = goldColor
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "COSMIC RESONANCE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = goldColor,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Tian Dao Acoustic Balancing",
                        fontSize = 9.sp,
                        color = silverColor.copy(alpha = 0.6f),
                        letterSpacing = 1.sp
                    )
                }

                Box(modifier = Modifier.size(48.dp)) // Spacer to balance back button
            }

            Spacer(modifier = Modifier.weight(0.4f))

            // Central Sound Core (Yin Yang Spherical Core) with Glowing Aura
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .graphicsLayer {
                        scaleX = pulseScale
                        scaleY = pulseScale
                    }
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                goldColor.copy(alpha = if (isPlaying) 0.12f else 0.03f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Outer gold rotating circuit track
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .rotate(rotationAngle)
                        .border(
                            width = 1.5.dp,
                            brush = Brush.sweepGradient(
                                colors = listOf(goldColor, Color.Transparent, silverColor, Color.Transparent, goldColor)
                            ),
                            shape = CircleShape
                        )
                )

                // Inner Yin-Yang core
                Box(
                    modifier = Modifier
                        .size(124.dp)
                        .rotate(-rotationAngle * 0.5f) // Reverse counter-rotation for visual complexity
                ) {
                    YinYangCore(
                        modifier = Modifier.fillMaxSize(),
                        isThinking = isPlaying,
                        isDarkTheme = true
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.4f))

            // Real-Time Equalizer Visualizer (Bouncing High-Tech frequencies)
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(45.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                for (i in 0 until visualizerBarsCount) {
                    val barHeightFraction = visualizerStates[i].value
                    val isCenter = i in 6..13
                    val color = if (isCenter) goldColor else silverColor.copy(alpha = 0.7f)
                    
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 2.dp)
                            .fillMaxHeight(barHeightFraction)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(color, color.copy(alpha = 0.15f))
                                ),
                                shape = RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Track Details
            Text(
                text = track.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("track_title")
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${track.artist} • ${track.frequencyHz} Hz Solfeggio",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = goldColor,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(0.3f))

            // HIGH TECH SEEKBAR / PROGRESS SLIDER (Slider from left to right)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Slider(
                    value = progress,
                    onValueChange = { viewModel.setMusicProgress(it) },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        activeTrackColor = goldColor,
                        inactiveTrackColor = silverColor.copy(alpha = 0.15f),
                        thumbColor = goldColor,
                        activeTickColor = Color.Transparent,
                        inactiveTickColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("progress_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val currentSecs = (progress * 3).toInt() // Assume a 5-minute track (300 secs total)
                    val minutes = currentSecs / 60
                    val seconds = currentSecs % 60
                    
                    Text(
                        text = String.format("%02d:%02d", minutes, seconds),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = silverColor.copy(alpha = 0.45f)
                    )
                    Text(
                        text = "05:00",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = silverColor.copy(alpha = 0.45f)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(0.3f))

            // Media Tactile Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Prev button
                IconButton(
                    onClick = { viewModel.prevTrack() },
                    modifier = Modifier
                        .size(52.dp)
                        .background(Color(0xFF0F0F12), shape = CircleShape)
                        .border(1.dp, silverColor.copy(alpha = 0.12f), CircleShape)
                        .testTag("prev_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous track",
                        tint = silverColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Play / Pause button with breathing glow border
                IconButton(
                    onClick = { viewModel.setMusicPlaying(!isPlaying) },
                    modifier = Modifier
                        .size(76.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(goldColor.copy(alpha = 0.35f), Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                        .border(
                            width = 2.dp,
                            brush = Brush.sweepGradient(
                                colors = listOf(goldColor, silverColor, goldColor)
                            ),
                            shape = CircleShape
                        )
                        .testTag("play_pause_button")
                ) {
                    // Modern pause or play state representation
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                            .background(Color(0xFF131317), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isPlaying) {
                            // Custom high-tech pause bars
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(4.dp)
                                        .height(18.dp)
                                        .background(goldColor, shape = RoundedCornerShape(2.dp))
                                )
                                Box(
                                    modifier = Modifier
                                        .width(4.dp)
                                        .height(18.dp)
                                        .background(goldColor, shape = RoundedCornerShape(2.dp))
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play track",
                                tint = goldColor,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                // Next button
                IconButton(
                    onClick = { viewModel.nextTrack() },
                    modifier = Modifier
                        .size(52.dp)
                        .background(Color(0xFF0F0F12), shape = CircleShape)
                        .border(1.dp, silverColor.copy(alpha = 0.12f), CircleShape)
                        .testTag("next_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next track",
                        tint = silverColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
