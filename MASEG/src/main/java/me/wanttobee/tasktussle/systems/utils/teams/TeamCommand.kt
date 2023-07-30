package me.wanttobee.tasktussle.systems.utils.teams

import me.wanttobee.tasktussle.TTPlugin
import me.wanttobee.tasktussle.commands.ISystemCommand
import me.wanttobee.tasktussle.commands.commandTree.CommandEmptyLeaf
import me.wanttobee.tasktussle.commands.commandTree.CommandIntLeaf
import me.wanttobee.tasktussle.commands.commandTree.CommandTree

object TeamCommand  : ISystemCommand {
    private val plugin = TTPlugin.instance
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