package com.mineinabyss.mineinabyss.core

import com.mineinabyss.geary.addon.GearyAddon
import com.mineinabyss.idofront.commands.Command
import com.mineinabyss.idofront.commands.execution.IdofrontCommandExecutor
import com.mineinabyss.idofront.plugin.getServiceOrNull
import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit
import org.jetbrains.exposed.sql.Database
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.qualifier

/** A reference to the MineInAbyss plugin */
val mineInAbyss: MineInAbyssPlugin by lazy { Bukkit.getPluginManager().getPlugin("MineInAbyss") as MineInAbyssPlugin }

class AbyssContext : KoinComponent {
    val econ: Economy? = getServiceOrNull<Economy>("Vault")
    val startup: AbyssStartup by inject()
    val db: Database by inject(qualifier<MineInAbyssPlugin>())
    val config: MIAConfig by inject()
}

interface AbyssStartup {
    val addonScope: GearyAddon
    val miaSubcommands: MutableList<Command.() -> Unit>
    val tabCompletions: MutableList<MineInAbyssPlugin.TabCompletion.() -> List<String>?>
    val commandExecutor: IdofrontCommandExecutor
}
