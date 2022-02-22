package com.mineinabyss.relics

import com.mineinabyss.components.relics.BoundingLance
import com.mineinabyss.geary.papermc.access.toGeary
import com.mineinabyss.geary.papermc.spawnFromPrefab
import com.mineinabyss.geary.papermc.toPrefabKey
import com.mineinabyss.helpers.dropItems
import com.mineinabyss.idofront.entities.rightClicked
import com.mineinabyss.idofront.util.toMCKey
import com.mineinabyss.looty.LootyFactory
import com.mineinabyss.looty.tracking.toGearyOrNull
import com.mineinabyss.mineinabyss.core.mineInAbyss
import com.okkero.skedule.schedule
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class BoundingLanceListener : Listener {

    @EventHandler
    fun PlayerInteractEvent.spawnBoundingLance() {
        if (hand != EquipmentSlot.HAND) return
        if (!rightClicked) return
        if (clickedBlock == null) return
        if (blockFace == BlockFace.DOWN) return
        val item = player.inventory.itemInMainHand
        val gearyItem = item.toGearyOrNull(player) ?: return
        val boundingLance = gearyItem.get<BoundingLance>() ?: return
        val loc = player.getLastTwoTargetBlocks(null, 6)

        val newLoc =
            if (blockFace == BlockFace.UP)
                loc.last()?.location?.toCenterLocation()?.apply { y += 0.5 } ?: return
            else
                loc.first()?.location?.toCenterLocation()?.apply { y -= 0.5 } ?: return

        val entityPrefab = "mineinabyss:boundinglance".toMCKey().toPrefabKey()
        val itemPrefab = "mineinabyss:bounding_lance".toMCKey().toPrefabKey()

        item.subtract(1)
        newLoc.spawnFromPrefab(entityPrefab)
        player.playSound(newLoc, boundingLance.placeSound, 1f, 1f)

        mineInAbyss.schedule {
            clickedBlock!!.location.getNearbyEntities(1.1, 1.1, 1.1).firstOrNull {
                it.toGeary().has<BoundingLance>()
            }?.let { entity,  ->
                val lance = entity.toGeary().get<BoundingLance>() ?: return@schedule
                var timePassed = 0L
                val lastTime = System.currentTimeMillis()
                do {
                    entity.world.spawnParticle(
                        lance.effectParticles.random(),
                        entity.location.toCenterLocation(),
                        100,
                        lance.effectRadius,
                        0.0,
                        lance.effectRadius,
                        0.0
                    )
                    timePassed += System.currentTimeMillis() - lastTime
                    waitFor(1)
                }while (timePassed < lance.effectDuration * 1000)
                waitFor(60)
                entity.remove()
                dropItems(entity.location.toCenterLocation(), LootyFactory.createFromPrefab(itemPrefab)!!)
            }
        }


    }
}

