package me.wanttobee.maseg.systems.utils.randomPlayer

import me.wanttobee.maseg.MASEGPlugin
import me.wanttobee.maseg.commands.ISystemCommand
import me.wanttobee.maseg.commands.commandTree.*
import org.bukkit.entity.Player


object RandomPlayerCommand : ISystemCommand {
    private val plugin = MASEGPlugin.instance
    override val exampleCommand = "/mu randomPlayer [amount/Int] [without/Players]+ "
    override val helpText = "selects a random player"

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