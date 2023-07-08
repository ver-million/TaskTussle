package me.wanttobee.maseg.commands.commandTree

import org.bukkit.ChatColor
import org.bukkit.entity.Player

class CommandBoolLeaf(arg : String, effect : (Player, Boolean) -> Unit, emptyEffect : ((Player) -> Unit)? = null ) : ICommandLeaf<Boolean>(arg,effect, emptyEffect) {

    override fun validateValue(sender: Player, tailArgs: Array<String>): Boolean? {
        val bool = tailArgs.first().toBooleanStrictOrNull() ?: run {
            sender.sendMessage("${ChatColor.GRAY}${tailArgs.first()} ${ChatColor.RED}is not a valid boolean ${ChatColor.DARK_RED}(true/false)")
            return null
        }
        return bool
    }

    override fun thisTabComplete(sender: Player, currentlyTyping: String): List<String> {
        val list = mutableListOf<String>()
        if("true".startsWith(currentlyTyping)) list.add("true")
        if("false".startsWith(currentlyTyping)) list.add("false")
        return list
    }
}