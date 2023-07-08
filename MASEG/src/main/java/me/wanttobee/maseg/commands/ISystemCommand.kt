package me.wanttobee.maseg.commands

import net.md_5.bungee.api.ChatColor
import org.bukkit.entity.Player

interface ISystemCommand {
    val exampleCommand : String
    val helpText : String
    val key : String
    val aliases : Array<String>

    fun onCommand(sender: Player, args : Array<String>)
    fun onTabComplete(sender: Player, args : Array<String>) : List<String>

}



