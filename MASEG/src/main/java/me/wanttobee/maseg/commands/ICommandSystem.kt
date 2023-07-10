package me.wanttobee.maseg.commands

import me.wanttobee.maseg.MASEGPlugin
import org.bukkit.ChatColor
import org.bukkit.entity.Player

interface ICommandSystem : IPlayerCommands {
    val helpText : String
    val systemCommands : Array<ISystemCommand>

    override fun onCommand(sender: Player, args: Array<String>): Boolean {
        for(sysCom in systemCommands){
            if (sysCom.baseTree.arg.lowercase() == args.first()) {
                sysCom.baseTree.onCommand(sender, args.copyOfRange(1, args.size))
                break
            }
        }
        return true
    }

    override fun help(sender: Player){
        sender.sendMessage("${MASEGPlugin.title} $helpText")
        val helperTab : (String)-> String = { h -> "${ChatColor.YELLOW}$h${ChatColor.WHITE}"}
        for(sysCom in systemCommands){
            sender.sendMessage("- ${helperTab(sysCom.baseTree.arg + ":")} ${sysCom.helpText} ${ChatColor.GRAY}${sysCom.exampleCommand}")
        }
    }

    override fun onTabComplete(sender: Player, args: Array<String>): List<String> {
        val list : MutableList<String> = mutableListOf();

        if(args.size == 1){
            for(sysCom in systemCommands){
                if(sysCom.baseTree.arg.lowercase().startsWith(args.first()))
                    list.add(sysCom.baseTree.arg)
            }
            return list
        }

        for(sysCom in systemCommands){
            if(sysCom.baseTree.arg.lowercase() == args.first())
                return sysCom.baseTree.getTabComplete(sender, args.copyOfRange(1, args.size) )
        }
        return list
    }

}