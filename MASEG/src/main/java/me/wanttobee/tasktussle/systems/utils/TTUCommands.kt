package me.wanttobee.tasktussle.systems.utils

import me.wanttobee.tasktussle.commands.ICommandSystem
import me.wanttobee.tasktussle.commands.ISystemCommand
import me.wanttobee.tasktussle.systems.utils.playerTracker.PlayerTrackerCommand
import me.wanttobee.tasktussle.systems.utils.randomPlayer.RandomPlayerCommand
import me.wanttobee.tasktussle.systems.utils.teams.TeamCommand
import org.bukkit.ChatColor


object TTUCommands : ICommandSystem {
    override val helpText: String = "${ChatColor.GRAY}/ttu${ChatColor.WHITE} for Task Tussle Utilities"

    override val systemCommands : Array<ISystemCommand> = arrayOf(
        RandomPlayerCommand,
        PlayerTrackerCommand,
        TeamCommand,
    )
}