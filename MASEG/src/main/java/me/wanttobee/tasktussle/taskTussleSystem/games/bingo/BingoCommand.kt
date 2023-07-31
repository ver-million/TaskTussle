package me.wanttobee.tasktussle.taskTussleSystem.games.bingo

import me.wanttobee.tasktussle.commands.ISystemCommand
import me.wanttobee.tasktussle.commands.commandTree.CommandIntLeaf
import me.wanttobee.tasktussle.commands.commandTree.CommandStringLeaf
import me.wanttobee.tasktussle.commands.commandTree.ICommandBranch
import me.wanttobee.tasktussle.taskTussleSystem.TaskTussleSystem
import org.bukkit.ChatColor

object BingoCommand : ISystemCommand {
    override val exampleCommand: String = "/taskTussle start bingo"
    override val helpText: String = "to start a bingo game"

    val settings : Array<ICommandBranch> =  arrayOf(
        CommandStringLeaf("bingo_winCondition", BingoGameSystem.possibleConditions,
            { p,arg -> BingoGameSystem.winningCondition = arg; p.sendMessage("${ChatColor.GRAY}(bingo) ${ChatColor.GOLD}winCondition${ChatColor.WHITE} is changed to: ${ChatColor.GOLD}$arg") },
            { p -> p.sendMessage("${ChatColor.GRAY}(bingo) ${ChatColor.GOLD}winCondition${ChatColor.WHITE} is currently: ${ChatColor.GOLD}${BingoGameSystem.winningCondition}") }),
        CommandIntLeaf("bingo_mutualItems", 0, null,
            { p,arg -> BingoGameSystem.mutualItems = arg; p.sendMessage("${ChatColor.GRAY}(bingo) ${ChatColor.GOLD}mutualItems${ChatColor.WHITE} is changed to: ${ChatColor.GOLD}$arg") },
            { p -> p.sendMessage("${ChatColor.GRAY}(bingo) ${ChatColor.GOLD}mutualItems${ChatColor.WHITE} is currently: ${ChatColor.GOLD}${BingoGameSystem.mutualItems}") }),
    )
    override val baseTree: ICommandBranch = CommandIntLeaf("bingo", 1, 15,
        {commander, size -> TaskTussleSystem.start(commander,size,BingoGameSystem) },
        {commander -> commander.sendMessage("${ChatColor.RED}you must specify the amount of teams you want to play with") }
    )
}