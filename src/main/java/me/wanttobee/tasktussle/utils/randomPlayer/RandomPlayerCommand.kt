package me.wanttobee.tasktussle.utils.randomPlayer

import me.wanttobee.tasktussle.TTPlugin
import me.wanttobee.tasktussle.commands.ISystemCommand
import me.wanttobee.tasktussle.commands.commandTree.*
import org.bukkit.entity.Player


object RandomPlayerCommand : ISystemCommand {
    private val plugin = TTPlugin.instance
    override val exampleCommand = "/mu randomPlayer [amount/Int] [playerPool/Player]+ "
    override val helpText = "selects a random player, if player pool is given, it will select random from that playerPool, otherwise from everyone online"

    override val baseTree = CommandPairLeaf<Int,List<Player>>("randomPlayer",
        CommandIntLeaf("amountSelected", { 1 }, { plugin.server.onlinePlayers.size }, {_,_ -> }),
        CommandVarargLeaf("playerPool",
            CommandPlayerLeaf("player", { plugin.server.onlinePlayers }, {_,_ -> } ), true,{_,_ -> }),
        { sender, args ->
            if(args.second.isEmpty())
                RandomPlayerSystem.choseRandomTeam(sender,args.first)
            else
                RandomPlayerSystem.choseRandomTeam(sender,args.first,args.second)}
    )

}