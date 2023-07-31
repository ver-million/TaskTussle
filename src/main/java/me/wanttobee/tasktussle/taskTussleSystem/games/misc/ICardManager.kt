package me.wanttobee.tasktussle.taskTussleSystem.games.misc

import me.wanttobee.tasktussle.taskTussleSystem.tasks.misc.ITask
import me.wanttobee.tasktussle.utils.teams.TeamSet
import org.bukkit.entity.Player

interface ICardManager {
    val card : ICard
    fun openCard(player : Player){ card.open(player) }

    fun setTasks(tasks : Array<ITask>) : Boolean
    fun <T:ICardManager> setTeams(teams : TeamSet<T>) : Boolean

    fun onTaskDisabled(task : ITask)

}