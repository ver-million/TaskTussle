package me.wanttobee.maseg.systems.utils.playerCompass

import me.wanttobee.maseg.MASEGPlugin
import me.wanttobee.maseg.commands.ISystemCommand
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object PlayerCompassCommand : ISystemCommand {
    override val exampleCommand = "/mu playerCompass [refreshTime/Ticks] [focusClosest/Boolean] [saveWorldLocation/Boolean] [target/Players]+"
    override val helpText = "gives compass for the given player with the give settings"
    override val key = "playerCompass"
    override val aliases = arrayOf("compass")

    private val plugin = MASEGPlugin.instance

    override fun onCommand(sender: Player, args : Array<String>) {

        if(args.isNotEmpty() && args[0] == "list"){
            PlayerCompassSystem.printListTo(sender)
            return
        }
        if(args.size < 2 ) {
            sender.sendMessage("${ChatColor.DARK_RED} not enough arguments")
            return
        }
        val number = args[1].toIntOrNull() ?: run {
            sender.sendMessage("${ChatColor.RED}${args[1]}${ChatColor.DARK_RED} is not a valid number")
            return
        }

        when (args[0]) {
            "start" -> {
                if(args.size < 4) {
                    sender.sendMessage("${ChatColor.DARK_RED} not enough arguments")
                    return
                }
                val closest = args[2].toBooleanStrictOrNull() ?: run {
                    sender.sendMessage("${ChatColor.RED}${args[2]}${ChatColor.DARK_RED} is not a valid boolean")
                    return
                }
                val saveWorldLocation = args[3].toBooleanStrictOrNull() ?: run {
                    sender.sendMessage("${ChatColor.RED}${args[3]}${ChatColor.DARK_RED} is not a valid boolean")
                    return
                }
                val players = Array(args.size-4 ) {i -> plugin.server.getPlayer(args[i+4]) ?: run {
                    sender.sendMessage("${ChatColor.RED}${args[i+4]}${ChatColor.DARK_RED} doesnt exists or is not a player")
                    return
                }}
                val id = PlayerCompassSystem.startCompass(number,closest,saveWorldLocation,players)
                sender.sendMessage("${ChatColor.GREEN}started tracker as:${ChatColor.GOLD} $id")
            }
            "stop" ->{
                val done = PlayerCompassSystem.stopCompass(number)
                if(!done) sender.sendMessage("${ChatColor.DARK_RED} the tracker id: ${ChatColor.RED}$number${ChatColor.DARK_RED} doesn't exist")
                else sender.sendMessage("${ChatColor.GREEN}stopped tracker:${ChatColor.GOLD} $number")
                return
            }
            "get" -> {
                val done = PlayerCompassSystem.giveCompassFor(sender, number)
                if(!done) sender.sendMessage("${ChatColor.DARK_RED} the tracker id: ${ChatColor.RED}$number${ChatColor.DARK_RED} doesn't exist")
                return
            }
            "addplayer" -> {
                val player = plugin.server.getPlayer( args[2]) ?: run {
                    sender.sendMessage("${ChatColor.RED}${args[2]}${ChatColor.DARK_RED} doesnt exists or is not a player")
                    return
                }
                PlayerCompassSystem.addPlayerToCompass(player, number)
            }
            "removeplayer" -> {
                val player = plugin.server.getPlayer( args[2]) ?: run {
                    sender.sendMessage("${ChatColor.RED}${args[2]}${ChatColor.DARK_RED} doesnt exists or is not a player")
                    return
                }
                PlayerCompassSystem.removePlayerFromCompass(player, number)
            }
            else ->{
                sender.sendMessage("${ChatColor.RED}${args[0]}${ChatColor.DARK_RED} is not a valid argument")
                return
            }

        }


    }

    override fun onTabComplete(sender: Player,  args : Array<String>) : List<String> {
        val list : MutableList<String> = mutableListOf<String>();
        val size = args.size
        if(size == 1){
            if("start".startsWith(args[0])) list.add("start")
            if("stop".startsWith(args[0])) list.add("stop")
            if("get".startsWith(args[0])) list.add("get")
            if("list".startsWith(args[0])) list.add("list")
            if("addplayer".startsWith(args[0])) list.add("addPlayer")
            if("removeplayer".startsWith(args[0])) list.add("removePlayer")
        }
        if((size == 3 || size == 4)  && args[0] == "start"){
            if("true".startsWith(args[2])) list.add("true")
            if("false".startsWith(args[2])) list.add("false")
        }
        if(size >= 5 && args[0] == "start"){
            for(p in plugin.server.onlinePlayers){
                if(p.name.lowercase().startsWith(args[size-1])) list.add(p.name)
            }
        }
        if(size == 2 && (args[0] == "addplayer" || args[0] == "removeplayer" || args[0] == "stop" || args[0] == "get")){
            for(p in PlayerCompassSystem.playerCompasses.keys){
                if(p.toString().startsWith(args[1])) list.add(p.toString())
            }
        }
        if(size == 3 && (args[0] == "addplayer" || args[0] == "removeplayer") ) {
            for(p in plugin.server.onlinePlayers){
                if(p.name.lowercase().startsWith(args[2])) list.add(p.name)
            }
        }
        return list
    }

}