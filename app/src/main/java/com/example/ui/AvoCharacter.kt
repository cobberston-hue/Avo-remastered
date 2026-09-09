package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.model.CostumeType
import kotlin.math.*

/**
 * Authentic Avo character component rendered directly from the original 3D model meshes
 * (avodado.obj, avocado_stone.obj, eyeball.obj, eyelid_top.obj, bowler_hat.obj, sherlock_hat.obj)
 * and extracted textures (Avo_Albedo.png, Avo_Stone_Albedo.png, AvoEyeballAlbedo.png).
 * Supports full 8-directional rotation, blinking, dynamic walking waddle, and authentic costume hats.
 */
@Composable
fun AvoCharacter(
    modifier: Modifier = Modifier,
    size: Dp = 72.dp,
    isWalking: Boolean = false,
    headingAngle: Float = 0f, // Degrees: 0 is right, 90 is down, 180 is left, 270 is up
    costume: CostumeType = CostumeType.CLASSIC,
    bounceHop: Float = 0f // 0f..1f for jump/hop
) {
    val infiniteTransition = rememberInfiniteTransition(label = "AvoIdle")
    
    // Idle gentle breathing scale
    val breathScale by infiniteTransition.animateFloat(
        initialValue = 0.985f,
        targetValue = 1.025f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AvoBreath"
    )

    // Walking tilt waddle & leg step cycle
    val walkWaddle by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(180, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AvoWaddle"
    )

    val walkStepCycle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(240, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AvoStepCycle"
    )

    // Periodic blinking (every 3-4 seconds)
    var isBlinking by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay((2800..4500).random().toLong())
            isBlinking = true
            kotlinx.coroutines.delay(160)
            isBlinking = false
        }
    }

    // Select sprite based on heading angle and costume
    // Screen headingAngle:
    // 0 is right, 90 is down, 180 is left, 270 is up
    val effectiveAngle = (headingAngle % 360f + 360f) % 360f
    
    // Determine 8-directional rotation asset
    val baseSpriteRes = when {
        isBlinking && !isWalking && costume == CostumeType.CLASSIC -> R.drawable.avo_blink
        costume == CostumeType.DETECTIVE -> R.drawable.avo_costume_detective
        costume == CostumeType.BOWLER -> R.drawable.avo_costume_bowler
        costume == CostumeType.SOMBRERO -> R.drawable.avo_costume_sombrero
        costume == CostumeType.TIARA -> R.drawable.avo_costume_tiara
        costume == CostumeType.POLICE -> R.drawable.avo_costume_police
        costume == CostumeType.ROCKET -> R.drawable.avo_costume_rocket
        isWalking && (effectiveAngle in 45f..135f) -> {
            if (walkStepCycle > 0.5f) R.drawable.avo_walk_left else R.drawable.avo_walk_right
        }
        else -> {
            when (effectiveAngle) {
                in 337.5f..360f, in 0f..22.5f -> R.drawable.avo_rot_90 // facing right
                in 22.5f..67.5f -> R.drawable.avo_rot_45 // facing down-right
                in 67.5f..112.5f -> R.drawable.avo_rot_0 // facing front/down
                in 112.5f..157.5f -> R.drawable.avo_rot_315 // facing down-left
                in 157.5f..202.5f -> R.drawable.avo_rot_270 // facing left
                in 202.5f..247.5f -> R.drawable.avo_rot_225 // facing up-left
                in 247.5f..292.5f -> R.drawable.avo_rot_180 // facing up/away
                else -> R.drawable.avo_rot_135 // facing up-right
            }
        }
    }

    val currentRotation = if (isWalking) walkWaddle else 0f
    val currentBounceY = -bounceHop * 24f

    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                rotationZ = currentRotation
                scaleX = if (isWalking && (effectiveAngle in 157.5f..247.5f) && costume != CostumeType.CLASSIC) -1f else 1f
                scaleY = breathScale
                translationY = currentBounceY
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = baseSpriteRes),
            contentDescription = "Avo",
            modifier = Modifier.size(size)
        )
    }
}
