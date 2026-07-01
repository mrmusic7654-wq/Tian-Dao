package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun YinYangCore(
    modifier: Modifier = Modifier,
    isThinking: Boolean = false,
    isDarkTheme: Boolean = true, // true = Yin background (White on Black Yin-Yang), false = Yang background (Black on White Yin-Yang)
    baseRotationDurationMillis: Int = 8000,
    onClick: () -> Unit = {}
) {
    // Rotation speed based on whether Gemini is thinking
    val duration = if (isThinking) 1500 else baseRotationDurationMillis
    val infiniteTransition = rememberInfiniteTransition(label = "yin_yang_rotation")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Pulse scale effect when thinking
    val scale by animateFloatAsState(
        targetValue = if (isThinking) 1.15f else 1.0f,
        animationSpec = tween(1000, easing = LinearEasing),
        label = "pulse_scale"
    )

    Box(
        modifier = modifier
            .size((180 * scale).dp)
            .testTag("yin_yang_core")
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null // Quiet click, no huge distracting ripple for the cosmic core
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width / 2

            // Gold and Silver Yin-Yang
            // Yin is Silver (Moon, passive reflection)
            // Yang is Gold (Sun, active light)
            val silverColor = Color(0xFFC0C0C0)
            val goldColor = Color(0xFFD4AF37)
            val silverHighlight = Color(0xFFF1F5F9)
            val goldHighlight = Color(0xFFFCD34D)

            // Let's rotate the drawing based on the animated angle
            rotate(rotationAngle) {
                val rInner = radius - 10.dp.toPx()

                // 1. Draw solid dark background drop-shadow for depth
                drawCircle(
                    color = Color(0xFF040405),
                    radius = radius,
                    center = center
                )

                // 2. Draw Outer Bezel Ring (Sweep gradient simulating polished Gold and Chrome metal)
                val bezelColors = listOf(
                    Color(0xFF8A640F), // Dark gold shadow
                    Color(0xFFD4AF37), // Pure gold
                    Color(0xFFFFF2CD), // Gold highlight
                    Color(0xFFB38F2D), // Gold mid
                    Color(0xFFFFFFFF), // Chrome highlight
                    Color(0xFF9CA3AF), // Silver mid
                    Color(0xFF374151), // Silver shadow
                    Color(0xFFD4AF37), // Gold
                    Color(0xFF8A640F)  // Back to dark gold
                )
                drawCircle(
                    brush = Brush.sweepGradient(colors = bezelColors, center = center),
                    radius = radius - 4.dp.toPx(),
                    center = center,
                    style = Stroke(width = 6.dp.toPx())
                )

                // 3. Draw thin dark inset groove
                drawCircle(
                    color = Color(0xFF08080A),
                    radius = radius - 7.dp.toPx(),
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )

                // 4. DRAW BASE YIN & YANG LOBES WITH METALLIC GRADIENTS
                // Left half Yin (Charcoal metal)
                val yinGradient = Brush.radialGradient(
                    colors = listOf(Color(0xFF32343C), Color(0xFF1E1F22), Color(0xFF0F1012)),
                    center = Offset(center.x - rInner / 2f, center.y),
                    radius = rInner * 1.2f
                )
                drawCircle(
                    brush = yinGradient,
                    radius = rInner,
                    center = center
                )

                // Right half Yang (Polished Gold metal)
                val yangGradient = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFDF85), // Brilliant light gold
                        Color(0xFFD4AF37), // Standard gold
                        Color(0xFF916A16), // Rich dark gold
                        Color(0xFF533B07)  // Deep shadow gold
                    ),
                    start = Offset(center.x, center.y - rInner),
                    end = Offset(center.x + rInner, center.y + rInner)
                )
                drawArc(
                    brush = yangGradient,
                    startAngle = -90f,
                    sweepAngle = 180f,
                    useCenter = true,
                    size = Size(rInner * 2, rInner * 2),
                    topLeft = Offset(center.x - rInner, center.y - rInner)
                )

                // Blend curves (S-curve structure)
                // Upper medium circle (Yin lobe - Dark charcoal)
                drawCircle(
                    brush = yinGradient,
                    radius = rInner / 2f,
                    center = Offset(center.x, center.y - rInner / 2f)
                )

                // Lower medium circle (Yang lobe - Gold metal)
                drawCircle(
                    brush = yangGradient,
                    radius = rInner / 2f,
                    center = Offset(center.x, center.y + rInner / 2f)
                )

                // 5. HIGH-FIDELITY EMBEDDED MICRO-CIRCUITS
                // Draw concentric circuit traces matching the curve contours of each side
                // Dark Yin side - Glowing gold traces
                drawArc(
                    color = Color(0xFFD4AF37).copy(alpha = 0.35f),
                    startAngle = 90f,
                    sweepAngle = 180f,
                    useCenter = false,
                    size = Size(rInner * 1.5f, rInner * 1.5f),
                    topLeft = Offset(center.x - rInner * 0.75f, center.y - rInner * 0.75f),
                    style = Stroke(width = 1.2.dp.toPx())
                )
                drawArc(
                    color = Color(0xFFFFFFFF).copy(alpha = 0.25f),
                    startAngle = 110f,
                    sweepAngle = 140f,
                    useCenter = false,
                    size = Size(rInner * 1.1f, rInner * 1.1f),
                    topLeft = Offset(center.x - rInner * 0.55f, center.y - rInner * 0.55f),
                    style = Stroke(width = 1.dp.toPx())
                )

                // Gold Yang side - Dark bronze and silver traces
                drawArc(
                    color = Color(0xFF533B07).copy(alpha = 0.5f),
                    startAngle = -90f,
                    sweepAngle = 180f,
                    useCenter = false,
                    size = Size(rInner * 1.5f, rInner * 1.5f),
                    topLeft = Offset(center.x - rInner * 0.75f, center.y - rInner * 0.75f),
                    style = Stroke(width = 1.2.dp.toPx())
                )
                drawArc(
                    color = Color(0xFFFFFFFF).copy(alpha = 0.4f),
                    startAngle = -70f,
                    sweepAngle = 140f,
                    useCenter = false,
                    size = Size(rInner * 1.1f, rInner * 1.1f),
                    topLeft = Offset(center.x - rInner * 0.55f, center.y - rInner * 0.55f),
                    style = Stroke(width = 1.dp.toPx())
                )

                // Custom fine terminal nodes in the body
                // Top-left dark side
                val node1 = Offset(center.x - rInner * 0.6f, center.y - rInner * 0.2f)
                val node2 = Offset(center.x - rInner * 0.4f, center.y - rInner * 0.4f)
                drawLine(color = Color(0xFFD4AF37).copy(alpha = 0.6f), start = node1, end = node2, strokeWidth = 1.dp.toPx())
                drawCircle(color = Color(0xFFFFD54F), radius = 2.dp.toPx(), center = node2)

                // Bottom-right gold side
                val node3 = Offset(center.x + rInner * 0.6f, center.y + rInner * 0.2f)
                val node4 = Offset(center.x + rInner * 0.4f, center.y + rInner * 0.4f)
                drawLine(color = Color(0xFF374151).copy(alpha = 0.6f), start = node3, end = node4, strokeWidth = 1.dp.toPx())
                drawCircle(color = Color(0xFF9CA3AF), radius = 2.dp.toPx(), center = node4)


                // 6. DRAW THE CONTRASTING EYES (FUTURISTIC HIGH-TECH REACTOR DIALS)
                val eyeRadius = rInner * 0.25f

                // --- UPPER EYE (Inside the Dark Yin Lobe): Gold Core Reactor ---
                val upperEyeCenter = Offset(center.x, center.y - rInner / 2f)
                // Black base
                drawCircle(
                    color = Color(0xFF131416),
                    radius = eyeRadius,
                    center = upperEyeCenter
                )
                // Outer gold reactor ring
                drawCircle(
                    color = Color(0xFFD4AF37),
                    radius = eyeRadius,
                    center = upperEyeCenter,
                    style = Stroke(width = 1.5.dp.toPx())
                )
                // Concentric inner tech ring
                drawCircle(
                    color = Color(0xFFFFD54F).copy(alpha = 0.5f),
                    radius = eyeRadius * 0.7f,
                    center = upperEyeCenter,
                    style = Stroke(width = 1.dp.toPx())
                )
                // Center Glowing Core (specular radial light orb)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFFFFFF), Color(0xFFFFD54F), Color(0xFFFF8F00), Color.Transparent),
                        center = upperEyeCenter,
                        radius = eyeRadius * 0.45f
                    ),
                    radius = eyeRadius * 0.45f,
                    center = upperEyeCenter
                )
                // Reactor crosshair/spikes
                for (angle in listOf(0, 90, 180, 270)) {
                    val rad = Math.toRadians(angle.toDouble())
                    val cos = Math.cos(rad).toFloat()
                    val sin = Math.sin(rad).toFloat()
                    drawLine(
                        color = Color(0xFFFFD54F).copy(alpha = 0.8f),
                        start = Offset(upperEyeCenter.x + eyeRadius * 0.5f * cos, upperEyeCenter.y + eyeRadius * 0.5f * sin),
                        end = Offset(upperEyeCenter.x + eyeRadius * 0.8f * cos, upperEyeCenter.y + eyeRadius * 0.8f * sin),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // --- LOWER EYE (Inside the Gold Yang Lobe): Glossy Chrome Sphere ---
                val lowerEyeCenter = Offset(center.x, center.y + rInner / 2f)
                // Dark outer shadow ring
                drawCircle(
                    color = Color(0xFF0F172A),
                    radius = eyeRadius,
                    center = lowerEyeCenter
                )
                // Polished metal outer stroke ring
                drawCircle(
                    color = Color(0xFF94A3B8),
                    radius = eyeRadius,
                    center = lowerEyeCenter,
                    style = Stroke(width = 1.5.dp.toPx())
                )
                // Glossy Chrome/Silver Sphere (simulating 3D metallic volume with specular highlight)
                val chromeSphereBrush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF), // Highlight point
                        Color(0xFFE5E7EB), // Silver
                        Color(0xFF9CA3AF), // Medium grey
                        Color(0xFF374151), // Shadow
                        Color(0xFF111827)  // Deep shadow at base
                    ),
                    center = Offset(lowerEyeCenter.x - eyeRadius * 0.25f, lowerEyeCenter.y - eyeRadius * 0.25f),
                    radius = eyeRadius * 0.85f
                )
                drawCircle(
                    brush = chromeSphereBrush,
                    radius = eyeRadius * 0.82f,
                    center = lowerEyeCenter
                )
                // Secondary tech orbit ring
                drawCircle(
                    color = Color(0xFFFFFFFF).copy(alpha = 0.4f),
                    radius = eyeRadius * 1.15f,
                    center = lowerEyeCenter,
                    style = Stroke(width = 0.8.dp.toPx())
                )

                // 7. MULTI-PADS/CONCENTRIC DECORATIONS ALONG OUTER BORDERS
                val microAngles = listOf(30, 60, 120, 150, 210, 240, 300, 330)
                for (angle in microAngles) {
                    val rad = Math.toRadians(angle.toDouble())
                    val cos = Math.cos(rad).toFloat()
                    val sin = Math.sin(rad).toFloat()
                    val padX = center.x + (rInner - 4.dp.toPx()) * cos
                    val padY = center.y + (rInner - 4.dp.toPx()) * sin
                    drawCircle(
                        color = if (angle % 60 == 0) Color(0xFFD4AF37) else Color(0xFFE5E7EB),
                        radius = 2.dp.toPx(),
                        center = Offset(padX, padY)
                    )
                }
            }
        }
    }
}
