package me.wanttobee.maseg.commands

import me.wanttobee.maseg.systems.utils.playerTracker.PlayerTrackerCommand
import me.wanttobee.maseg.systems.utils.randomPlayer.RandomPlayerCommand
import me.wanttobee.maseg.systems.utils.teams.TeamCommand2
import org.bukkit.ChatColor


object MUCommands : ICommandSystem {
    override val helpText: String = "${ChatColor.GRAY}/mu${ChatColor.WHITE} for Maseg Utilities"

    override val systemCommands : Array<ISystemCommand> = arrayOf(
        RandomPlayerCommand,
        PlayerTrackerCommand,
        TeamCommand2,
    )
}