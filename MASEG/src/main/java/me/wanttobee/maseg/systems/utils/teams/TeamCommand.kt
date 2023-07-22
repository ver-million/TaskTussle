package me.wanttobee.maseg.systems.utils.teams

import me.wanttobee.maseg.MASEGPlugin
import me.wanttobee.maseg.commands.ISystemCommand
import me.wanttobee.maseg.commands.commandTree.CommandEmptyLeaf
import me.wanttobee.maseg.commands.commandTree.CommandIntLeaf
import me.wanttobee.maseg.commands.commandTree.CommandTree

object TeamCommand  : ISystemCommand {
    private val plugin = MASEGPlugin.instance
    override val exampleCommand = "/mu teams list"
    override val helpText = "just to see a list of teams that are currently active (only useful for debugging, most games will show teams in other places)"


    private val listTree = CommandEmptyLeaf("list") {p -> TeamSystem.listTeams(p) }
    private val deleteTree = CommandEmptyLeaf("clearAll") { _ -> TeamSystem.clearAll() }
    private val generateTree = CommandIntLeaf("makeXTeams", null,  {_,i -> TeamSystem.makeXTeams(i,"Command Generated"){} } )
    private val makeTree = CommandIntLeaf("teamMaker", 2, 54,  {p,i -> TeamSystem.teamMaker(p,{},i,"Command Made"){} } )

    override val baseTree = CommandTree("teams", arrayOf(
        listTree,
        deleteTree,
        generateTree,
        makeTree
    ))
}