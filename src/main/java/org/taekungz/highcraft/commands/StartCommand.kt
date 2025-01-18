package org.taekungz.highcraft.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.taekungz.highcraft.HighCraft
import org.taekungz.highcraft.core.TimerCore

class StartCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        if (sender.isOp) {
            HighCraft.isSBnotone = true

            TimerCore.seconds = -60
            TimerCore.minutes = 0
            TimerCore.hours = 0
            HighCraft.isTimerActive = true


            HighCraft.worldBorder = Bukkit.getWorlds()[0].worldBorder

            HighCraft.worldBorder.setSize(50.00, 0)
        }
        else {
            sender.sendMessage(ChatColor.RED.toString() + "This command is not allow player that non-Op to executed.")
        }
        return false
    }
}