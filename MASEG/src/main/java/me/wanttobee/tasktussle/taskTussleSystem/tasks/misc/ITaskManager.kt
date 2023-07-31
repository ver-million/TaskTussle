package me.wanttobee.tasktussle.taskTussleSystem.tasks.misc

import me.wanttobee.tasktussle.commands.commandTree.ICommandBranch
import me.wanttobee.tasktussle.utils.teams.Team

interface ITaskManager<T : ITask> {

    val settings : Array<ICommandBranch>
    fun generateTasks(associatedTeam : Team, amounts : Triple<Int,Int,Int>, skip: List<ITask> = emptyList() ) : Array<T>?
}