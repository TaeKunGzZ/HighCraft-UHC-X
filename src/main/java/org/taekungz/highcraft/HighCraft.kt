@file:Suppress("DEPRECATION", "KotlinConstantConditions", "SameParameterValue")

package org.taekungz.highcraft

import org.bukkit.*
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.taekungz.highcraft.HighCraft.Companion.worldBorder
import org.taekungz.highcraft.commands.LobbyCommand
import org.taekungz.highcraft.commands.SBtoggleCommand
import org.taekungz.highcraft.commands.StartCommand
import org.taekungz.highcraft.commands.TimerCommand
import org.taekungz.highcraft.core.AirdropCore
import org.taekungz.highcraft.core.TimerCore
import org.taekungz.highcraft.listeners.Listeners
import org.taekungz.highcraft.worlds.LobbyVoidGen

class HighCraft : JavaPlugin(), Listener {
    companion object {
        var isSBnotone: Boolean = false
        var isTimerActive: Boolean = false
        var isPortalClose: Boolean = false
        var hasExecutedCommand: Boolean = false
        var isPVPon: Boolean = false

        val killCounts = mutableMapOf<String, Int>()
        lateinit var worldBorder: WorldBorder


        fun callDatapackFunction(functionName: String) {
            if (!hasExecutedCommand) {
                val console: CommandSender = Bukkit.getConsoleSender()
                Bukkit.dispatchCommand(console, "function $functionName")
                hasExecutedCommand = true
            }
        }
    }

    private val timerGame: TimerGame = TimerGame()
    private val timerTimer: TimerCore = TimerCore()
    private val timerAirdrop: AirdropCore = AirdropCore()

    override fun onEnable() {
        server.pluginManager.registerEvents(Listeners(), this)
        server.pluginManager.registerEvents(this, this)
        logger.info("highCraft Plugin has been Enabled!")

        registeredCommands()
        registeredWorld()
        setupWorldBorder()

        timerGame.runTaskTimer(this, 0L, 20L)
        timerTimer.runTaskTimer(this, 0L, 20L)
        timerAirdrop.runTaskTimer(this, 0L, 20L)
        object : BukkitRunnable() {
            override fun run() {
                updateSec()
            }
        }.runTaskTimer(this, 0L, 20L)
    }


    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.name.equals("killstreak", ignoreCase = true)) {
            if (sender is Player) {
                val playerName = sender.name
                val killCount = killCounts.getOrDefault(playerName, 0)
                sender.sendMessage("${ChatColor.GREEN}Your Kill Count: $killCount")
            } else {
                sender.sendMessage("This command can only be used by players.")
            }
            return true
        } else if (command.name.equals("allkillcounts", ignoreCase = true)) {
            if (sender is Player) {
                sender.sendMessage("Kill Counts:")
                for ((playerName, killCount) in killCounts) {
                    sender.sendMessage("${ChatColor.GREEN}$playerName: $killCount")
                }
            } else {
                sender.sendMessage("This command can only be used by players.")
            }
            return true
        }
        return false
    }

    private fun getKillCount(player: Player): Int {
        return killCounts.getOrDefault(player.name, 0)
    }


    //registeredEvents..
    private fun registeredCommands() {
        getCommand("timer")?.setExecutor(TimerCommand())
        getCommand("sb")?.setExecutor(SBtoggleCommand())
        getCommand("start")?.setExecutor(StartCommand())
        getCommand("lobby")?.setExecutor(LobbyCommand())
    }
    private fun registeredWorld() {
        val worldName = "lobby"
        val world = server.createWorld(WorldCreator(worldName).generator(LobbyVoidGen())).toString()

        logger.info("Custom World '$worldName' registered. $world")
    }


    //updateTick
    private fun updateSec() {
        val timerCore = TimerCore()

        for (player in Bukkit.getOnlinePlayers()) {
            player.playerListHeader = "                   " + ChatColor.DARK_PURPLE + ChatColor.BOLD + "High" + ChatColor.WHITE + ChatColor.BOLD + "Craft" +
                    ChatColor.YELLOW + " Season " + ChatColor.GOLD + ChatColor.BOLD + "X" + "               "
        player.playerListFooter = "                    " + ChatColor.GOLD + "Presented " + ChatColor.WHITE + "by " + ChatColor.RED + ChatColor.BOLD + "IMZ " + ChatColor.WHITE + ChatColor.BOLD + "Team" + "                    "


            if (isSBnotone) {
                if (TimerCore.seconds in 0..9 && TimerCore.minutes in 0..9 && TimerCore.hours in 0..9) {
            player.sendActionBar(ChatColor.RED.toString() + ChatColor.BOLD + " Game Time: " + ChatColor.WHITE +
                    "0${timerCore.getHours()}:0${timerCore.getMinutes()}:0${timerCore.getSeconds()} |" +
                    ChatColor.GREEN + ChatColor.BOLD + " Kills: " + ChatColor.WHITE + "${getKillCount(player)} |" +
                    ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + " World Border: " + ChatColor.WHITE + "=${getWorldBorderSize()}")
        }
                else if (TimerCore.seconds in 0..9 && TimerCore.minutes in 0..9) {
            player.sendActionBar(ChatColor.RED.toString() + ChatColor.BOLD + " Game Time: " + ChatColor.WHITE +
                    "${timerCore.getHours()}:0${timerCore.getMinutes()}:0${timerCore.getSeconds()} |" +
                    ChatColor.GREEN + ChatColor.BOLD + " Kills: " + ChatColor.WHITE + "${getKillCount(player)} |" +
                    ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + " World Border: " + ChatColor.WHITE + "±${getWorldBorderSize()}")
        }
                else if (TimerCore.minutes in 0..9 && TimerCore.hours in 0..9) {
            player.sendActionBar(ChatColor.RED.toString() + ChatColor.BOLD + " Game Time: " + ChatColor.WHITE +
                    "0${timerCore.getHours()}:0${timerCore.getMinutes()}:${timerCore.getSeconds()} |" +
                    ChatColor.GREEN + ChatColor.BOLD + " Kills: " + ChatColor.WHITE + "${getKillCount(player)} |" +
                    ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + " World Border: " + ChatColor.WHITE + "±${getWorldBorderSize()}")
        }
                else if (TimerCore.hours in 0..9 && TimerCore.seconds in 0..9) {
            player.sendActionBar(ChatColor.RED.toString() + ChatColor.BOLD + " Game Time: " + ChatColor.WHITE +
                    "0${timerCore.getHours()}:${timerCore.getMinutes()}:0${timerCore.getSeconds()} |" +
                    ChatColor.GREEN + ChatColor.BOLD + " Kills: " + ChatColor.WHITE + "${getKillCount(player)} |" +
                    ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + " World Border: " + ChatColor.WHITE + "±${getWorldBorderSize()}")
        }
                else if (TimerCore.hours in 0..9) {
            player.sendActionBar(ChatColor.RED.toString() + ChatColor.BOLD + " Game Time: " + ChatColor.WHITE +
                    "0${timerCore.getHours()}:${timerCore.getMinutes()}:${timerCore.getSeconds()} |" +
                    ChatColor.GREEN + ChatColor.BOLD + " Kills: " + ChatColor.WHITE + "${getKillCount(player)} |" +
                    ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + " World Border: " + ChatColor.WHITE + "±${getWorldBorderSize()}")
        }
                else {
            player.sendActionBar(ChatColor.RED.toString() + ChatColor.BOLD + " Game Time: " + ChatColor.WHITE +
                    "${timerCore.getHours()}:${timerCore.getMinutes()}:${timerCore.getSeconds()} |" +
                    ChatColor.GREEN + ChatColor.BOLD + " Kills: " + ChatColor.WHITE + "${getKillCount(player)} |" +
                    ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + " World Border: " + ChatColor.WHITE + "±${getWorldBorderSize()}")
        }
            }
            else {
                player.sendActionBar("")
            }
        }
    }

    //worldborder setup/detect
    private fun setupWorldBorder() {
        // You may need to adjust these values based on your requirements
        worldBorder = Bukkit.getWorlds()[0].worldBorder
    }
    private fun getWorldBorderSize(): Int {
        return worldBorder.size.toInt()
    }
}

class TimerGame : BukkitRunnable() {
    private val hc = ChatColor.RESET.toString() + "[" + ChatColor.DARK_PURPLE + ChatColor.BOLD + "High" + ChatColor.WHITE + ChatColor.BOLD + "Craft" + ChatColor.RESET + "]"

    override fun run() {
        if (HighCraft.hasExecutedCommand) HighCraft.hasExecutedCommand = false

        for (player in Bukkit.getOnlinePlayers()) {

            //final heal
            if (TimerCore.seconds == 0 && TimerCore.minutes == 10 && TimerCore.hours == 0) {
                val regenPotion = PotionEffect(PotionEffectType.REGENERATION, 20, 255)
                player.addPotionEffect(regenPotion)
                player.spawnParticle(Particle.HEART, player.location, 10, 1.0, 1.0, 1.0)

                player.sendTitle(ChatColor.GREEN.toString() + "Mark" + ChatColor.GOLD + " 10 " + ChatColor.GREEN + "Minutes"
                        , ChatColor.LIGHT_PURPLE.toString() + "Final Heal has been Appeared!", 10, 30, 10)
            }

            //notify pvp
            else if (TimerCore.seconds == 55 && TimerCore.minutes == 19 && TimerCore.hours == 0) {
                player.sendMessage("$hc Pvp will be Enabled in " + ChatColor.GREEN + "5")
                player.playSound(player.location, Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1.0f, 0.25f)
            }
            else if (TimerCore.seconds == 56 && TimerCore.minutes == 19 && TimerCore.hours == 0) {
                player.sendMessage("$hc Pvp will be Enabled in " + ChatColor.GREEN + "4")
                player.playSound(player.location, Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1.0f, 0.25f)
            }
            else if (TimerCore.seconds == 57 && TimerCore.minutes == 19 && TimerCore.hours == 0) {
                player.sendMessage("$hc Pvp will be Enabled in " + ChatColor.YELLOW + "3")
                player.playSound(player.location, Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1.0f, 0.25f)
            }
            else if (TimerCore.seconds == 58 && TimerCore.minutes == 19 && TimerCore.hours == 0) {
                player.sendMessage("$hc Pvp will be Enabled in " + ChatColor.RED + "2")
                player.playSound(player.location, Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1.0f, 0.25f)
            }
            else if (TimerCore.seconds == 59 && TimerCore.minutes == 19 && TimerCore.hours == 0) {
                player.sendMessage("$hc Pvp will be Enabled in " + ChatColor.DARK_RED + "1")
                player.playSound(player.location, Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1.0f, 0.25f)
            }

            //pvp on
            else if (TimerCore.seconds == 0 && TimerCore.minutes == 20 && TimerCore.hours == 0) {
                player.sendMessage("$hc Pvp has been Enabled! You all can now fight.")
                player.playSound(player.location, Sound.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, 1.0f, 0.25f)

                HighCraft.isPVPon = true
            }


            //border shrink #1 (1000 1800)
            else if (TimerCore.seconds == 0 && TimerCore.minutes == 30 && TimerCore.hours == 0) {
                worldBorder = Bukkit.getWorlds()[0].worldBorder

                worldBorder.setSize(1000.00, 1800)
                player.sendTitle(ChatColor.GREEN.toString() + "Mark" + ChatColor.GOLD + " 30 " + ChatColor.GREEN + "Minutes"
                        , ChatColor.LIGHT_PURPLE.toString() + "Border Shrinking..", 10, 30, 10)
                player.sendMessage("$hc " + ChatColor.RED + "Worldborder has began to shrink from 4000 to 1000 in 30 minutes.")
                player.playSound(player.location, Sound.BLOCK_PISTON_CONTRACT, 1.0f, 0.25f)
            }

            //portal nether broken after 55 mins
            else if (TimerCore.seconds == 0 && TimerCore.minutes == 55 && TimerCore.hours == 0) {
                val netherWorld = Bukkit.getWorld("world_nether")
                if (netherWorld != null) {
                    val worldBorder = netherWorld.worldBorder
                    worldBorder.setSize(1.00, 600)
                }

                player.sendTitle(ChatColor.GREEN.toString() + "Mark" + ChatColor.GOLD + " 55 " + ChatColor.GREEN + "Minutes"
                        , ChatColor.LIGHT_PURPLE.toString() + "Nether Closed..", 10, 30, 10)
                player.playSound(player.location, Sound.BLOCK_PISTON_EXTEND, 1.0f, 0.25f)

                HighCraft.isPortalClose = true
            }

            //border shrink #2 (100 1200)
            else if (TimerCore.seconds == 0 && TimerCore.minutes == 5 && TimerCore.hours == 1) {
                worldBorder = Bukkit.getWorlds()[0].worldBorder

                worldBorder.setSize(100.00, 1200)
                player.sendTitle(ChatColor.GREEN.toString() + "Mark" + ChatColor.GOLD + " 65 " + ChatColor.GREEN + "Minutes"
                        , ChatColor.LIGHT_PURPLE.toString() + "Border Shrinking..", 10, 30, 10)
                player.sendMessage("$hc " + ChatColor.RED + "Worldborder has began to shrink from 1000 to 100 in 20 minutes.")
                player.playSound(player.location, Sound.BLOCK_PISTON_CONTRACT, 1.0f, 0.25f)
            }



            //start count down
            else if (TimerCore.seconds == -30 && TimerCore.minutes == 0 && TimerCore.hours == 0) {
                val targetWorld: World? = Bukkit.getWorld("world")

                if (targetWorld != null) {
                    val targetLoc = Location(targetWorld, 0.0, 100.0, 0.0)
                    if (player.gameMode == GameMode.ADVENTURE) {
                        player.teleport(targetLoc)
                    }

                    setGameRule(GameRule.FALL_DAMAGE, false)
                }
            }

            else if (TimerCore.seconds == -6 && TimerCore.minutes == 0 && TimerCore.hours == 0) {
                player.sendMessage("$hc Game will start in " + ChatColor.GREEN + "5")
                player.playSound(player.location, Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1.0f, 1.25f)
            }
            else if (TimerCore.seconds == -5 && TimerCore.minutes == 0 && TimerCore.hours == 0) {
                player.sendMessage("$hc Game will start in " + ChatColor.GREEN + "4")
                player.playSound(player.location, Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1.0f, 1.25f)
            }
            else if (TimerCore.seconds == -4 && TimerCore.minutes == 0 && TimerCore.hours == 0) {
                player.sendMessage("$hc Game will start in " + ChatColor.YELLOW + "3")
                player.playSound(player.location, Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1.0f, 1.25f)
            }
            else if (TimerCore.seconds == -3 && TimerCore.minutes == 0 && TimerCore.hours == 0) {
                player.sendMessage("$hc Game will start in " + ChatColor.RED + "2")
                player.playSound(player.location, Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1.0f, 1.25f)
            }
            else if (TimerCore.seconds == -2 && TimerCore.minutes == 0 && TimerCore.hours == 0) {
                player.sendMessage("$hc Game will start in " + ChatColor.DARK_RED + "1")
                player.playSound(player.location, Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1.0f, 1.25f)
            }
            else if (TimerCore.seconds == -1 && TimerCore.minutes == 0 && TimerCore.hours == 0) {
                player.sendMessage("$hc Game started!")
                player.playSound(player.location, Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1.0f, 0.15f)

                player.sendTitle(ChatColor.GREEN.toString() + "Game Started", "", 10, 30, 10)

                worldBorder = Bukkit.getWorlds()[0].worldBorder

                worldBorder.setSize(4000.00, 0)
            }
            else if (TimerCore.seconds == 0 && TimerCore.minutes == 0 && TimerCore.hours == 0) {
                setDifficulty(Difficulty.PEACEFUL)
                HighCraft.callDatapackFunction("soultiz:spreadplayers")

                if (player.gameMode == GameMode.ADVENTURE) {
                    clearInventory(player)
                    player.gameMode = GameMode.SURVIVAL
                }
            }

            //1 mins
            else if (TimerCore.seconds == 0 && TimerCore.minutes == 1 && TimerCore.hours == 0) {
                setGameRule(GameRule.FALL_DAMAGE, true)
                setDifficulty(Difficulty.NORMAL)
            }

        }
    }


    private fun clearInventory(player: Player) {
        player.inventory.clear()
    }

    private fun <T : Any> setGameRule(rule: GameRule<T>, value: T) {
        Bukkit.getServer().worlds.forEach { world ->
            world.setGameRule(rule, value)
        }
    }

    private fun setDifficulty(difficulty: Difficulty) {
        Bukkit.getServer().worlds.forEach { world ->
            world.difficulty = difficulty
        }
    }
}
