package org.taekungz.highcraft.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class LobbyCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        if (sender.isOp) {
            val targetLobby: World? = Bukkit.getWorld("lobby")
            val targetWorld: World? = Bukkit.getWorld("world")
            if (args.isNotEmpty() && args[0].equals("1", ignoreCase = true)) {
                if (targetLobby != null) {
                    if (sender.world.name.equals("lobby", ignoreCase = true)) {
                        sender.sendMessage(ChatColor.RED.toString() + "You are already in Lobby.")
                    }
                    else {
                        val targetLoc1 = Location(targetLobby, 0.0, 50.0, 0.0)
                        sender.teleport(targetLoc1)
                    }
                }
                return true
            }

            if (args.isNotEmpty() && args[0].equals("2", ignoreCase = true)) {
                if (targetWorld != null) {
                    if (sender.world.name.equals("world", ignoreCase = true)) {
                        sender.sendMessage(ChatColor.RED.toString() + "You are already in World.")
                    }
                    else {
                        val targetLoc2 = Location(targetWorld, 0.0, 50.0, 0.0)
                        sender.teleport(targetLoc2)
                    }
                }
                return true
            }
        }
        else {
            sender.sendMessage(ChatColor.RED.toString() + "This command is not allow player that non-Op to executed.")
        }
        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val completions = mutableListOf<String>()

        if (args.size == 1) {
            completions.add("1")
            completions.add("2")
        }

        return completions
    }
}