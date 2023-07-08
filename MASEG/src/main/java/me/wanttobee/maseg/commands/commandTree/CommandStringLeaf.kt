package me.wanttobee.maseg.commands.commandTree

import org.bukkit.ChatColor
import org.bukkit.entity.Player

class CommandStringLeaf(arg : String, private val possibilities : Array<String>?, effect : (Player, String) -> Unit, emptyEffect : ((Player) -> Unit)? = null ) : ICommandLeaf<String>(arg,effect, emptyEffect) {

    override fun validateValue(sender: Player, tailArgs: Array<String>): String? {
        if(tailArgs.first() == "..."){
            if(emptyEffect != null) emptyEffect.invoke(sender)
            else sender.sendMessage("${ChatColor.RED}these ${ChatColor.GRAY}...${ChatColor.RED} are there to convey that you could type any string, but not literally ${ChatColor.GRAY}...")
            return null
        }
        if(possibilities == null )
            return tailArgs.first()

        for(pos in possibilities){
            if(pos.lowercase() == tailArgs.first())
                return tailArgs.first()
        }
        sender.sendMessage("${ChatColor.GRAY}${tailArgs.first()} ${ChatColor.RED}is not a valid argument")
        return null
    }

    override fun thisTabComplete(sender: Player, currentlyTyping: String): List<String> {
        val list = mutableListOf<String>()
        if(possibilities == null){
            if("" == currentlyTyping)
                list.add("...")
        } else {
            for (pos in possibilities) {
                if (pos.lowercase().startsWith(currentlyTyping))
                    list.add(pos)
            }
        }
        return list
    }
}