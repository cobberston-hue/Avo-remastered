package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.AvoAudioManager
import com.example.model.CostumeType
import com.example.model.Episode
import com.example.model.JellyBean
import com.example.model.MysteryClue
import com.example.ui.AvoCharacter
import com.example.ui.AvoVideoPlayer
import com.example.ui.InteractiveAvoField
import com.example.ui.theme.*

@Composable
fun GameplayScreen(
    episode: Episode,
    currentCostume: CostumeType,
    audioManager: AvoAudioManager,
    onBack: () -> Unit,
    onEpisodeFinished: (episodeId: Int, beansCollected: Int, clueFound: Boolean, stars: Int) -> Unit,
    onOpenWardrobe: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Current episode state
    val beans = remember(episode) {
        mutableStateListOf<JellyBean>().apply {
            addAll(episode.beans)
        }
    }
    var mysteryClue by remember(episode) {
        mutableStateOf(episode.mysteryClue)
    }

    var isMuted by remember { mutableStateOf(!audioManager.isSoundEnabled) }
    var isVideoPlaying by remember { mutableStateOf(true) }
    var showCompletionDialog by remember { mutableStateOf(false) }

    // Dynamic screen / camera zone tracking
    var currentCameraZone by remember(episode) {
        mutableStateOf(episode.cameraZones.firstOrNull())
    }
    var videoTargetTimeMs by remember { mutableStateOf<Int?>(null) }
    var activeZoneName by remember { mutableStateOf("") }

    // Count collected beans
    val collectedBeansCount = beans.count { it.isCollected }
    val totalBeansCount = beans.size
    val allBeansCollected = collectedBeansCount == totalBeansCount

    // Calculate stars
    val calculatedStars = remember(collectedBeansCount, mysteryClue.isFound) {
        var stars = 1 // 1 star for playing
        if (allBeansCollected) stars += 1
        if (mysteryClue.isFound) stars += 1
        stars
    }

    Box(modifier = modifier.fillMaxSize().background(Color.Black)) {
        // 1. Live-Action Untouched 25 FPS Video Plate with Dynamic Camera Cut Switching
        AvoVideoPlayer(
            videoResId = episode.videoResId,
            modifier = Modifier.fillMaxSize(),
            isLooping = true,
            isMuted = isMuted,
            isPlaying = isVideoPlaying,
            targetTimeMs = videoTargetTimeMs
        )

        // 2. Interactive Touch Path & Avo Physics Layer
        InteractiveAvoField(
            beans = beans,
            mysteryClue = mysteryClue,
            costume = currentCostume,
            audioManager = audioManager,
            onBeanCollected = { beanId ->
                val index = beans.indexOfFirst { it.id == beanId }
                if (index >= 0) {
                    beans[index] = beans[index].copy(isCollected = true)
                }
            },
            onClueFound = {
                mysteryClue = mysteryClue.copy(isFound = true)
            },
            onAvoPositionChanged = { normX, normY ->
                // Check closest camera zone in current episode
                if (episode.cameraZones.isNotEmpty()) {
                    val bestZone = episode.cameraZones.minByOrNull { zone ->
                        val dx = normX - zone.focusCenterX
                        val dy = normY - zone.focusCenterY
                        dx * dx + dy * dy
                    }
                    if (bestZone != null && bestZone.id != currentCameraZone?.id) {
                        currentCameraZone = bestZone
                        videoTargetTimeMs = bestZone.startMs
                        activeZoneName = bestZone.name
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // 3. Top HUD Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            Surface(
                color = Color(0x77000000),
                shape = CircleShape,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
                modifier = Modifier.size(42.dp)
            ) {
                IconButton(onClick = {
                    audioManager.playUiButton()
                    onBack()
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }

            // Episode Title & Dynamic Camera Zone Badge
            Surface(
                color = Color(0x88122016),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4482C838))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (activeZoneName.isNotEmpty()) "🎥 $activeZoneName" else "EP. ${episode.number}: ${episode.location}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Right Controls (Beans, Clue, Costume)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Beans Collected Indicator with authentic ic_bean
                Surface(
                    color = Color(0x88000000),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x44FFC83B))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_bean),
                            contentDescription = "Beans",
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "$collectedBeansCount/$totalBeansCount",
                            color = BeanGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Mystery Clue Indicator
                Surface(
                    color = if (mysteryClue.isFound) Color(0xAA2E7D32) else Color(0x88000000),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (mysteryClue.isFound) Color(0xFF4ADE80) else Color(0x33FFFFFF)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = mysteryClue.emoji, fontSize = 13.sp)
                        if (mysteryClue.isFound) {
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "✓",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Wardrobe shortcut
                Surface(
                    color = Color(0x77000000),
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
                    modifier = Modifier.size(38.dp)
                ) {
                    IconButton(onClick = {
                        audioManager.playUiButton()
                        onOpenWardrobe()
                    }) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_customizer),
                            contentDescription = "Wardrobe",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // 4. Bottom Hint & Complete Objective Strip
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = Color(0x990E1A11),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4482C838))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (!mysteryClue.isFound)
                            "👆 Draw a path to guide Avo to ${mysteryClue.name}!"
                        else if (!allBeansCollected)
                            "✨ Mystery found! Collect remaining sparks or finish chapter."
                        else
                            "🎉 All sparks & mystery clue gathered!",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Button(
                        onClick = {
                            audioManager.playLevelComplete()
                            showCompletionDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AvoGreenPrimary
                        ),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Finish",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // 5. Episode Completion Dialog
        AnimatedVisibility(
            visible = showCompletionDialog,
            enter = fadeIn() + scaleIn(initialScale = 0.85f),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xCC000000))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    color = Color(0xFF16261A),
                    shape = RoundedCornerShape(28.dp),
                    border = androidx.compose.foundation.BorderStroke(2.dp, AvoGreenBright),
                    modifier = Modifier.fillMaxWidth().wrapContentHeight()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AvoCharacter(
                            size = 80.dp,
                            costume = currentCostume,
                            isWalking = false
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "CHAPTER COMPLETE!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = episode.title,
                            fontSize = 14.sp,
                            color = AvoGreenLight,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 3-Star Reward Display with original ic_star
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(3) { index ->
                                val isEarned = index < calculatedStars
                                Image(
                                    painter = painterResource(id = R.drawable.ic_star),
                                    contentDescription = "Star",
                                    modifier = Modifier.size(36.dp),
                                    alpha = if (isEarned) 1f else 0.25f
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Star Goals Checklist
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0x33000000), RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_star),
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Completed Episode: +1 Star",
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_star),
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    alpha = if (allBeansCollected) 1f else 0.3f
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Gathered All Sparks ($collectedBeansCount/$totalBeansCount)",
                                    color = if (allBeansCollected) Color(0xFFFFE082) else Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_star),
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    alpha = if (mysteryClue.isFound) 1f else 0.3f
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Discovered Mystery: ${mysteryClue.name}",
                                    color = if (mysteryClue.isFound) Color(0xFFFFE082) else Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    showCompletionDialog = false
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x66FFFFFF))
                            ) {
                                Text(text = "Keep Playing", color = Color.White, fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    audioManager.playUiButton()
                                    onEpisodeFinished(episode.id, collectedBeansCount, mysteryClue.isFound, calculatedStars)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = AvoGreenPrimary),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(text = "Continue", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
