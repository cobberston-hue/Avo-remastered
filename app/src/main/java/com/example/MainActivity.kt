package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.audio.AvoAudioManager
import com.example.model.Costume
import com.example.model.CostumeType
import com.example.model.Episode
import com.example.model.EpisodeData
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import kotlin.math.max

enum class AppScreen {
    MAIN_MENU,
    GAMEPLAY,
    THEATER,
    WARDROBE,
    ARCHIVE
}

class MainActivity : ComponentActivity() {
    private var audioManager: AvoAudioManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val audioMgr = AvoAudioManager(this)
        audioManager = audioMgr

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    AvoAppRoot(audioManager = audioMgr)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        audioManager?.resumeMusic()
    }

    override fun onPause() {
        super.onPause()
        audioManager?.pauseMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        audioManager?.release()
        audioManager = null
    }
}

@Composable
fun AvoAppRoot(audioManager: AvoAudioManager) {
    var currentScreen by remember { mutableStateOf(AppScreen.MAIN_MENU) }

    // Persistent gameplay state
    val episodes = remember { mutableStateListOf<Episode>().apply { addAll(EpisodeData.createInitialEpisodes()) } }
    val costumes = remember { mutableStateListOf<Costume>().apply { addAll(EpisodeData.sampleCostumes) } }
    var totalBeans by remember { mutableIntStateOf(35) } // Starting reward beans
    var activeEpisode by remember { mutableStateOf<Episode?>(null) }
    var equippedCostumeType by remember { mutableStateOf(CostumeType.CLASSIC) }

    val totalStars = episodes.sumOf { it.starsEarned }

    BackHandler(enabled = currentScreen != AppScreen.MAIN_MENU) {
        currentScreen = AppScreen.MAIN_MENU
    }

    when (currentScreen) {
        AppScreen.MAIN_MENU -> {
            MainMenuScreen(
                episodes = episodes,
                totalBeans = totalBeans,
                totalStars = totalStars,
                equippedCostume = equippedCostumeType,
                audioManager = audioManager,
                onSelectEpisode = { ep ->
                    activeEpisode = ep
                    currentScreen = AppScreen.GAMEPLAY
                },
                onOpenTheater = {
                    currentScreen = AppScreen.THEATER
                },
                onOpenWardrobe = {
                    currentScreen = AppScreen.WARDROBE
                },
                onOpenArchive = {
                    currentScreen = AppScreen.ARCHIVE
                }
            )
        }

        AppScreen.GAMEPLAY -> {
            val ep = activeEpisode ?: episodes.first()
            GameplayScreen(
                episode = ep,
                currentCostume = equippedCostumeType,
                audioManager = audioManager,
                onBack = {
                    currentScreen = AppScreen.MAIN_MENU
                },
                onEpisodeFinished = { epId, beansCollected, clueFound, stars ->
                    totalBeans += beansCollected

                    // Update episode stars & unlock next episode
                    val epIndex = episodes.indexOfFirst { it.id == epId }
                    if (epIndex >= 0) {
                        val currentEp = episodes[epIndex]
                        val updatedStars = max(currentEp.starsEarned, stars)
                        episodes[epIndex] = currentEp.copy(starsEarned = updatedStars)

                        // Unlock next episode if not unlocked
                        if (epIndex + 1 < episodes.size) {
                            val nextEp = episodes[epIndex + 1]
                            episodes[epIndex + 1] = nextEp.copy(isUnlocked = true)
                        }
                    }

                    currentScreen = AppScreen.MAIN_MENU
                },
                onOpenWardrobe = {
                    currentScreen = AppScreen.WARDROBE
                }
            )
        }

        AppScreen.THEATER -> {
            CutsceneTheaterScreen(
                episodes = episodes,
                initialEpisodeIndex = activeEpisode?.let { ep -> episodes.indexOfFirst { it.id == ep.id } } ?: 0,
                audioManager = audioManager,
                onBack = {
                    currentScreen = AppScreen.MAIN_MENU
                }
            )
        }

        AppScreen.WARDROBE -> {
            WardrobeScreen(
                costumes = costumes,
                totalBeans = totalBeans,
                currentEquippedType = equippedCostumeType,
                audioManager = audioManager,
                onEquipCostume = { type ->
                    equippedCostumeType = type
                },
                onUnlockCostume = { type, cost ->
                    totalBeans -= cost
                    val idx = costumes.indexOfFirst { it.type == type }
                    if (idx >= 0) {
                        costumes[idx] = costumes[idx].copy(isUnlocked = true)
                    }
                    equippedCostumeType = type
                },
                onBack = {
                    currentScreen = if (activeEpisode != null) AppScreen.GAMEPLAY else AppScreen.MAIN_MENU
                }
            )
        }

        AppScreen.ARCHIVE -> {
            ArchiveNotesScreen(
                audioManager = audioManager,
                onBack = {
                    currentScreen = AppScreen.MAIN_MENU
                }
            )
        }
    }
}
