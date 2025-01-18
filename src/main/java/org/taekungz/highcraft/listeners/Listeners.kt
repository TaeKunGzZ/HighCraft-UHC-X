@file:Suppress("DEPRECATION", "KotlinConstantConditions", "SameParameterValue")

package org.taekungz.highcraft.listeners

import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.ServerListPingEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import org.taekungz.highcraft.HighCraft

class Listeners : Listener {

    //when player died
    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity

        player.gameMode = GameMode.SPECTATOR
        player.world.playSound(player.location, Sound.ENTITY_WITHER_SKELETON_DEATH, 0.55f, 0.69f)
        player.world.spawnParticle(Particle.SOUL_FIRE_FLAME, player.location, 100)
        player.world.spawnParticle(Particle.SOUL, player.location, 50)

        event.drops.clear()

        val head = ItemStack(Material.PLAYER_HEAD)
        val meta = head.itemMeta as SkullMeta
        meta.owningPlayer = player
        head.itemMeta = meta

        // Drop the head item at the death location
        event.entity.world.dropItem(event.entity.location, head)

        val killer = player.killer

        if (killer is Player) {
            val killerName = killer.name
            val currentCount = HighCraft.killCounts.getOrDefault(killerName, 0)
            HighCraft.killCounts[killerName] = currentCount + 1
        }
    }


    //customize motd server
    @EventHandler
    fun onServerListPing(event: ServerListPingEvent) {
        val motdline1 = ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "                High" + ChatColor.WHITE + ChatColor.BOLD + "Craft" + ChatColor.BLUE + " Season " + ChatColor.WHITE + "X"
        val motdline2 = ChatColor.AQUA.toString() + "         " + "  Status" + ChatColor.WHITE + ": " + ChatColor.GREEN + ChatColor.BOLD + "Online" + ChatColor.GOLD + " | " +
                ChatColor.AQUA + "Host" + ChatColor.WHITE + " By " + ChatColor.RED + ChatColor.BOLD + "IMZ" + ChatColor.WHITE + ChatColor.BOLD + "Team"
        val motd = "$motdline1\n$motdline2"

        event.motd = motd
    }


    //prevent player from place block y >= 100 or <= 60
    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player
        val block = event.block
        val xCoordinate = block.location.blockX
        val yCoordinate = block.location.blockY
        val zCoordinate = block.location.blockZ

        // Change the limit to your desired maximum y-coordinate
        val maxY = 100
        val minY = 50

        if (xCoordinate in 100 downTo -100 && zCoordinate in 100 downTo -100) {
            if (!player.isOp) {
                if (yCoordinate >= maxY) {
                    // Cancel the event if the y-coordinate is above the limit
                    event.isCancelled = true
                    event.player.sendActionBar(ChatColor.RED.toString() + "placing/breaking block above y=100 is not Allowed in HighCraft.")
                    event.player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 1.69f)
                }
                if (yCoordinate < minY) {
                    // Cancel the event if the y-coordinate is above the limit
                    event.isCancelled = true
                    event.player.sendActionBar(ChatColor.RED.toString() + "placing/breaking block below y=50 is not Allowed in HighCraft.")
                    event.player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 1.69f)
                }
            }
        }
    }

    //prevent player from break block y >= 100 or <= 60
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block
        val xCoordinate = block.location.blockX
        val yCoordinate = block.location.blockY
        val zCoordinate = block.location.blockZ

        // Change the limit to your desired maximum y-coordinate
        val maxY = 100
        val minY = 50

        if (xCoordinate in 100 downTo -100 && zCoordinate in 100 downTo -100) {
            if (!player.isOp) {
                if (yCoordinate >= maxY) {
                    // Cancel the event if the y-coordinate is above the limit
                    event.isCancelled = true
                    event.player.sendActionBar(ChatColor.RED.toString() + "placing/breaking block above y=100 is not Allowed in HighCraft.")
                    event.player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 1.69f)
                }
                if (yCoordinate < minY) {
                    // Cancel the event if the y-coordinate is above the limit
                    event.isCancelled = true
                    event.player.sendActionBar(ChatColor.RED.toString() + "placing/breaking block below y=50 is not Allowed in HighCraft.")
                    event.player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 1.69f)
                }
            }
        }

        if (block.type == Material.EMERALD_ORE) {
            if (player.gameMode == GameMode.SURVIVAL) {
                event.isDropItems = false
                event.expToDrop = 0

                player.giveExp(15)
                player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.2f)
            }
        }
    }


    //prevent player from pvp
    @EventHandler
    fun onEntityDamagebyEntity(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        val damaged = event.entity

        if (damager is Player && damaged is Player) {
            event.isCancelled = !HighCraft.isPVPon
        }
    }


    //when player join
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val playerName = player.name

        event.joinMessage = ChatColor.GREEN.toString() + "+ " + ChatColor.WHITE + playerName
    }

    //when player quit
    @EventHandler
    fun onPlayerDisconnect(event: PlayerQuitEvent) {
        val player = event.player
        val playerName = player.name

        event.quitMessage = ChatColor.RED.toString() + "- " + ChatColor.WHITE + playerName
    }


    //portal break
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        val to = event.to

        // Check if the player enters a portal block
        if (HighCraft.isPortalClose) {
            if (player.gameMode == GameMode.SURVIVAL) {
                if (to.block.type == Material.NETHER_PORTAL) {
                    // Break the obsidian blocks surrounding the portal
                    val obsidianBlocks = findObsidianBlocks(to.block.location, 2.5) // Set radius to 2.5
                    obsidianBlocks.forEach { block ->
                        block.type = Material.AIR
                    }
                }
            }
        }
    }
    private fun findObsidianBlocks(location: Location, radius: Double): List<org.bukkit.block.Block> {
        val obsidianBlocks = mutableListOf<org.bukkit.block.Block>()

        val minX = (location.x - radius).toInt()
        val maxX = (location.x + radius).toInt()
        val minY = (location.y - 1).toInt()
        val maxY = (location.y + 1).toInt()
        val minZ = (location.z - radius).toInt()
        val maxZ = (location.z + radius).toInt()

        for (x in minX..maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    val block = location.world.getBlockAt(x, y, z)
                    if (block.type == Material.OBSIDIAN) {
                        obsidianBlocks.add(block)
                    }
                }
            }
        }

        return obsidianBlocks
    }
}