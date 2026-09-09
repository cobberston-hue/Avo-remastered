package com.example.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.audio.AvoAudioManager
import com.example.model.CostumeType
import com.example.model.JellyBean
import com.example.model.MysteryClue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.*

data class Particle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val size: Float,
    var alpha: Float = 1f,
    var life: Float = 1f
)

@Composable
fun InteractiveAvoField(
    beans: List<JellyBean>,
    mysteryClue: MysteryClue,
    costume: CostumeType,
    audioManager: AvoAudioManager,
    onBeanCollected: (beanId: Int) -> Unit,
    onClueFound: () -> Unit,
    onAvoPositionChanged: (normX: Float, normY: Float) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    // Avo's physical state (in pixels)
    var avoX by remember { mutableFloatStateOf(-1f) }
    var avoY by remember { mutableFloatStateOf(-1f) }
    var screenWidthPx by remember { mutableFloatStateOf(1080f) }
    var screenHeightPx by remember { mutableFloatStateOf(1920f) }
    var avoHeading by remember { mutableFloatStateOf(90f) }
    var isAvoWalking by remember { mutableStateOf(false) }
    val bounceHop = remember { Animatable(0f) }

    // Path waypoints (player drawn)
    val pathPoints = remember { mutableStateListOf<Offset>() }
    // Remaining points Avo is walking along
    val avoTargetPoints = remember { mutableStateListOf<Offset>() }

    // Particle bursts for collection
    val particles = remember { mutableStateListOf<Particle>() }

    // Animation frame ticker for Avo's walking movement
    LaunchedEffect(avoTargetPoints.size) {
        if (avoTargetPoints.isNotEmpty()) {
            isAvoWalking = true
            while (avoTargetPoints.isNotEmpty()) {
                val target = avoTargetPoints.first()
                val dx = target.x - avoX
                val dy = target.y - avoY
                val dist = hypot(dx, dy)

                // Calculate heading angle
                val angleDeg = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                avoHeading = (angleDeg + 360f) % 360f

                val speed = 9f // Movement speed per frame
                if (dist <= speed) {
                    avoX = target.x
                    avoY = target.y
                    avoTargetPoints.removeAt(0)
                } else {
                    avoX += (dx / dist) * speed
                    avoY += (dy / dist) * speed
                }

                // Notify normalized position for dynamic screen switching
                if (screenWidthPx > 0f && screenHeightPx > 0f) {
                    onAvoPositionChanged(avoX / screenWidthPx, avoY / screenHeightPx)
                }

                // Audio step
                audioManager.playAvoWalk()

                // Check collision with beans
                beans.forEach { bean ->
                    if (!bean.isCollected) {
                        // Bean position in pixels
                        // (calculated in canvas or relative to screen)
                    }
                }

                delay(16) // ~60 FPS update
            }
            isAvoWalking = false
            // Little celebratory stop hop and squeak
            coroutineScope.launch {
                bounceHop.animateTo(1f, tween(140))
                bounceHop.animateTo(0f, tween(140))
            }
            audioManager.playAvoSqueak()
        }
    }

    // Particle updater
    LaunchedEffect(Unit) {
        while (true) {
            if (particles.isNotEmpty()) {
                val iterator = particles.listIterator()
                while (iterator.hasNext()) {
                    val p = iterator.next()
                    p.alpha -= 0.045f
                    p.life -= 0.045f
                    if (p.life <= 0f) {
                        iterator.remove()
                    }
                }
            }
            delay(16)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    // Check if tapped on Avo
                    val distToAvo = hypot(tapOffset.x - avoX, tapOffset.y - avoY)
                    if (distToAvo < 60f) {
                        audioManager.playAvoSqueak()
                        coroutineScope.launch {
                            bounceHop.animateTo(1f, tween(120))
                            bounceHop.animateTo(0f, tween(120))
                        }
                    } else {
                        // Quick walk to tap location
                        pathPoints.clear()
                        pathPoints.add(Offset(avoX, avoY))
                        pathPoints.add(tapOffset)
                        avoTargetPoints.clear()
                        avoTargetPoints.add(tapOffset)
                        audioManager.playSparkDrop()
                    }
                }
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { startOffset ->
                        pathPoints.clear()
                        pathPoints.add(startOffset)
                        avoTargetPoints.clear()
                        avoTargetPoints.add(startOffset)
                        audioManager.playSparkDrop()
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        val newPoint = change.position
                        // Only add point if sufficiently far from last
                        val last = pathPoints.lastOrNull()
                        if (last == null || hypot(newPoint.x - last.x, newPoint.y - last.y) > 18f) {
                            pathPoints.add(newPoint)
                            avoTargetPoints.add(newPoint)

                            // Add spark particle at finger tip
                            particles.add(
                                Particle(
                                    x = newPoint.x,
                                    y = newPoint.y,
                                    vx = (Math.random().toFloat() - 0.5f) * 4f,
                                    vy = (Math.random().toFloat() - 0.5f) * 4f,
                                    color = Color(0xFFFFE066),
                                    size = 5f
                                )
                            )
                        }
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height
            screenWidthPx = canvasW
            screenHeightPx = canvasH

            // Initialize Avo position to bottom-center of stage if not set
            if (avoX < 0f || avoY < 0f) {
                avoX = canvasW * 0.5f
                avoY = canvasH * 0.76f
                onAvoPositionChanged(0.5f, 0.76f)
            }

            // 1. Draw Player Drawn Sparkling Path
            if (pathPoints.size > 1) {
                val path = Path().apply {
                    moveTo(pathPoints.first().x, pathPoints.first().y)
                    for (i in 1 until pathPoints.size) {
                        val p0 = pathPoints[i - 1]
                        val p1 = pathPoints[i]
                        val midX = (p0.x + p1.x) / 2f
                        val midY = (p0.y + p1.y) / 2f
                        quadraticTo(p0.x, p0.y, midX, midY)
                    }
                    lineTo(pathPoints.last().x, pathPoints.last().y)
                }

                // Outer soft glowing amber bloom
                drawPath(
                    path = path,
                    color = Color(0x55FFAA00),
                    style = Stroke(width = 24f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                // Mid bright gold line
                drawPath(
                    path = path,
                    color = Color(0xCCFFCC00),
                    style = Stroke(width = 12f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                // Inner pure brilliant white-gold spark core
                drawPath(
                    path = path,
                    color = Color(0xFFFFFFEE),
                    style = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }

            // 2. Draw Floating Jellybeans
            beans.forEach { bean ->
                if (!bean.isCollected) {
                    val bx = bean.x * canvasW
                    val by = bean.y * canvasH
                    val beanColor = Color(bean.color)

                    // Collision check with Avo
                    val dist = hypot(avoX - bx, avoY - by)
                    if (dist < 46f) {
                        onBeanCollected(bean.id)
                        audioManager.playSparkCollect()
                        // Burst particles
                        repeat(12) {
                            particles.add(
                                Particle(
                                    x = bx,
                                    y = by,
                                    vx = (Math.random().toFloat() - 0.5f) * 12f,
                                    vy = (Math.random().toFloat() - 0.5f) * 12f,
                                    color = beanColor,
                                    size = 7f
                                )
                            )
                        }
                    }

                    // Draw Jellybean (authentic candy gem bean shape)
                    // Glow
                    drawCircle(
                        color = beanColor.copy(alpha = 0.35f),
                        radius = 22f,
                        center = Offset(bx, by)
                    )
                    // Bean body
                    drawOval(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White, beanColor, beanColor.copy(alpha = 0.8f)),
                            center = Offset(bx - 4f, by - 4f),
                            radius = 18f
                        ),
                        topLeft = Offset(bx - 14f, by - 10f),
                        size = androidx.compose.ui.geometry.Size(28f, 20f)
                    )
                    // Specular highlight dot
                    drawCircle(
                        color = Color.White,
                        radius = 3.5f,
                        center = Offset(bx - 5f, by - 5f)
                    )
                }
            }

            // 3. Draw Mystery Clue Target (if not found yet)
            if (!mysteryClue.isFound) {
                val cx = mysteryClue.x * canvasW
                val cy = mysteryClue.y * canvasH

                // Collision check with Avo
                val dist = hypot(avoX - cx, avoY - cy)
                if (dist < 54f) {
                    onClueFound()
                    audioManager.playCelebration()
                    // Golden celebratory particle fountain
                    repeat(24) {
                        particles.add(
                            Particle(
                                x = cx,
                                y = cy,
                                vx = (Math.random().toFloat() - 0.5f) * 16f,
                                vy = -Math.random().toFloat() * 16f,
                                color = Color(0xFFFFD700),
                                size = 8f
                            )
                        )
                    }
                }

                // Clue pulse ring
                drawCircle(
                    color = Color(0x66FFD700),
                    radius = 28f,
                    center = Offset(cx, cy),
                    style = Stroke(width = 3f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFFFF9C4), Color(0xFFFFB300)),
                        center = Offset(cx, cy),
                        radius = 18f
                    ),
                    radius = 18f,
                    center = Offset(cx, cy)
                )
                // Sparkle dot
                drawCircle(color = Color.White, radius = 5f, center = Offset(cx, cy))
            }

            // 4. Draw Collection Particles
            particles.forEach { p ->
                drawCircle(
                    color = p.color.copy(alpha = p.alpha.coerceIn(0f, 1f)),
                    radius = p.size * p.life,
                    center = Offset(p.x, p.y)
                )
            }
        }

        // Render Avo Character over the canvas at (avoX, avoY)
        if (avoX >= 0f && avoY >= 0f) {
            val avoSizePx = with(density) { 76.dp.toPx() }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationX = avoX - avoSizePx / 2f
                        translationY = avoY - avoSizePx / 2f
                    }
            ) {
                AvoCharacter(
                    size = 76.dp,
                    isWalking = isAvoWalking,
                    headingAngle = avoHeading,
                    costume = costume,
                    bounceHop = bounceHop.value
                )
            }
        }
    }
}
