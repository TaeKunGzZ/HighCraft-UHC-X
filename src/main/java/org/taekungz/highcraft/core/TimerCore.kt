package org.taekungz.highcraft.core

import org.bukkit.scheduler.BukkitRunnable
import org.taekungz.highcraft.HighCraft

class TimerCore : BukkitRunnable() {
    companion object {
        var seconds = -100
        var minutes = 0
        var hours = 0
    }

    override fun run() {
        if (HighCraft.isTimerActive) seconds++

        if (seconds >= 60) {
            minutes++
            seconds = 0
        }

        if (minutes >= 60) {
            hours++
            minutes = 0
        }
    }

    fun getSeconds(): Int {
        return seconds
    }
    fun getMinutes(): Int {
        return minutes
    }
    fun getHours(): Int {
        return hours
    }
}