package com.mineinabyss.anticheese

import com.mineinabyss.idofront.plugin.isPluginEnabled
import com.mineinabyss.idofront.plugin.registerEvents
import com.mineinabyss.mineinabyss.core.AbyssFeature
import com.mineinabyss.mineinabyss.core.MineInAbyssPlugin
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("anticheese")
class AntiCheeseFeature : AbyssFeature {
    override fun MineInAbyssPlugin.enableFeature() {
        if (isPluginEnabled("GSit")) registerEvents(GSitListener())
        registerEvents(AntiCheeseListener())
    }
}
