package com.mineinabyss.plugin

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.mineinabyss.components.curse.AscensionEffect
import com.mineinabyss.geary.addon.GearyAddon
import com.mineinabyss.geary.addon.autoscan
import com.mineinabyss.geary.papermc.dsl.gearyAddon
import com.mineinabyss.geary.papermc.store.PrefabNamespaceMigrations
import com.mineinabyss.guilds.database.GuildJoinQueue
import com.mineinabyss.guilds.database.Guilds
import com.mineinabyss.guilds.database.Players
import com.mineinabyss.helpers.MessageQueue
import com.mineinabyss.idofront.autoscan.autoscanPolymorphic
import com.mineinabyss.idofront.commands.Command
import com.mineinabyss.idofront.config.singleConfig
import com.mineinabyss.idofront.config.startOrAppendKoin
import com.mineinabyss.idofront.features.FeaturesCommandExecutor
import com.mineinabyss.idofront.platforms.IdofrontPlatforms
import com.mineinabyss.idofront.plugin.isPluginEnabled
import com.mineinabyss.mineinabyss.core.*
import kotlinx.serialization.modules.SerializersModule
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

class MineInAbyssPluginImpl : MineInAbyssPlugin(), KoinComponent {
    override fun onLoad() {
        IdofrontPlatforms.load(this, "mineinabyss")
    }

    override fun onEnable() {
        saveDefaultConfig()

        var addon: GearyAddon? = null
        if (isPluginEnabled("Geary")) {
            PrefabNamespaceMigrations.migrations += listOf("looty" to "mineinabyss", "mobzy" to "mineinabyss")
            gearyAddon {
                addon = this
                autoscan("com.mineinabyss") {
                    components()
                }
            }
        }

        FeaturesCommandExecutor(this)

        startOrAppendKoin(module {
            single(qualifier<MineInAbyssPlugin>()) {
                Database.connect("jdbc:sqlite:" + dataFolder.path + "/data.db", "org.sqlite.JDBC")
            }
            single<AbyssStartup> {
                object : AbyssStartup {
                    override val addonScope: GearyAddon
                        get() = addon ?: error("Feature tried accessing Geary but it wasn't loaded")
                    override val miaSubcommands = mutableListOf<Command.() -> Unit>()
                    override val tabCompletions = mutableListOf<TabCompletion.() -> List<String>?>()
                }
            }
            single<AbyssWorldManager> { AbyssWorldManagerImpl() }
            singleConfig<MIAConfig>(
                this@MineInAbyssPluginImpl,
                format = Yaml(
                    serializersModule = SerializersModule {
                        autoscanPolymorphic<AbyssFeature>("com.mineinabyss")
                        autoscanPolymorphic<AscensionEffect>("com.mineinabyss")
                    },
                    configuration = YamlConfiguration(extensionDefinitionPrefix = "x-")
                ),
                load = { conf ->
                    conf.features
                    "Enabling features" {
                    }
                },
                unload = { conf ->
                    "Disabling features" {
                        conf.features.forEach {
                            it.apply {
                                "Disabled ${it::class.simpleName}" {
                                    mineInAbyss.disableFeature()
                                }
                            }
                        }
                    }
                })
        })

        with(AbyssContext()) {
            transaction(db) {
                addLogger(StdOutSqlLogger)
                SchemaUtils.createMissingTablesAndColumns(Guilds, Players, GuildJoinQueue, MessageQueue)
            }
        }
    }
}
