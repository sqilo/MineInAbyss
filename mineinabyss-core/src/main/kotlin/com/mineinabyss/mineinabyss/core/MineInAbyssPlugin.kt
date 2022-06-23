package com.mineinabyss.mineinabyss.core

import com.mineinabyss.geary.addon.GearyAddon
import com.mineinabyss.idofront.commands.Command
import com.mineinabyss.idofront.commands.CommandHolder
import org.bukkit.plugin.java.JavaPlugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class MineInAbyssPlugin : JavaPlugin(), KoinComponent {
    val context: AbyssContext by inject()

    fun CommandHolder.mineinabyss(run: Command.() -> Unit) {
        context.startup.miaSubcommands += run
    }

    fun CommandHolder.tabCompletion(completion: TabCompletion.() -> List<String>?) {
        context.startup.tabCompletions += completion
    }
}

inline fun MineInAbyssPlugin.geary(run: GearyAddon.() -> Unit) {
    AbyssContext.addonScope.apply(run)
}

inline fun MineInAbyssPlugin.commands(run: CommandHolder.() -> Unit) {
    AbyssContext.commandExecutor.commands.apply(run)
}
