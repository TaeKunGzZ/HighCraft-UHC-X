package org.taekungz.highcraft.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.taekungz.highcraft.HighCraft
import org.taekungz.highcraft.core.TimerCore

class TimerCommand : CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        if (sender.isOp) {
            if (args.size >= 2 && args[0].equals("set", ignoreCase = true) && args[1].equals("hours", ignoreCase = true)) {
                return try {
                    val hours = args[2].toInt()
                    TimerCore.hours = hours
                    sender.sendMessage("UHC timer set to $hours hours.")
                    true
                } catch (e: NumberFormatException) {
                    sender.sendMessage("Invalid number format. Please provide a valid integer for hours.")
                    false
                }
            }
            if (args.size >= 2 && args[0].equals("set", ignoreCase = true) && args[1].equals("minutes", ignoreCase = true)) {
                return try {
                    val minutes = args[2].toInt()
                    TimerCore.minutes = minutes
                    sender.sendMessage("UHC timer set to $minutes minutes.")
                    true
                } catch (e: NumberFormatException) {
                    sender.sendMessage("Invalid number format. Please provide a valid integer for minutes.")
                    false
                }
            }
            if (args.size >= 2 && args[0].equals("set", ignoreCase = true) && args[1].equals("seconds", ignoreCase = true)) {
                return try {
                    val seconds = args[2].toInt()
                    TimerCore.seconds = seconds
                    sender.sendMessage("UHC timer set to $seconds seconds.")
                    true
                } catch (e: NumberFormatException) {
                    sender.sendMessage("Invalid number format. Please provide a valid integer for seconds.")
                    false
                }
            }

            if (args.isNotEmpty() && args[0].equals("start", ignoreCase = true)) {
                HighCraft.isTimerActive = true
                sender.sendMessage("UHC timer has been started.")
                return true
            }
            if (args.isNotEmpty() && args[0].equals("stop", ignoreCase = true)) {
                HighCraft.isTimerActive = false
                sender.sendMessage("UHC timer has been stopped.")
                return true
            }
        }
        return false
    }
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        val completions = mutableListOf<String>()

        if (args.size == 1) {
            completions.add("start")
            completions.add("stop")
            completions.add("set")
        }
        if (args.size == 2) {
            completions.add("seconds")
            completions.add("minutes")
            completions.add("hours")
        }

        return completions
    }
}