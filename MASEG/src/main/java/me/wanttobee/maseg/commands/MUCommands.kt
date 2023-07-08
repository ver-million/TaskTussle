package me.wanttobee.maseg.commands

import me.wanttobee.maseg.systems.utils.playerTracker.PlayerTrackerCommand
import me.wanttobee.maseg.systems.utils.randomPlayer.RandomPlayerCommand
import org.bukkit.ChatColor


object MUCommands : ICommandSystem {
    override val title = "${ChatColor.GRAY}[${ChatColor.GOLD}MASEG${ChatColor.GRAY}]${ChatColor.RESET}"
    override val helpText: String = "${ChatColor.GRAY}/mu${ChatColor.WHITE} for maseg utilities"

    override val systemCommands : Array<ISystemCommand> = arrayOf(
        RandomPlayerCommand,
        PlayerTrackerCommand,
    )
}