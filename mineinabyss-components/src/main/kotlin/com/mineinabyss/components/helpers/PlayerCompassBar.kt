package com.mineinabyss.components.helpers

import com.mineinabyss.idofront.serialization.BossBarSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component

@Serializable
@SerialName("mineinabyss:player_compass_bar")
class PlayerCompassBar {
    var compassBar: @Serializable(with = BossBarSerializer::class) BossBar =
        BossBar.bossBar(Component.text(":arrow_null:"), 1.0f, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS)
}
