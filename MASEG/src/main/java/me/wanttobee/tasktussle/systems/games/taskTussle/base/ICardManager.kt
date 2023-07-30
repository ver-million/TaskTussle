package me.wanttobee.tasktussle.systems.games.taskTussle.base

import me.wanttobee.tasktussle.systems.utils.teams.TeamSet
import org.bukkit.entity.Player

interface ICardManager {
    val card : ICard
    fun openCard(player : Player){ card.open(player) }

    fun setTasks(tasks : Array<ITask>) : Boolean
    fun setTeams(teams : TeamSet<ICardManager>) : Boolean

    fun onTaskDisabled(task : ITask)

}