package me.wanttobee.maseg.systems.games.taskTussle

import me.wanttobee.maseg.MASEGPlugin
import me.wanttobee.maseg.commands.ICommandSystem
import me.wanttobee.maseg.commands.ISystemCommand
import me.wanttobee.maseg.commands.commandTree.*
import me.wanttobee.maseg.systems.games.taskTussle.bingo.BingoCommand
import me.wanttobee.maseg.systems.games.taskTussle.tasks.ObtainTaskFiles
import me.wanttobee.maseg.systems.games.taskTussle.tasks.ObtainTaskManager
import org.bukkit.ChatColor

object TaskTussleCommand : ICommandSystem {
    private val plugin = MASEGPlugin.instance
    override val helpText = "/taskTussle to start a game or change settings before starting the game"

    object StartTree : ISystemCommand {
        override val exampleCommand: String = "/taskTussle start [game]"
        override val helpText: String = "to start one of the games"
        override val baseTree = CommandTree("start", arrayOf(
                BingoCommand.baseTree
        ))
    }

    object DebugTree : ISystemCommand {
        override val exampleCommand: String = "/taskTussle debugStatus"
        override val helpText: String = "check some information just about its current running game (no real usage)"
        override val baseTree = CommandEmptyLeaf("debugStatus") { commander -> TaskTussleSystem.debugStatus(commander) }
    }

    object StopTree : ISystemCommand {
        override val exampleCommand: String = "/taskTussle stop"
        override val helpText: String = "to stop the current running game"
        override val baseTree = CommandEmptyLeaf("stop") { commander -> TaskTussleSystem.stop(commander) }
    }

    object SettingsTree : ISystemCommand {
        private val mutualCommands : Array<ICommandBranch> = arrayOf(
                CommandBoolLeaf("tt_choseTeamsBeforehand",
                        { p, arg -> TaskTussleSystem.choseTeamsBeforehand = arg; p.sendMessage("${ChatColor.GOLD}choseTeamsBeforehand${ChatColor.WHITE} is changed to: ${ChatColor.GOLD}$arg") },
                        { p -> p.sendMessage("${ChatColor.GOLD}choseTeamsBeforehand${ChatColor.WHITE} is currently: ${ChatColor.GOLD}${TaskTussleSystem.choseTeamsBeforehand}") }),
                CommandBoolLeaf("tt_hideCard",
                        { p, arg -> TaskTussleSystem.hideCard = arg; p.sendMessage("${ChatColor.GOLD}hideCard${ChatColor.WHITE} is changed to: ${ChatColor.GOLD}$arg") },
                        { p -> p.sendMessage("${ChatColor.GOLD}hideCard${ChatColor.WHITE} is currently: ${ChatColor.GOLD}${TaskTussleSystem.hideCard}") }),
                CommandIntLeaf("tt_gameTime", 1, null,
                        { p, arg -> TaskTussleSystem.gameTime = arg; p.sendMessage("${ChatColor.GOLD}gameTime${ChatColor.WHITE} is changed to: ${ChatColor.GOLD}$arg ${ChatColor.WHITE}minutes") },
                        { p -> p.sendMessage("${ChatColor.GOLD}gameTime${ChatColor.WHITE} is currently: ${ChatColor.GOLD}${TaskTussleSystem.gameTime} ${ChatColor.WHITE}minutes") }),
                CommandIntLeaf("tt_easyRatio", 0, null,
                        { p, arg -> TaskTussleSystem.easyRatio = arg; p.sendMessage("${ChatColor.GOLD}easyRatio${ChatColor.WHITE} is changed to: ${ChatColor.GOLD}$arg${ChatColor.GRAY}/${TaskTussleSystem.normalRatio}/${TaskTussleSystem.hardRatio}") },
                        { p -> p.sendMessage("${ChatColor.GOLD}easyRatio${ChatColor.WHITE} is currently: ${ChatColor.GOLD}${TaskTussleSystem.easyRatio}${ChatColor.GRAY}/${TaskTussleSystem.normalRatio}/${TaskTussleSystem.hardRatio}") }),
                CommandIntLeaf("tt_normalRatio", 0, null,
                        { p, arg -> TaskTussleSystem.normalRatio = arg; p.sendMessage("${ChatColor.GOLD}normalRatio${ChatColor.WHITE} is changed to: ${ChatColor.GRAY}${TaskTussleSystem.easyRatio}/${ChatColor.GOLD}$arg${ChatColor.GRAY}/${TaskTussleSystem.hardRatio}") },
                        { p -> p.sendMessage("${ChatColor.GOLD}normalRatio${ChatColor.WHITE} is currently: ${ChatColor.GRAY}${TaskTussleSystem.easyRatio}/${ChatColor.GOLD}${TaskTussleSystem.normalRatio}${ChatColor.GRAY}/${TaskTussleSystem.hardRatio}") }),
                CommandIntLeaf("tt_hardRatio", 0, null,
                        { p, arg -> TaskTussleSystem.hardRatio = arg; p.sendMessage("${ChatColor.GOLD}hardRatio${ChatColor.WHITE} is changed to: ${ChatColor.GRAY}${TaskTussleSystem.easyRatio}/${TaskTussleSystem.normalRatio}/${ChatColor.GOLD}$arg") },
                        { p -> p.sendMessage("${ChatColor.GOLD}hardRatio${ChatColor.WHITE} is currently: ${ChatColor.GRAY}${TaskTussleSystem.easyRatio}/${TaskTussleSystem.normalRatio}/${ChatColor.GOLD}${TaskTussleSystem.hardRatio}") }),
                )

        override val exampleCommand: String = "/taskTussle setting [setting] [value/empty]"
        override val helpText: String = "to view or change settings"
        override val baseTree = CommandTree("setting", (
                mutualCommands +
                BingoCommand.settings +
                ObtainTaskManager.settings
                ))
    }


    override val systemCommands: Array<ISystemCommand> = arrayOf(
            StartTree,
            StopTree,
            SettingsTree,
            DebugTree
    )
}