package com.mineinabyss.cosmetics

import com.destroystokyo.paper.MaterialTags
import com.mineinabyss.components.cosmetics.cosmetics
import com.mineinabyss.components.players.Backpack
import com.mineinabyss.geary.papermc.access.toGeary
import com.mineinabyss.helpers.equipCosmeticBackpack
import com.mineinabyss.helpers.getCosmeticBackpack
import com.mineinabyss.helpers.unEquipCosmeticBackpack
import com.mineinabyss.idofront.entities.rightClicked
import com.mineinabyss.idofront.serialization.toSerializable
import org.bukkit.Material
import org.bukkit.block.ShulkerBox
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BlockStateMeta

class CosmeticListener : Listener {

    // Cancel MCCosmetics backpack equip if player isn't wearing a backpack
//    @EventHandler
//    fun CosmeticEquipEvent.onEquipBackpack() {
//
//    }
//
//    @EventHandler
//    fun CosmeticUnequipEvent.onUnequipBackpack() {
//
//    }

    @EventHandler
    fun PlayerInteractEvent.equipBackpack() {
        if (player.isSneaking && rightClicked) {
            // Put backpack on and store backpack-inv
            if (player.getCosmeticBackpack().type == Material.AIR) {
                // Get the color of the players backpack, or non-colored, from the material
                val i = item?.type.toString().lowercase()
                val type = if (!i.startsWith("shulker")) i.replace("_shulker_box", "") else i
                val meta = (item?.itemMeta as? BlockStateMeta)?.blockState as? ShulkerBox ?: return

                isCancelled = true
                val backpack = player.toGeary().getOrSetPersisting { Backpack() }
                meta.inventory.filterNotNull().forEach {
                    backpack.backpackContent?.add(it.toSerializable())
                }
                item?.subtract(1)

                // Use default color backpack or custom one if specified so by the component
                if (player.cosmetics.cosmeticBackpack == null) {
                    if (MaterialTags.SHULKER_BOXES.isTagged(item!!)) player.equipCosmeticBackpack("default_yellow")
                    else player.equipCosmeticBackpack("default_$type")
                }
                else player.equipCosmeticBackpack(player.cosmetics.cosmeticBackpack!!)
            }

            // Remove backpack and refill backpack with inventory
            else {
                // Create a new itemstack of the cosmetic backpack
                val inv = player.inventory
                val item = ItemStack(player.getCosmeticBackpack())
                val bsm = (item.itemMeta as? BlockStateMeta) ?: return
                val box = bsm.blockState as? ShulkerBox ?: return

                isCancelled = true

                // Add all the items on the component back into the backpack
                player.toGeary().get<Backpack>()?.backpackContent?.forEach {
                    box.inventory.addItem(it.toItemStack())
                }
                bsm.blockState = box
                item.itemMeta = bsm
                if (inv.itemInMainHand.type == Material.AIR)
                    inv.setItemInMainHand(item)
                else if (inv.itemInOffHand.type == Material.AIR)
                    inv.setItemInOffHand(item)
                else if (inv.firstEmpty() != -1)
                    inv.setItem(inv.firstEmpty(), item)
                else return

                // Unequip the backpack from the player
                // Move to Unequip-event when implemented
                player.unEquipCosmeticBackpack()
                player.toGeary().get<Backpack>()?.backpackContent?.clear()
                player.toGeary().remove<Backpack>()
            }
        }
    }
}
