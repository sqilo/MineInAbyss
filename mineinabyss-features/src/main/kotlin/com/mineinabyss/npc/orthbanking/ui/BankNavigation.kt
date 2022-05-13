package com.mineinabyss.npc.orthbanking.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.mineinabyss.components.playerData
import com.mineinabyss.guilds.extensions.getGuildLevel
import com.mineinabyss.guiy.components.Item
import com.mineinabyss.guiy.components.Spacer
import com.mineinabyss.guiy.components.canvases.Chest
import com.mineinabyss.guiy.inventory.GuiyOwner
import com.mineinabyss.guiy.layout.Row
import com.mineinabyss.guiy.modifiers.Modifier
import com.mineinabyss.guiy.modifiers.at
import com.mineinabyss.guiy.modifiers.height
import com.mineinabyss.guiy.modifiers.size
import com.mineinabyss.helpers.TitleItem
import com.mineinabyss.helpers.ui.Navigator
import com.mineinabyss.helpers.ui.composables.Button
import com.mineinabyss.idofront.font.Space
import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.idofront.messaging.miniMsg
import org.bukkit.ChatColor.WHITE
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

sealed class BankScreen(val title: String, val height: Int) {
    object Default : BankScreen("${Space.of(-18)}$WHITE:orthbanking_menu:", 4)
    object Deposit : BankScreen("${Space.of(-18)}$WHITE:orthbanker_deposit_menu:", 5)
    object Withdraw : BankScreen("${Space.of(18)}$WHITE:orthbanker_withdrawal_menu:", 5)
}

typealias BankNav = Navigator<BankScreen>

class OrthBankScope(
    val player: Player,
) {
    //TODO cache more than just guild level here
    val guildLevel = player.getGuildLevel() ?: 0
    val nav = BankNav { BankScreen.Default }
}

@Composable
fun GuiyOwner.BankMenu(player: Player) {
    val scope = remember { OrthBankScope(player) }
    scope.apply {
        nav.withScreen(setOf(player), onEmpty = ::exit) { bankScreen ->
            Chest(
                setOf(player),
                "${Space.of(-12)}:orthbanking:",
                Modifier.height(4),
                onClose = { player.closeInventory() }) {
                when (bankScreen) {
                    BankScreen.Default -> HomeScreen()
                    BankScreen.Deposit -> DepositScreen(player)
                    BankScreen.Withdraw -> WithdrawScreen(player)
                }
            }
        }
    }
}

@Composable
fun OrthBankScope.HomeScreen() {
    val data = player.playerData
    if (data.orthCoinsHeld > 0 && data.mittyTokensHeld > 0)
        CoinLabel(player, Modifier.at(0,0))
    Row(Modifier.at(2,1)) {
        DepositButton(player)
        Spacer(width = 1)
        WithdrawButton(player)
    }

}

@Composable
fun OrthBankScope.CoinLabel(player: Player, modifier: Modifier) {
    Button(
        modifier = modifier,
        onClick = { /* Toggle between orth coins and mitty tokens */ },
    ) {
        Item(ItemStack(Material.EMERALD).editItemMeta {
            displayName("".miniMsg())
        })
    }
}

@Composable
fun OrthBankScope.DepositButton(player: Player) {
    Button(
        onClick = { nav.open(BankScreen.Deposit) },
    ) {
        Item(
            TitleItem.of(
                "<gold><b>Open Deposit Menu".miniMsg(),
                "<yellow>You currently have <i>${player.playerData.orthCoinsHeld}".miniMsg(),
                "<yellow>coins in your account.".miniMsg()
            ), modifier = Modifier.size(2, 2)
        )
    }
}

@Composable
fun OrthBankScope.WithdrawButton(player: Player) {
    Button(
        onClick = { nav.open(BankScreen.Withdraw) },
        enabled = player.playerData.orthCoinsHeld > 0
    ) {
        Item(
            TitleItem.of(
                "<gold><b>Open Withdraw Menu".miniMsg(),
                "<yellow>You currently have <i>${player.playerData.orthCoinsHeld}".miniMsg(),
                "<yellow>coins in your account.".miniMsg()
            ), modifier = Modifier.size(2, 2)
        )
    }
}
