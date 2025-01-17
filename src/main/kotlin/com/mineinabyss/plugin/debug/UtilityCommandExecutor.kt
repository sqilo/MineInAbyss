package com.mineinabyss.plugin.debug

import com.github.shynixn.mccoroutine.bukkit.launch
import com.mineinabyss.components.layer.Layer
import com.mineinabyss.components.layer.LayerKey
import com.mineinabyss.idofront.commands.arguments.stringArg
import com.mineinabyss.idofront.commands.execution.IdofrontCommandExecutor
import com.mineinabyss.idofront.commands.execution.stopCommand
import com.mineinabyss.idofront.commands.extensions.actions.playerAction
import com.mineinabyss.idofront.messaging.info
import com.mineinabyss.mineinabyss.core.AbyssWorldManager
import com.mineinabyss.mineinabyss.core.mineInAbyss
import kotlinx.coroutines.yield
import org.bukkit.Material
import org.bukkit.block.Container
import org.bukkit.command.CommandSender

class UtilityCommandExecutor : IdofrontCommandExecutor() {
    override val commands = commands(mineInAbyss) {
        "clearcontainers" {
            val itemToClear by stringArg()
            val layerToClear by stringArg { default = "all" }

            playerAction {
                val parsedItem = Material.getMaterial(itemToClear) ?: command.stopCommand("Item not found")

                if (layerToClear == "all") {
                    sender.info("Start clearing out $parsedItem from containers in all layers.")
                    AbyssWorldManager.layers.values.forEach {
                        mineInAbyss.launch {
                            clearItemFromContainers(it, parsedItem, sender)
                        }
                    }
                } else {
                    val parsedLayer = AbyssWorldManager.getLayerFor(LayerKey(layerToClear))
                        ?: command.stopCommand("Layer not found")
                    sender.info("Start clearing out $parsedItem from containers in ${parsedLayer.name}.")
                    mineInAbyss.launch {
                        clearItemFromContainers(parsedLayer, parsedItem, sender)
                    }
                }
            }
        }
    }

    // This is a suspending fun so that we don't freeze the server
    private suspend fun clearItemFromContainers(
        layer: Layer,
        item: Material,
        sender: CommandSender
    ) {
        val worldsToBeChecked = layer.sections.groupBy { it.world }
        worldsToBeChecked.forEach { (world, sections) ->
            sections.forEach { section ->
                for (x in (section.region.a.x / 16)..(section.region.b.x / 16)) {
                    for (z in (section.region.a.z / 16)..(section.region.b.z / 16)) {
                        val chunk = world.getChunkAt(x, z)
                        chunk.load()
                        val containers = chunk.tileEntities
                            .filterIsInstance<Container>()
                            .filter {
                                section.region.contains(
                                    it.location.x.toInt(),
                                    it.location.y.toInt(),
                                    it.location.z.toInt()
                                )
                            }

                        containers.forEach { container ->
                            if (container.inventory.contains(item)) {
                                val numberOfItems = container.inventory.contents
                                    ?.filter { it != null && it.type == item }
                                container.inventory.remove(item)
                                sender.info("Removed $numberOfItems ${item.name} from a container at x:${container.x} y:${container.y} z:${container.z} in layer ${layer.name} (world ${world.name})")
                            }
                        }
                        chunk.unload()
                        yield()
                    }
                }
            }
        }
        sender.info("Finished layer ${layer.name}")
    }
}
