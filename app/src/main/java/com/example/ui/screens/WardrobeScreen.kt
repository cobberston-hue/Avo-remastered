package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
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
import com.example.model.Costume
import com.example.model.CostumeType
import com.example.ui.AvoCharacter
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun WardrobeScreen(
    costumes: List<Costume>,
    totalBeans: Int,
    currentEquippedType: CostumeType,
    audioManager: AvoAudioManager,
    onEquipCostume: (CostumeType) -> Unit,
    onUnlockCostume: (CostumeType, cost: Int) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var previewCostume by remember { mutableStateOf(currentEquippedType) }
    val bounceHop = remember { Animatable(0f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0F1C13), Color(0xFF192C1F), Color(0xFF0A140D))
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
                horizontalArrangement = Arrangement.SpaceBetween,
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

                Text(
                    text = "Avo's Wardrobe",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                // Total Beans Badge with authentic ic_bean
                Surface(
                    color = Color(0x44000000),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x44FFC83B))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_bean),
                            contentDescription = "Beans",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$totalBeans",
                            color = BeanGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Big Live Interactive Character Stage
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable {
                        audioManager.playAvoSqueak()
                        coroutineScope.launch {
                            bounceHop.animateTo(1f, tween(120))
                            bounceHop.animateTo(0f, tween(120))
                        }
                    }
                ) {
                    AvoCharacter(
                        size = 130.dp,
                        costume = previewCostume,
                        isWalking = false,
                        bounceHop = bounceHop.value
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Original 3D Model • Tap Avo to squeak!",
                        color = AvoGreenLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Costumes Grid
            Text(
                text = "Choose Costume / Hat",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(costumes) { costume ->
                    val isEquipped = costume.type == currentEquippedType
                    val isPreviewed = costume.type == previewCostume

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                previewCostume = costume.type
                                audioManager.playUiButton()
                            },
                        color = if (isPreviewed) Color(0xFF223827) else Color(0xFF142419),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            if (isPreviewed) 2.dp else 1.dp,
                            if (isEquipped) AvoGreenBright else if (isPreviewed) Color.White else Color(0x22FFFFFF)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Icon Preview using extracted icon
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x33000000)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = costume.iconResId),
                                    contentDescription = costume.name,
                                    modifier = Modifier.size(42.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = costume.name,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = costume.description,
                                color = Color(0xFFA1B8A5),
                                fontSize = 10.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Action Button
                            if (isEquipped) {
                                Surface(
                                    color = Color(0x3382C838),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = AvoGreenBright,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "Equipped", color = AvoGreenBright, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else if (costume.isUnlocked) {
                                Button(
                                    onClick = {
                                        audioManager.playAvoSqueak()
                                        onEquipCostume(costume.type)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AvoGreenPrimary),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text(text = "Equip", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                val canAfford = totalBeans >= costume.costBeans
                                Button(
                                    onClick = {
                                        if (canAfford) {
                                            audioManager.playCelebration()
                                            onUnlockCostume(costume.type, costume.costBeans)
                                        } else {
                                            audioManager.playSparkDrop()
                                        }
                                    },
                                    enabled = canAfford,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = BeanGold,
                                        disabledContainerColor = Color(0xFF333333)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        modifier = Modifier.size(12.dp),
                                        tint = if (canAfford) Color.Black else Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${costume.costBeans}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (canAfford) Color.Black else Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
