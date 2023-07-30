package me.wanttobee.maseg.systems.games.taskTussle.bingo

import me.wanttobee.maseg.commands.ISystemCommand
import me.wanttobee.maseg.commands.commandTree.CommandIntLeaf
import me.wanttobee.maseg.commands.commandTree.CommandStringLeaf
import me.wanttobee.maseg.commands.commandTree.ICommandBranch
import me.wanttobee.maseg.systems.games.taskTussle.TaskTussleSystem
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
    override val baseTree: ICommandBranch = CommandIntLeaf("bingo", 1, 10,
        {commander, size -> TaskTussleSystem.start(commander,size,BingoGameSystem) },
        {commander -> commander.sendMessage("${ChatColor.RED}you must specify the amount of teams you want to play with") }
    )
}