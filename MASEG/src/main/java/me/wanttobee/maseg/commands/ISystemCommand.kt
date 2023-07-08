package me.wanttobee.maseg.commands

import me.wanttobee.maseg.commands.commandTree.ICommandBranch
import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player

interface ISystemCommand {

    val exampleCommand : String
    val helpText : String

    val baseTree : ICommandBranch
}



