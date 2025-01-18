package org.taekungz.highcraft.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.taekungz.highcraft.HighCraft

class SBtoggleCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        if (sender.isOp) {
            if (args.isNotEmpty() && args[0].equals("toggle", ignoreCase = true)) {
                HighCraft.isSBnotone = !HighCraft.isSBnotone
                return true
            }
        }
        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val completions = mutableListOf<String>()

        if (args.size == 1) {
            completions.add("toggle")
        }

        return completions
    }
}