package me.wanttobee.tasktussle.commands

import me.wanttobee.tasktussle.commands.commandTree.ICommandBranch

//an interface to build your command tree from
interface ISystemCommand {

    val exampleCommand : String
    val helpText : String

    val baseTree : ICommandBranch
}



