package com.mineinabyss.components.helpers

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component

class PlayerCompassBar {
    var compassBar: BossBar = BossBar.bossBar(
        Component.text(":arrow_null:"), 1.0f, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS
    )
}
