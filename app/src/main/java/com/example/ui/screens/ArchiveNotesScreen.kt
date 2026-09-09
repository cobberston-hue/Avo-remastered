package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AvoAudioManager
import com.example.ui.AvoCharacter
import com.example.ui.theme.*

@Composable
fun ArchiveNotesScreen(
    audioManager: AvoAudioManager,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0C1610), Color(0xFF142419), Color(0xFF09120B))
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Playdeo Preservation Archive",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Scrollable Content
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Header card
                    Surface(
                        color = Color(0xFF192E20),
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AvoGreenBright)
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AvoCharacter(size = 72.dp)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Avo! by Playdeo",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "First Released: February 2019",
                                    color = AvoGreenLight,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "Genre: Interactive Live-Action Television",
                                    color = Color(0xFFB5D4BD),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                item {
                    ArchiveSectionCard(
                        title = "1. Preservation Mission",
                        description = "Playdeo was founded in London by Jack Schulze and Timo Arnall to reinvent television into an interactive medium you can physically touch. Following the studio's closure and the app's removal from store listings, this preservation remaster ensures that the ground-breaking episodic masterpiece remains accessible for future generations to enjoy."
                    )
                }

                item {
                    ArchiveSectionCard(
                        title = "2. Strict Fidelity Mandate",
                        description = "In strict accordance with digital preservation principles, all video plates are presented directly from Playdeo's original production cuts at their native 25 frames per second.\n\nNo AI upscaling, hallucination, or synthetic frame interpolation was applied. Every shot retains its original film texture, natural camera motion blur, and precise lighting continuity."
                    )
                }

                item {
                    ArchiveSectionCard(
                        title = "3. Original Wwise Audio Extraction",
                        description = "Sound effects and music tracks were extracted directly from the original SoundBank containers (COMMON.bnk & SoundbanksInfo.xml). You are hearing the authentic acoustic Foley: Avo's wooden waddling steps, authentic avocado vocal squeaks, and the dynamic musical score composed specifically for Billie and Avo."
                    )
                }

                item {
                    ArchiveSectionCard(
                        title = "4. Creative Team & Cast",
                        description = "• Billie: Played with wonder and warmth by Katy Reece.\n• Screenplay: Ryan North (Marvel's The Unbeatable Squirrel Girl, Adventure Time, Dinosaur Comics).\n• Creative Direction: Jack Schulze & Timo Arnall.\n• Production: Playdeo Studio, London, UK."
                    )
                }

                item {
                    ArchiveSectionCard(
                        title = "5. Gameplay Mechanics",
                        description = "Playdeo invented 'Television you can touch':\n• Drag your finger across the live-action plate to illuminate a path of golden sparks.\n• Avo follows your path through the real-world lab benches and countertops.\n• Gather hidden energy sparks and find Billie's investigation clues in every episode."
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Preserved with respect for the artists, actors, and engineers at Playdeo.",
                        color = Color(0xFF8FA895),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ArchiveSectionCard(
    title: String,
    description: String
) {
    Surface(
        color = Color(0xFF15251A),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x334ADE80))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = AvoGreenBright,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                color = Color(0xFFD4E6D8),
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}
