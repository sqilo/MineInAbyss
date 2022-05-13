package com.mineinabyss.npc.orthbanking.ui

import androidx.compose.runtime.Composable
import com.mineinabyss.guiy.components.ItemGrid
import com.mineinabyss.guiy.components.canvases.Chest
import com.mineinabyss.guiy.components.rememberItemGridState
import com.mineinabyss.guiy.inventory.GuiyOwner
import com.mineinabyss.guiy.modifiers.Modifier
import com.mineinabyss.guiy.modifiers.at
import com.mineinabyss.guiy.modifiers.clickable
import com.mineinabyss.guiy.modifiers.size
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType

@Composable
fun GuiyOwner.DepositScreen(player: Player) {
    val title = "Hello world"
    val state = rememberItemGridState()
    Chest(
        setOf(player),
        title,
        onClose = { exit() },
        modifier = Modifier.clickable {
            if(clickType == ClickType.SHIFT_LEFT) {
                cursor = cursor?.let { state.add(it, 6, 1) }
            }
        }
    ) {
        ItemGrid(state, Modifier.size(6, 1).at(1,1))
    }
}
