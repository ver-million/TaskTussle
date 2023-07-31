package me.wanttobee.tasktussle.taskTussleSystem.games.misc

import me.wanttobee.tasktussle.TTUtil
import me.wanttobee.tasktussle.taskTussleSystem.tasks.misc.ITask
import me.wanttobee.tasktussle.utils.interactiveInventory.InteractiveInventory
import me.wanttobee.tasktussle.utils.teams.Team
import me.wanttobee.tasktussle.utils.teams.TeamSet
import org.bukkit.ChatColor
import org.bukkit.Material

abstract class ICard(private val associatedTeam : Team,val taskAmount : Int,val teamAmount : Int, inventorySlotAmount : Int, inventoryTitle: String) : InteractiveInventory(inventorySlotAmount,inventoryTitle) {
    abstract val teamIcon : TeamIcon

    init{
        displayStatic()
    }

    abstract fun displayTask(tasks: Array<ITask>) : Boolean
    abstract fun displayStatic()
    abstract fun <T:ICardManager> displayTeams(teams : TeamSet<T>) : Boolean //its own teamIcon shouldn't use the publicTeamIcon, it's for outside viewers


    companion object{
        val emptyTeamIcon = TTUtil.itemFactory(Material.GRAY_STAINED_GLASS, "${ChatColor.RESET}${ChatColor.GRAY}Empty Card", null)
    }
}