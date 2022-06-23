package com.mineinabyss.mineinabyss.core.helpers

import com.mineinabyss.components.layer.Layer
import com.mineinabyss.deeperworld.world.section.Section
import com.mineinabyss.deeperworld.world.section.section
import com.mineinabyss.mineinabyss.core.AbyssWorldManager
import org.bukkit.Location
import org.bukkit.World


val Location.layer: Layer?
    get() {
        return AbyssWorldManager.getLayerForSection(this.section ?: return null)
    }

val Section.layer: Layer?
    get() = AbyssWorldManager.getLayerForSection(this)

val World.isAbyssWorld: Boolean get() = AbyssWorldManager.isAbyssWorld(this)
