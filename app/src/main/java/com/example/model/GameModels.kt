package com.example.model

import com.example.R

data class JellyBean(
    val id: Int,
    val x: Float, // Normalized 0f..1f (width)
    val y: Float, // Normalized 0f..1f (height)
    val color: Long,
    val isCollected: Boolean = false
)

data class MysteryClue(
    val name: String,
    val description: String,
    val x: Float,
    val y: Float,
    val isFound: Boolean = false,
    val emoji: String = "🔍"
)

data class CameraZone(
    val id: String,
    val name: String,
    val startMs: Int,
    val endMs: Int,
    val boundsMinX: Float, // Normalized 0..1 coordinate bounds on screen
    val boundsMaxX: Float,
    val boundsMinY: Float,
    val boundsMaxY: Float,
    val focusCenterX: Float,
    val focusCenterY: Float
)

data class Episode(
    val id: Int,
    val number: Int,
    val title: String,
    val location: String,
    val description: String,
    val videoResId: Int,
    val durationSeconds: Int,
    val beans: List<JellyBean>,
    val mysteryClue: MysteryClue,
    val cameraZones: List<CameraZone> = emptyList(),
    val isUnlocked: Boolean = false,
    val starsEarned: Int = 0,
    val maxStars: Int = 3
)

enum class CostumeType {
    CLASSIC,
    DETECTIVE,
    BOWLER,
    SOMBRERO,
    TIARA,
    POLICE,
    ROCKET
}

data class Costume(
    val type: CostumeType,
    val name: String,
    val description: String,
    val iconResId: Int,
    val costBeans: Int,
    val isUnlocked: Boolean = false,
    val isEquipped: Boolean = false
)

object EpisodeData {
    val sampleCostumes = listOf(
        Costume(
            CostumeType.CLASSIC,
            "Classic Avo",
            "The original bare avocado half brought to life by Billie.",
            R.drawable.ic_avo_body,
            0,
            isUnlocked = true,
            isEquipped = true
        ),
        Costume(
            CostumeType.DETECTIVE,
            "Sherlock Fedora",
            "Deerstalker detective hat extracted from the original game files.",
            R.drawable.ic_hat_sherlock,
            20,
            isUnlocked = true,
            isEquipped = false
        ),
        Costume(
            CostumeType.BOWLER,
            "Gentleman Bowler",
            "Classic felt bowler hat with a clean satin ribbon.",
            R.drawable.ic_hat_bowler,
            35,
            isUnlocked = false,
            isEquipped = false
        ),
        Costume(
            CostumeType.SOMBRERO,
            "Fiesta Sombrero",
            "Wide-brimmed festive sombrero with hand-woven embroidery.",
            R.drawable.ic_hat_sombrero,
            50,
            isUnlocked = false,
            isEquipped = false
        ),
        Costume(
            CostumeType.TIARA,
            "Royal Tiara",
            "Gleaming jeweled tiara fitting for royalty.",
            R.drawable.ic_hat_tiara,
            65,
            isUnlocked = false,
            isEquipped = false
        ),
        Costume(
            CostumeType.POLICE,
            "Inspector Cap",
            "Official detective inspector peaked cap with golden badge.",
            R.drawable.ic_hat_police,
            80,
            isUnlocked = false,
            isEquipped = false
        ),
        Costume(
            CostumeType.ROCKET,
            "Rocket Helmet",
            "Retro-futuristic space helmet with chrome antennas.",
            R.drawable.ic_hat_rocket,
            100,
            isUnlocked = false,
            isEquipped = false
        )
    )

    fun createInitialEpisodes(): List<Episode> {
        return listOf(
            Episode(
                id = 1,
                number = 1,
                title = "Awakening in the Lab",
                location = "Billie's Workbench",
                description = "Billie's first breakthrough! Touch the screen to draw a path and guide Avo across the workshop bench to collect glowing sparks.",
                videoResId = R.raw.ep1_lab_bench,
                durationSeconds = 60,
                beans = listOf(
                    JellyBean(101, 0.28f, 0.45f, 0xFFFFC83B),
                    JellyBean(102, 0.42f, 0.52f, 0xFFFF5277),
                    JellyBean(103, 0.65f, 0.48f, 0xFF38D3FF),
                    JellyBean(104, 0.52f, 0.68f, 0xFFA855F7),
                    JellyBean(105, 0.78f, 0.72f, 0xFF4ADE80),
                    JellyBean(106, 0.35f, 0.78f, 0xFFFFC83B)
                ),
                mysteryClue = MysteryClue(
                    name = "Billie's Magnifying Glass",
                    description = "Found near the blueprint notes on the lab bench.",
                    x = 0.72f,
                    y = 0.38f,
                    emoji = "🔍"
                ),
                cameraZones = listOf(
                    CameraZone("z_wide", "Workbench Overview", 0, 5720, 0.0f, 1.0f, 0.65f, 1.0f, 0.5f, 0.8f),
                    CameraZone("z_notes", "Computer & Notes", 5720, 18760, 0.0f, 0.45f, 0.2f, 0.65f, 0.25f, 0.45f),
                    CameraZone("z_chem", "Chemistry Apparatus", 18760, 31720, 0.45f, 0.8f, 0.2f, 0.65f, 0.62f, 0.45f),
                    CameraZone("z_teleport", "Teleporter Stage", 31720, 45720, 0.65f, 1.0f, 0.4f, 0.85f, 0.82f, 0.62f),
                    CameraZone("z_close", "Macro Discovery", 45720, 60000, 0.3f, 0.7f, 0.4f, 0.8f, 0.5f, 0.6f)
                ),
                isUnlocked = true,
                starsEarned = 0
            ),
            Episode(
                id = 2,
                number = 2,
                title = "Kitchen Counter Experiments",
                location = "Kitchen Island",
                description = "Morning breakfast is interrupted by science. Navigate the kitchen marble counter and avoid the spilled orange juice.",
                videoResId = R.raw.ep2_kitchen_counter,
                durationSeconds = 60,
                beans = listOf(
                    JellyBean(201, 0.22f, 0.40f, 0xFF38D3FF),
                    JellyBean(202, 0.48f, 0.42f, 0xFFFFC83B),
                    JellyBean(203, 0.68f, 0.55f, 0xFFFF5277),
                    JellyBean(204, 0.35f, 0.65f, 0xFF4ADE80),
                    JellyBean(205, 0.80f, 0.62f, 0xFFA855F7),
                    JellyBean(206, 0.55f, 0.80f, 0xFFFFC83B)
                ),
                mysteryClue = MysteryClue(
                    name = "Secret Recipe Note",
                    description = "A folded napkin containing electrolyte formula.",
                    x = 0.25f,
                    y = 0.60f,
                    emoji = "📜"
                ),
                cameraZones = listOf(
                    CameraZone("z_counter_wide", "Island Panorama", 0, 7840, 0.0f, 1.0f, 0.6f, 1.0f, 0.5f, 0.8f),
                    CameraZone("z_toaster", "Toaster & Cutting Board", 7840, 20360, 0.0f, 0.5f, 0.2f, 0.65f, 0.28f, 0.45f),
                    CameraZone("z_sink", "Marble Basin & Sink", 20360, 36240, 0.5f, 1.0f, 0.2f, 0.65f, 0.72f, 0.45f),
                    CameraZone("z_close_fruit", "Fruit Basket Macro", 36240, 60000, 0.3f, 0.7f, 0.4f, 0.8f, 0.5f, 0.6f)
                ),
                isUnlocked = true,
                starsEarned = 0
            ),
            Episode(
                id = 3,
                number = 3,
                title = "The Secret Prototype",
                location = "Living Room Workshop",
                description = "Billie tests the electromagnetic prototype. Guide Avo along the coffee table to power up the circuit.",
                videoResId = R.raw.ep3_living_room_device,
                durationSeconds = 60,
                beans = listOf(
                    JellyBean(301, 0.30f, 0.38f, 0xFFFFC83B),
                    JellyBean(302, 0.50f, 0.50f, 0xFF38D3FF),
                    JellyBean(303, 0.72f, 0.44f, 0xFFFF5277),
                    JellyBean(304, 0.25f, 0.70f, 0xFFA855F7),
                    JellyBean(305, 0.62f, 0.75f, 0xFF4ADE80),
                    JellyBean(306, 0.82f, 0.65f, 0xFFFFC83B)
                ),
                mysteryClue = MysteryClue(
                    name = "Glow Reactor Battery",
                    description = "A glowing mini-battery powering the gadget.",
                    x = 0.52f,
                    y = 0.32f,
                    emoji = "🔋"
                ),
                cameraZones = listOf(
                    CameraZone("z_living_wide", "Living Room Establishing", 0, 10200, 0.0f, 1.0f, 0.6f, 1.0f, 0.5f, 0.8f),
                    CameraZone("z_device_table", "Electromagnetic Device", 10200, 26400, 0.3f, 0.7f, 0.2f, 0.6f, 0.5f, 0.38f),
                    CameraZone("z_sofa_side", "Sofa Cushion Stash", 26400, 42800, 0.0f, 0.4f, 0.4f, 0.85f, 0.25f, 0.65f),
                    CameraZone("z_circuit_board", "Overhead Circuit Map", 42800, 60000, 0.6f, 1.0f, 0.4f, 0.85f, 0.75f, 0.65f)
                ),
                isUnlocked = true,
                starsEarned = 0
            ),
            Episode(
                id = 4,
                number = 4,
                title = "The Lab Break-In",
                location = "Crime Scene",
                description = "Someone sneaked in overnight! Trace the mysterious footprints across the crime scene and gather evidence.",
                videoResId = R.raw.ep4_breakin_investigation,
                durationSeconds = 60,
                beans = listOf(
                    JellyBean(401, 0.18f, 0.48f, 0xFFFF5277),
                    JellyBean(402, 0.38f, 0.36f, 0xFF38D3FF),
                    JellyBean(403, 0.58f, 0.58f, 0xFFFFC83B),
                    JellyBean(404, 0.75f, 0.46f, 0xFFA855F7),
                    JellyBean(405, 0.32f, 0.75f, 0xFF4ADE80),
                    JellyBean(406, 0.85f, 0.78f, 0xFFFFC83B)
                ),
                mysteryClue = MysteryClue(
                    name = "Intruder's Keycard",
                    description = "A strange badge dropped near the lab doorway.",
                    x = 0.76f,
                    y = 0.70f,
                    emoji = "💳"
                ),
                cameraZones = listOf(
                    CameraZone("z_crime_wide", "Shattered Doorway", 0, 8900, 0.0f, 1.0f, 0.6f, 1.0f, 0.5f, 0.8f),
                    CameraZone("z_footprints", "Fluorescent Footprints", 8900, 24100, 0.1f, 0.5f, 0.3f, 0.75f, 0.3f, 0.55f),
                    CameraZone("z_safe", "Cracked Vault Stash", 24100, 41500, 0.5f, 0.95f, 0.3f, 0.75f, 0.72f, 0.55f),
                    CameraZone("z_clue_detail", "Badge Fingerprint Scan", 41500, 60000, 0.3f, 0.7f, 0.2f, 0.6f, 0.5f, 0.4f)
                ),
                isUnlocked = true,
                starsEarned = 0
            ),
            Episode(
                id = 5,
                number = 5,
                title = "The Corridor Pursuit",
                location = "Apartment Hallway",
                description = "Catch the thief! Chase down the hallway, dodging floor obstacles and picking up turbo sparks.",
                videoResId = R.raw.ep5_corridor_chase,
                durationSeconds = 60,
                beans = listOf(
                    JellyBean(501, 0.45f, 0.35f, 0xFFFFC83B),
                    JellyBean(502, 0.35f, 0.50f, 0xFF38D3FF),
                    JellyBean(503, 0.60f, 0.52f, 0xFFFF5277),
                    JellyBean(504, 0.42f, 0.68f, 0xFF4ADE80),
                    JellyBean(505, 0.70f, 0.72f, 0xFFA855F7),
                    JellyBean(506, 0.28f, 0.82f, 0xFFFFC83B)
                ),
                mysteryClue = MysteryClue(
                    name = "Dropped USB Drive",
                    description = "Contains blueprints for Billie's stolen device.",
                    x = 0.46f,
                    y = 0.24f,
                    emoji = "💾"
                ),
                cameraZones = listOf(
                    CameraZone("z_corridor_wide", "Hallway Pursuit Sprint", 0, 9500, 0.0f, 1.0f, 0.6f, 1.0f, 0.5f, 0.8f),
                    CameraZone("z_doors_mid", "Apartment Doorways", 9500, 25200, 0.2f, 0.8f, 0.3f, 0.7f, 0.5f, 0.5f),
                    CameraZone("z_elevator", "Elevator Landing Chase", 25200, 43600, 0.0f, 0.5f, 0.2f, 0.6f, 0.3f, 0.4f),
                    CameraZone("z_exit_turn", "Emergency Stairs Turn", 43600, 60000, 0.5f, 1.0f, 0.2f, 0.6f, 0.75f, 0.4f)
                ),
                isUnlocked = true,
                starsEarned = 0
            ),
            Episode(
                id = 6,
                number = 6,
                title = "The Greenhouse Maze",
                location = "Botanical Conservatory",
                description = "Deep in the botanical garden. Follow the path between exotic plants and find the ventilation switch.",
                videoResId = R.raw.ep6_greenhouse,
                durationSeconds = 60,
                beans = listOf(
                    JellyBean(601, 0.25f, 0.42f, 0xFF4ADE80),
                    JellyBean(602, 0.48f, 0.38f, 0xFFFFC83B),
                    JellyBean(603, 0.72f, 0.50f, 0xFF38D3FF),
                    JellyBean(604, 0.32f, 0.62f, 0xFFFF5277),
                    JellyBean(605, 0.58f, 0.74f, 0xFFA855F7),
                    JellyBean(606, 0.80f, 0.82f, 0xFFFFC83B)
                ),
                mysteryClue = MysteryClue(
                    name = "Golden Orchid Blossom",
                    description = "A rare flower revealing secret passage coordinates.",
                    x = 0.80f,
                    y = 0.40f,
                    emoji = "🌸"
                ),
                cameraZones = listOf(
                    CameraZone("z_green_wide", "Canopy Overview", 0, 8500, 0.0f, 1.0f, 0.6f, 1.0f, 0.5f, 0.8f),
                    CameraZone("z_ferns", "Giant Fern Foliage", 8500, 23800, 0.0f, 0.5f, 0.2f, 0.65f, 0.25f, 0.45f),
                    CameraZone("z_waterfall", "Mist Waterfall Pool", 23800, 41200, 0.5f, 1.0f, 0.2f, 0.65f, 0.75f, 0.45f),
                    CameraZone("z_orchid_close", "Golden Orchid Macro", 41200, 60000, 0.3f, 0.7f, 0.3f, 0.7f, 0.5f, 0.5f)
                ),
                isUnlocked = true,
                starsEarned = 0
            ),
            Episode(
                id = 7,
                number = 7,
                title = "Mastermind's Lair",
                location = "Secret Tech Facility",
                description = "Infiltrate the villain's underground control center. Deactivate security lasers and reach the terminal.",
                videoResId = R.raw.ep7_mastermind_lair,
                durationSeconds = 60,
                beans = listOf(
                    JellyBean(701, 0.20f, 0.36f, 0xFFFF5277),
                    JellyBean(702, 0.45f, 0.48f, 0xFF38D3FF),
                    JellyBean(703, 0.70f, 0.40f, 0xFFFFC83B),
                    JellyBean(704, 0.30f, 0.68f, 0xFFA855F7),
                    JellyBean(705, 0.60f, 0.70f, 0xFF4ADE80),
                    JellyBean(706, 0.85f, 0.62f, 0xFFFFC83B)
                ),
                mysteryClue = MysteryClue(
                    name = "Master Override Key",
                    description = "Shuts down the malicious surveillance network.",
                    x = 0.38f,
                    y = 0.28f,
                    emoji = "🔑"
                ),
                cameraZones = listOf(
                    CameraZone("z_lair_wide", "Laser Grid Chamber", 0, 11400, 0.0f, 1.0f, 0.6f, 1.0f, 0.5f, 0.8f),
                    CameraZone("z_servers", "Supercomputer Rack Bank", 11400, 27600, 0.0f, 0.5f, 0.2f, 0.65f, 0.25f, 0.45f),
                    CameraZone("z_lasers", "Red Security Beam Hub", 27600, 44200, 0.5f, 1.0f, 0.2f, 0.65f, 0.75f, 0.45f),
                    CameraZone("z_terminal", "Mainframe Key Terminal", 44200, 60000, 0.3f, 0.7f, 0.2f, 0.6f, 0.5f, 0.4f)
                ),
                isUnlocked = true,
                starsEarned = 0
            ),
            Episode(
                id = 8,
                number = 8,
                title = "The Grand Finale",
                location = "Rooftop Observatory",
                description = "The ultimate showdown! Billie and Avo join forces to save the laboratory network. The final triumph!",
                videoResId = R.raw.ep8_grand_finale,
                durationSeconds = 60,
                beans = listOf(
                    JellyBean(801, 0.22f, 0.42f, 0xFFFFC83B),
                    JellyBean(802, 0.42f, 0.36f, 0xFF38D3FF),
                    JellyBean(803, 0.65f, 0.45f, 0xFFFF5277),
                    JellyBean(804, 0.30f, 0.64f, 0xFF4ADE80),
                    JellyBean(805, 0.55f, 0.72f, 0xFFA855F7),
                    JellyBean(806, 0.78f, 0.78f, 0xFFFFC83B)
                ),
                mysteryClue = MysteryClue(
                    name = "Golden Friendship Medal",
                    description = "Awarded to Avo and Billie for saving the day.",
                    x = 0.50f,
                    y = 0.22f,
                    emoji = "🏆"
                ),
                cameraZones = listOf(
                    CameraZone("z_finale_wide", "Rooftop Dome Vista", 0, 9200, 0.0f, 1.0f, 0.6f, 1.0f, 0.5f, 0.8f),
                    CameraZone("z_telescope", "Giant Refractor Telescope", 9200, 26000, 0.0f, 0.5f, 0.2f, 0.65f, 0.25f, 0.45f),
                    CameraZone("z_antenna", "Skyline Microwave Transmitter", 26000, 43500, 0.5f, 1.0f, 0.2f, 0.65f, 0.75f, 0.45f),
                    CameraZone("z_medal_win", "Celebratory Podium Stage", 43500, 60000, 0.3f, 0.7f, 0.2f, 0.6f, 0.5f, 0.4f)
                ),
                isUnlocked = true,
                starsEarned = 0
            ),
            Episode(
                id = 9,
                number = 9,
                title = "Prologue & Origins",
                location = "Billie's Childhood Workshop",
                description = "The unseen origin story of Billie's inventions and the first spark of organic animation.",
                videoResId = R.raw.ep9_prologue,
                durationSeconds = 60,
                beans = listOf(
                    JellyBean(901, 0.24f, 0.40f, 0xFFFFC83B),
                    JellyBean(902, 0.45f, 0.48f, 0xFF38D3FF),
                    JellyBean(903, 0.70f, 0.42f, 0xFFFF5277),
                    JellyBean(904, 0.32f, 0.68f, 0xFFA855F7),
                    JellyBean(905, 0.58f, 0.70f, 0xFF4ADE80),
                    JellyBean(906, 0.82f, 0.75f, 0xFFFFC83B)
                ),
                mysteryClue = MysteryClue(
                    name = "First Prototype Sketch",
                    description = "Hand-drawn sketch of the very first walking avocado prototype.",
                    x = 0.50f,
                    y = 0.30f,
                    emoji = "📐"
                ),
                cameraZones = listOf(
                    CameraZone("z_prologue_wide", "Childhood Wooden Desk", 0, 8800, 0.0f, 1.0f, 0.6f, 1.0f, 0.5f, 0.8f),
                    CameraZone("z_sketchbook", "Notebook & Crayon Drawings", 8800, 24500, 0.0f, 0.5f, 0.2f, 0.65f, 0.25f, 0.45f),
                    CameraZone("z_clockwork", "Miniature Gear Mechanism", 24500, 42000, 0.5f, 1.0f, 0.2f, 0.65f, 0.75f, 0.45f),
                    CameraZone("z_spark_birth", "First Organic Life Spark", 42000, 60000, 0.3f, 0.7f, 0.3f, 0.7f, 0.5f, 0.5f)
                ),
                isUnlocked = true,
                starsEarned = 0
            )
        )
    }
}
