package me.wanttobee.maseg.commands

import org.bukkit.ChatColor
import org.bukkit.entity.Player

interface ICommandSystem : IPlayerCommands {
    val title : String
    val helpText : String
    val systemCommands : Array<ISystemCommand>

    override fun onCommand(sender: Player, args: Array<String>): Boolean {
        var checkAliases = true;
        for(sysCom in systemCommands){
            if (sysCom.key.lowercase() == args[0]) {
                sysCom.onCommand(sender, args.copyOfRange(1, args.size))
                checkAliases = false
                break
            }
        }

        if(checkAliases){
            for(sysCom in systemCommands){
                if (sysCom.aliases.contains(args[0])) {
                    sysCom.onCommand(sender, args.copyOfRange(1, args.size))
                    break
                }
            }
        }

        return true
    }

    override fun help(sender: Player){
        sender.sendMessage("$title $helpText")
        val helperTab : (String)-> String = { h -> "${ChatColor.YELLOW}$h${ChatColor.WHITE}"}
        for(sysCom in systemCommands){
            sender.sendMessage("- ${helperTab(sysCom.key + ":")} ${sysCom.helpText} ${ChatColor.GRAY}${sysCom.exampleCommand}")
        }
    }

    override fun onTabComplete(sender: Player, args: Array<String>): List<String> {
        val list : MutableList<String> = mutableListOf();

        if(args.size == 1){
            for(sysCom in systemCommands){
                if(sysCom.key.lowercase().startsWith(args[0]))
                    list.add(sysCom.key)
            }
            return list
        }

        for(sysCom in systemCommands){
            if(sysCom.key.lowercase() == args[0])
                return sysCom.onTabComplete(sender, args.copyOfRange(1, args.size) )
        }
        return list
    }

}