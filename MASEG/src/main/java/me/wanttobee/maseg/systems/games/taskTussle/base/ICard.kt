package me.wanttobee.maseg.systems.games.taskTussle.base

import me.wanttobee.maseg.MASEGUtil
import me.wanttobee.maseg.systems.utils.interactiveInventory.InteractiveInventory
import me.wanttobee.maseg.systems.utils.teams.Team
import me.wanttobee.maseg.systems.utils.teams.TeamSet
import org.bukkit.ChatColor
import org.bukkit.Material

abstract class ICard(private val associatedTeam : Team,val taskAmount : Int,val teamAmount : Int, inventorySlotAmount : Int, inventoryTitle: String) : InteractiveInventory(inventorySlotAmount,inventoryTitle) {
    abstract val teamIcon : TeamIcon

    init{
        displayStatic()
    }

    abstract fun displayTask(tasks: Array<ITask>) : Boolean
    abstract fun displayStatic()
    abstract fun displayTeams(teams : TeamSet<ICardManager>) : Boolean //its own teamIcon shouldn't use the publicTeamIcon, it's for outside viewers


    companion object{
        val emptyTeamIcon = MASEGUtil.itemFactory(Material.GRAY_STAINED_GLASS, "${ChatColor.RESET}${ChatColor.GRAY}Empty Card", null)
    }
}