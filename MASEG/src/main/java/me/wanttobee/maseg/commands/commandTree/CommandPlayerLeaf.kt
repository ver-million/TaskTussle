package me.wanttobee.maseg.commands.commandTree

import me.wanttobee.maseg.MASEGPlugin
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class CommandPlayerLeaf(arg : String,private val possiblePlayers: Collection<Player>, effect : (Player, Player) -> Unit, emptyEffect : ((Player) -> Unit)? = null ) : ICommandLeaf<Player>(arg,effect, emptyEffect) {

    override fun validateValue(sender: Player, tailArgs: Array<String>): Player? {
        var isRealPlayer = false
        for(player in MASEGPlugin.instance.server.onlinePlayers){
            if(player.name.lowercase() == tailArgs.first()){
                isRealPlayer = true
                break
            }
        }
        if(!isRealPlayer){
            sender.sendMessage("${ChatColor.GRAY}${tailArgs.first()} ${ChatColor.RED}is not online")
            return null
        }

        for(pos in possiblePlayers){
            if(pos.name.lowercase() == tailArgs.first())
                return pos
        }
        sender.sendMessage("${ChatColor.GRAY}${tailArgs.first()} ${ChatColor.RED}cant be entered as an argument")
        return null
    }

    override fun thisTabComplete(sender: Player, currentlyTyping: String): List<String> {
        val list = mutableListOf<String>()
        if(possiblePlayers.isEmpty()){
            if("" == currentlyTyping)
                list.add("no players available")
        } else {
            for (pos in possiblePlayers) {
                if (pos.name.lowercase().startsWith(currentlyTyping))
                    list.add(pos.name)
            }
        }
        return list
    }
}