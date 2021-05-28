package com.derongan.minecraft.mineinabyss.ecs.systems

import com.derongan.minecraft.deeperworld.event.PlayerAscendEvent
import com.derongan.minecraft.deeperworld.event.PlayerChangeSectionEvent
import com.derongan.minecraft.deeperworld.event.PlayerDescendEvent
import com.derongan.minecraft.mineinabyss.configuration.MIAConfig
import com.derongan.minecraft.mineinabyss.ecs.components.DescentContext
import com.mineinabyss.geary.minecraft.access.geary
import com.mineinabyss.idofront.messaging.color
import com.mineinabyss.idofront.messaging.info
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import java.time.Duration
import java.time.Instant
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.toKotlinDuration

object OrthReturnSystem : Listener {
    @EventHandler
    fun PlayerAscendEvent.onReturnToOrth() {
         if (toSection != MIAConfig.data.hubSection) return
        removeDescentContext(player)
    }

    @EventHandler
    fun PlayerDescendEvent.onDescend() {
        if (fromSection != MIAConfig.data.hubSection) return
        geary(player).set(DescentContext())
    }

    @OptIn(ExperimentalTime::class)
    fun removeDescentContext(player: Player) {
        val gearyPlayer = geary(player)
        val delve = gearyPlayer.get<DescentContext>() ?: return

        player.info(
            """
                &lWelcome back to Orth!
            &7Run lasted ${Duration.between(delve.startDate.toInstant(), Instant.now()).toKotlinDuration()}
        """.trimIndent().color()
        )
        gearyPlayer.remove<DescentContext>()
    }
}
