package me.wanttobee.maseg.systems.utils.playerTracker

import me.wanttobee.maseg.MASEGPlugin
import me.wanttobee.maseg.commands.ISystemCommand
import me.wanttobee.maseg.commands.commandTree.*
import me.wanttobee.maseg.systems.utils.teams.Team
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object PlayerTrackerCommand : ISystemCommand {
    private val plugin = MASEGPlugin.instance
    override val exampleCommand = "/mu playerTracker"
    override val helpText = "everything for setting up the playerTracker, all games that use a tracker have al these settings baked in so you probably dont need this anyway"


    private val listTree = CommandEmptyLeaf("list") {p -> PlayerTrackerSystem.printListTo(p) }
    private val getTrackerTree = CommandIntLeaf("getTracker", PlayerTrackerSystem.getIDList(), {p,i ->
        val done = PlayerTrackerSystem.giveTracker(p, i)
        if(done) p.sendMessage("${ChatColor.GREEN}tracker for ${ChatColor.GRAY}$i${ChatColor.GREEN} has been given")
        else p.sendMessage("${ChatColor.GRAY}$i${ChatColor.RED}doesn't have a tracker assigned")
    })
    private val startTree = CommandPairLeaf<Player,Pair<Player,Pair<Int,Pair<Boolean, Boolean>>>>("startTracker",
        CommandPlayerLeaf("target1", plugin.server.onlinePlayers, {_,_ -> }),
        CommandPairLeaf("",
            CommandPlayerLeaf("target2",plugin.server.onlinePlayers, {_,_ -> }),
            CommandPairLeaf("",
                CommandIntLeaf("refreshTime",20, null,{_,_ -> }),
                CommandPairLeaf("booleans",
                    CommandBoolLeaf("focusClosest", {_,_ -> }),
                    CommandBoolLeaf("saveWorldLocation", {_,_ -> }))
            )),
        {sender, pairs ->
            val team = Team(ChatColor.WHITE)
            team.addMember(pairs.first)
            team.addMember(pairs.second.first)
            val id = PlayerTrackerSystem.startTracker(
                team,
                pairs.second.second.first,
                pairs.second.second.second.first,
                pairs.second.second.second.second)
            sender.sendMessage("${ChatColor.GREEN}started a tracker with id: ${ChatColor.GRAY}$id")
        }
    )


    private val stopTree = CommandIntLeaf( "stopTracker", PlayerTrackerSystem.getIDList() ,
        { p,id ->
            val done =  PlayerTrackerSystem.stopTracking(id)
            if(done) p.sendMessage("${ChatColor.GREEN}stopped tracker ${ChatColor.GRAY}$id")
            else p.sendMessage("${ChatColor.RED}cant stop a tracker that doesn't exist")
        } )

    override val baseTree = CommandTree("playerTracker", arrayOf(
        listTree,
        getTrackerTree ,
        startTree,
        stopTree
    ))
}