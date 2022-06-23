package com.mineinabyss.mineinabyss.core

import com.mineinabyss.components.layer.Layer
import com.mineinabyss.deeperworld.services.WorldManager
import com.mineinabyss.idofront.features.FeatureManager
import kotlinx.serialization.Serializable

@Serializable
class AbyssFeatureManager: FeatureManager<AbyssFeature>(AbyssFeature::class)

/**
 * @param layers A list of all the layers and sections composing them to be registered.
 * @property hubSection The hub section of the abyss, a safe place for living and trading.
 * @property guilds Guild related options.
 */
@Serializable
class MIAConfig(
    val layers: List<Layer>, //TODO way of changing the serializer from service
    private val features: AbyssFeatureManager,
    private val hubSectionName: String = "orth",
) {
    val hubSection by lazy {
        WorldManager.getSectionFor(hubSectionName) ?: error("Section $hubSectionName was not found for the hub.")
    }
}
