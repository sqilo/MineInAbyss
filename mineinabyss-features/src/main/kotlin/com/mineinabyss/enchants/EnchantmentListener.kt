package com.mineinabyss.enchants

import com.mineinabyss.idofront.messaging.broadcast
import com.mineinabyss.idofront.messaging.error
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.AnvilInventory
import org.bukkit.inventory.GrindstoneInventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta

//TODO Make vanilla handle pure vanilla books

class EnchantmentListener : Listener {

    @EventHandler
    fun InventoryClickEvent.applyCustomEnchantmentBook() {
        if (inventory.type != InventoryType.ANVIL) return
        val anvil = inventory as AnvilInventory

        val player = whoClicked as Player
        val enchanted =
            if (anvil.secondItem?.type == Material.ENCHANTED_BOOK)
                    (anvil.secondItem?.itemMeta as EnchantmentStorageMeta).storedEnchants
            else if (anvil.secondItem?.type != Material.ENCHANTED_BOOK && anvil.secondItem != null)
                anvil.secondItem?.enchantments
            else return

        val firstItemEnchants =
            if (anvil.firstItem?.type == Material.ENCHANTED_BOOK)
                (anvil.firstItem?.itemMeta as EnchantmentStorageMeta).storedEnchants
            else if (anvil.firstItem?.type != Material.ENCHANTED_BOOK && anvil.secondItem != null)
                anvil.firstItem?.enchantments
            else return

        // Up dumb limit of vanilla
        anvil.maximumRepairCost = 50
        anvil.repairCost = calculateItemEnchantCost(enchanted as MutableMap<Enchantment, Int>) + calculateItemEnchantCost(firstItemEnchants as MutableMap<Enchantment, Int>)

        if (anvil.firstItem?.type != anvil.secondItem?.type && anvil.secondItem?.type != Material.ENCHANTED_BOOK) return

        enchanted.forEach {
            val target = getItemTarget(anvil.firstItem)
            val enchant = it.key
            val enchantLevel = it.value
            var newLevel: Int

            firstItemEnchants.forEach firstItem@{ first ->
                if ( first.key.conflictsWith(enchant) || enchant.conflictsWith(first.key)) {
                    if (anvil.result?.type != Material.ENCHANTED_BOOK) anvil.result?.enchantments?.remove(enchant)
                    else (anvil.result?.itemMeta as EnchantmentStorageMeta).removeStoredEnchant(enchant)
                    return@forEach
                }
            }

            if ((!getEnchantmentTarget(enchant).contains(target) && !(enchant as EnchantmentWrapper).allowedItems.contains(target))
                && (slot == 2) && (anvil.firstItem?.type != Material.ENCHANTED_BOOK))
            {
                player.error("This book cannot be added to this item")
                broadcast(enchant)
                anvil.maximumRepairCost = 0
                isCancelled = true
                player.closeInventory()
                return@forEach
            }


            val first = anvil.firstItem
            val itemLevel =
                if (first?.type != Material.ENCHANTED_BOOK && first?.containsEnchantment(enchant) == true) {
                    first.getEnchantmentLevel(enchant)
                }
                else if (first?.type == Material.ENCHANTED_BOOK && (first.itemMeta as EnchantmentStorageMeta).hasStoredEnchant(enchant)) {
                    (first.itemMeta as EnchantmentStorageMeta).storedEnchants[enchant]!!
                }
                else 0

            newLevel = when {
                itemLevel == enchantLevel -> itemLevel + 1
                itemLevel > enchantLevel -> itemLevel
                itemLevel <= enchantLevel -> enchantLevel
                else -> itemLevel
            }

            if (newLevel > enchant.maxLevel) newLevel = enchant.maxLevel

            if (anvil.firstItem?.type != Material.ENCHANTED_BOOK) {
                anvil.result = anvil.firstItem
                anvil.result?.displayName()
                if (CustomEnchants.enchantmentList.contains(enchant)) {
                    anvil.result?.addCustomEnchant(enchant as EnchantmentWrapper, newLevel)
                    anvil.result?.updateEnchantmentLore(enchant as EnchantmentWrapper, itemLevel, "", true)
                    anvil.result?.updateEnchantmentLore(enchant as EnchantmentWrapper, itemLevel, removeLore = true)
                    anvil.result?.updateEnchantmentLore(enchant as EnchantmentWrapper, newLevel)
                } else anvil.result?.addEnchantment(enchant, newLevel)

            } else if (anvil.firstItem?.type == Material.ENCHANTED_BOOK) {
                anvil.result = anvil.firstItem
                val bookMeta = (anvil.result?.itemMeta as EnchantmentStorageMeta)

                bookMeta.addStoredEnchant(enchant, newLevel, false)
                anvil.result?.itemMeta = bookMeta
                if (CustomEnchants.enchantmentList.contains(enchant)) {
                    anvil.result?.updateEnchantmentLore(enchant as EnchantmentWrapper, itemLevel, removeLore = true)
                    anvil.result?.updateEnchantmentLore(enchant as EnchantmentWrapper, newLevel)
                }
            }
        }
    }

    @EventHandler
    fun InventoryClickEvent.removeCustomEnchantGrindstone() {
        if (inventory.type != InventoryType.GRINDSTONE) return
        if (slot != 2) return
        val grindstone = inventory as GrindstoneInventory

        if (grindstone.isEmpty) return

        if (grindstone.contains(Material.ENCHANTED_BOOK)) {
            grindstone.result = ItemStack(Material.BOOK)
            return
        }

        when (grindstone.upperItem != null) {
             true -> {
                 grindstone.result = grindstone.upperItem
                 grindstone.result?.itemMeta = grindstone.upperItem?.itemMeta
             }
            false -> {
                grindstone.result = grindstone.lowerItem
                grindstone.result?.itemMeta = grindstone.lowerItem?.itemMeta
            }
        }

        grindstone.result?.enchantments?.forEach {

            if (CustomEnchants.enchantmentList.contains(it.key))
                grindstone.result?.removeCustomEnchant(it.key as EnchantmentWrapper)

            else if (!CustomEnchants.enchantmentList.contains(it.key))
                grindstone.result?.removeEnchantment(it.key)
        }
    }
}