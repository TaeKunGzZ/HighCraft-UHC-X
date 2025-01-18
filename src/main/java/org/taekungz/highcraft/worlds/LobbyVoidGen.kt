package org.taekungz.highcraft.worlds

import org.bukkit.Material
import org.bukkit.World
import org.bukkit.generator.ChunkGenerator

class LobbyVoidGen : ChunkGenerator() {
    //customize world generation
    override fun generateChunkData(world: World, random: java.util.Random, x: Int, z: Int, biome: BiomeGrid): ChunkData {
        val chunkData: ChunkData = createChunkData(world)

        // Custom world generation logic here
        for (y in 0 until 128) {
            chunkData.setBlock(x, y, z, Material.AIR)
        }

        return chunkData
    }
}