package me.wanttobee.maseg.systems.games.taskTussle.base

import me.wanttobee.maseg.commands.commandTree.ICommandBranch
import me.wanttobee.maseg.systems.games.taskTussle.tasks.ObtainTask
import me.wanttobee.maseg.systems.utils.teams.Team

interface ITaskManager<T : ITask> {

    val settings : Array<ICommandBranch>
    fun generateTasks(associatedTeam : Team, amounts : Triple<Int,Int,Int>, skip: List<ITask> = emptyList() ) : Array<T>?
}