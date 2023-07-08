package me.wanttobee.maseg.systems.utils.randomPlayer

import me.wanttobee.maseg.MASEGPlugin
import me.wanttobee.maseg.commands.ISystemCommand
import me.wanttobee.maseg.systems.utils.playerCompass.PlayerCompassCommand
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

object RandomPlayerCommand : ISystemCommand {
    override val exampleCommand = "/mu randomPlayer [jumps/Int] [amount/Int] [without/Players]+ "
    override val helpText = "selects a random player"
    override val key = "randomPlayer"
    override val aliases = arrayOf("random")

    private val plugin = MASEGPlugin.instance

    override fun onCommand(sender: Player, args : Array<String>) {
        if(args.size < 2 ) {
            sender.sendMessage("${ChatColor.DARK_RED} not enough arguments")
            return
        }
        val jumps = args[0].toIntOrNull() ?: run {
            sender.sendMessage("${ChatColor.RED}${args[0]}${ChatColor.DARK_RED} is not a valid number")
            return
        }
        val amount = args[1].toIntOrNull() ?: run {
            sender.sendMessage("${ChatColor.RED}${args[1]}${ChatColor.DARK_RED} is not a valid number")
            return
        }
        val withoutPlayers = Array(args.size-2 ) {i -> plugin.server.getPlayer(args[i+2]) ?: run {
            sender.sendMessage("${ChatColor.RED}${args[i+2]}${ChatColor.DARK_RED} doesnt exists or is not a player")
            return
        }}
        val allPlayers = plugin.server.onlinePlayers
        val clampedJumps = 0.coerceAtLeast(15.coerceAtMost(jumps))
        if(jumps != clampedJumps) sender.sendMessage("${ChatColor.DARK_RED}Clamped ${ChatColor.RED}$jumps${ChatColor.DARK_RED} to be between 0 and 15")
        val clampedAmount = 1.coerceAtLeast((plugin.server.onlinePlayers.size).coerceAtMost(amount))
        if(amount != clampedAmount) sender.sendMessage("${ChatColor.DARK_RED}Clamped ${ChatColor.RED}$amount${ChatColor.DARK_RED} to be between 1 and ${plugin.server.onlinePlayers.size}")

        RandomPlayerSystem.doRandomPlayerProcess( clampedJumps, clampedAmount,withoutPlayers,null )
    }

    override fun onTabComplete(sender: Player,  args : Array<String>) : List<String> {
        if(args.size >= 3 ){
            val list : MutableList<String> = mutableListOf<String>();
            for(p in plugin.server.onlinePlayers){
                if(p.name.lowercase().startsWith(args[args.size-1])) list.add(p.name)
            }
            return list
        }
        return emptyList()
    }

}