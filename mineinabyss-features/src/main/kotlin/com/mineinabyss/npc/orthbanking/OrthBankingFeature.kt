package com.mineinabyss.npc.orthbanking

import com.mineinabyss.components.playerData
import com.mineinabyss.helpers.updateBalance
import com.mineinabyss.hubstorage.isInHub
import com.mineinabyss.idofront.commands.arguments.intArg
import com.mineinabyss.idofront.commands.extensions.actions.playerAction
import com.mineinabyss.idofront.plugin.registerEvents
import com.mineinabyss.mineinabyss.core.AbyssFeature
import com.mineinabyss.mineinabyss.core.MineInAbyssPlugin
import com.mineinabyss.mineinabyss.core.commands
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.entity.Player

@Serializable
@SerialName("orth_banking")
class OrthBankingFeature : AbyssFeature {
    override fun MineInAbyssPlugin.enableFeature() {
        registerEvents(OrthBankingListener())

        commands {
            mineinabyss {
                "bank"(desc = "Orthbanking related commands"){
                    "balance"(desc = "Toggles whether or not the balance should be shown.") {
                        playerAction {
                            val player = sender as Player
                            val data = player.playerData
                            data.showPlayerBalance = !data.showPlayerBalance
                            if (data.showPlayerBalance) player.updateBalance()
                        }
                    }
                    "deposit"(desc = "Dev command until Guiy can take items") {
                        val amount by intArg { default = 1 }
                        playerAction {
                            val player = sender as Player
                            val data = player.playerData
                            val currItem = player.inventory.itemInMainHand
                            //val gearyEntity = currItem.toGearyOrNull(player)
                            //val orthCoin = gearyEntity?.get<OrthCoin>() ?: return@playerAction

                            if (!player.isInHub()) return@playerAction

                            data.orthCoinsHeld += amount
                            data.mittyTokensHeld += amount
                            //currItem.subtract(currItem.amount)
                            //currItem.subtract(currItem.amount).broadcastVal("amount: ")
                            if (data.showPlayerBalance) player.updateBalance()

                        }
                    }
                    "withdraw"(desc = "Dev command until Guiy can take items") {
                        val amount by intArg { default = 1 }
                        playerAction {
                            val player = sender as Player
                            val data = player.playerData
                            val slot = player.inventory.firstEmpty()

                            if (!player.isInHub()) return@playerAction

//                        if (slot == -1) {
//                            player.error("No empty slots in inventory")
//                            return@playerAction
//                        }

                            if (data.orthCoinsHeld > 0) data.orthCoinsHeld -= amount
                            if (data.mittyTokensHeld > 0) data.mittyTokensHeld -= amount
                            if (data.showPlayerBalance) player.updateBalance()
                        }
                    }
                }
            }
            tabCompletion {
                when (args.size) {
                    1 -> listOf(
                        "bank"
                    ).filter { it.startsWith(args[0]) }
                    2 -> {
                        when (args[0]) {
                            "bank" -> listOf("withdraw", "deposit", "balance")
                            else -> null
                        }
                    }
                    else -> null
                }
            }
        }
    }
}
