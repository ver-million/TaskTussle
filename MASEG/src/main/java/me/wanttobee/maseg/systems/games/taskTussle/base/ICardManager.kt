package me.wanttobee.maseg.systems.games.taskTussle.base

import me.wanttobee.maseg.systems.games.taskTussle.tasks.ITask
import me.wanttobee.maseg.systems.utils.teams.TeamSet
import org.bukkit.entity.Player

interface ICardManager {
    val card : ICard
    fun openCard(player : Player){ card.open(player) }

    fun setTasks(tasks : Array<ITask>) : Boolean
    fun setTeams(teams : TeamSet<ICardManager>) : Boolean

    fun onTaskDisabled(task : ITask)

}