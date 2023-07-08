package me.wanttobee.maseg.commands

import me.wanttobee.maseg.systems.games.bingo.BingoCommand
import me.wanttobee.maseg.systems.games.manHunt.ManHuntCommand
import org.bukkit.ChatColor

object MGCommands : ICommandSystem {
    override val title = "${ChatColor.GRAY}[${ChatColor.GOLD}MASEG${ChatColor.GRAY}]${ChatColor.RESET}"
    override val helpText: String = "${ChatColor.GRAY}/mg${ChatColor.WHITE} for maseg games"

    override val systemCommands : Array<ISystemCommand> = arrayOf(
        ManHuntCommand,
        BingoCommand,
    )
}