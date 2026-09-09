package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.audio.AvoAudioManager
import com.example.model.CostumeType
import com.example.model.Episode
import com.example.ui.AvoCharacter
import com.example.ui.AvoVideoPlayer
import com.example.ui.theme.*

@Composable
fun MainMenuScreen(
    episodes: List<Episode>,
    totalBeans: Int,
    totalStars: Int,
    equippedCostume: CostumeType,
    audioManager: AvoAudioManager,
    onSelectEpisode: (Episode) -> Unit,
    onOpenTheater: () -> Unit,
    onOpenWardrobe: () -> Unit,
    onOpenArchive: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isMusicOn by remember { mutableStateOf(audioManager.isMusicEnabled) }
    var isSoundOn by remember { mutableStateOf(audioManager.isSoundEnabled) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 1. Authentic Original Background Menu Video Loop (MenuVideo.mp4 from original IPA)
        AvoVideoPlayer(
            videoResId = R.raw.menu_video,
            modifier = Modifier.fillMaxSize(),
            isLooping = true,
            isMuted = true, // Play the authentic game audio track separately
            isPlaying = true
        )

        // Semi-transparent overlay to ensure crisp UI legibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xCC08120B),
                            Color(0x660B1C10),
                            Color(0xEE060C08)
                        )
                    )
                )
        )

        // Menu Interface
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 36.dp)
        ) {
            // Header Bar with Original Playdeo Archive Badge, Beans, Stars, & Audio
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Authentic Status Badge
                    Surface(
                        color = Color(0x77000000),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x5582C838))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(AvoGreenBright)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ORIGINAL GAME PRESERVATION",
                                color = AvoGreenBright,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    // Stat Counters with extracted bean & star icons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Beans Counter with extracted ic_bean.png
                        Surface(
                            color = Color(0x77000000),
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x55FFC83B))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_bean),
                                    contentDescription = "Jellybeans",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "$totalBeans",
                                    color = BeanGold,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Stars Counter with extracted ic_star.png
                        Surface(
                            color = Color(0x77000000),
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x55FFD700))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_star),
                                    contentDescription = "Stars",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "$totalStars/${episodes.size * 3}",
                                    color = Color(0xFFFFE082),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Music Toggle
                        IconButton(
                            onClick = {
                                isMusicOn = !isMusicOn
                                audioManager.isMusicEnabled = isMusicOn
                                audioManager.playUiButton()
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isMusicOn) Icons.Default.MusicNote else Icons.Default.MusicOff,
                                contentDescription = "Music",
                                tint = if (isMusicOn) AvoGreenLight else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Hero Brand Presentation featuring the Original 3D Avo Model
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(128.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0x6682C838), Color(0x00000000))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        AvoCharacter(
                            size = 110.dp,
                            costume = equippedCostume,
                            isWalking = false
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "AVO!",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )

                    Text(
                        text = "Playdeo 1:1 Remaster & Archive",
                        fontSize = 14.sp,
                        color = AvoGreenLight,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Faithful 1080x1920 25 FPS Video Plates • Original 3D Character Model • Authentic Soundscape",
                        fontSize = 11.sp,
                        color = Color(0xFFA5C4AC),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }

            // Quick Mode Selector Cards with extracted icons
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Cutscene Theater Card
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                audioManager.playUiButton()
                                onOpenTheater()
                            },
                        color = Color(0xDD15261A),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x444ADE80))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_play_card),
                                contentDescription = "Theater",
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Cutscenes",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "All 9 Plates",
                                color = Color(0xFF9CB8A2),
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Avo Wardrobe Card
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                audioManager.playUiButton()
                                onOpenWardrobe()
                            },
                        color = Color(0xDD15261A),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x44FFC83B))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_customizer),
                                contentDescription = "Wardrobe",
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Wardrobe",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Hats & Skins",
                                color = Color(0xFF9CB8A2),
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Archive & History Card
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                audioManager.playUiButton()
                                onOpenArchive()
                            },
                        color = Color(0xDD15261A),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x4438D3FF))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.avo_doodle_thinking),
                                contentDescription = "Archive",
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Archive",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Playdeo Lore",
                                color = Color(0xFF9CB8A2),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // Episode List Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Episodes (Television You Touch)",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${episodes.size} Full Chapters",
                        color = AvoGreenLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Episode Cards List
            items(episodes) { ep ->
                EpisodeItemCard(
                    episode = ep,
                    onClick = {
                        if (ep.isUnlocked) {
                            audioManager.playUiButton()
                            onSelectEpisode(ep)
                        } else {
                            audioManager.playSparkDrop()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun EpisodeItemCard(
    episode: Episode,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 6.dp)
            .clickable(enabled = episode.isUnlocked) { onClick() },
        color = if (episode.isUnlocked) Color(0xEE16291C) else Color(0xAA111913),
        shape = RoundedCornerShape(18.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (episode.isUnlocked) Color(0x4482C838) else Color(0x22FFFFFF)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Episode Number Badge
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (episode.isUnlocked)
                            Brush.linearGradient(listOf(AvoGreenPrimary, AvoGreenDark))
                        else
                            Brush.linearGradient(listOf(Color(0xFF2A362D), Color(0xFF1B241E)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (episode.isUnlocked) {
                    Text(
                        text = "${episode.number}",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Episode Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = episode.title,
                    color = if (episode.isUnlocked) Color.White else Color(0xFF888888),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "📍 ${episode.location} • Full Live Plate",
                    color = AvoGreenLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = episode.description,
                    color = Color(0xFFA1B5A6),
                    fontSize = 11.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Stars Display using original ic_star
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(3) { starIndex ->
                        val earned = starIndex < episode.starsEarned
                        Image(
                            painter = painterResource(id = R.drawable.ic_star),
                            contentDescription = "Star",
                            modifier = Modifier
                                .size(14.dp)
                                .padding(end = 2.dp),
                            alpha = if (earned) 1f else 0.25f
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_bean),
                        contentDescription = "Bean",
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${episode.beans.size} Sparks",
                        fontSize = 11.sp,
                        color = BeanGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Arrow action
            if (episode.isUnlocked) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = AvoGreenBright,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
