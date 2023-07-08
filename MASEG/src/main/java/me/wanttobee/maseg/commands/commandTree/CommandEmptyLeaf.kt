package me.wanttobee.maseg.commands.commandTree

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.Objects

class CommandEmptyLeaf(arg : String, emptyEffect : ((Player) -> Unit) ) : ICommandLeaf<Unit>(arg,{_,_->}, emptyEffect) {

    override fun onCommand(sender: Player, tailArgs: Array<String>) {
        emptyEffect!!.invoke(sender)
    }

    override fun validateValue(sender: Player, tailArgs: Array<String>) {}

    override fun thisTabComplete(sender: Player, currentlyTyping: String): List<String> {
        return emptyList()
    }
}