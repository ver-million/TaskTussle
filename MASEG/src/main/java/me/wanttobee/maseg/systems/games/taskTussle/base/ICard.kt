package me.wanttobee.maseg.systems.games.taskTussle.base

import me.wanttobee.maseg.MASEGUtil
import me.wanttobee.maseg.systems.games.taskTussle.tasks.ITask
import me.wanttobee.maseg.systems.utils.interactiveInventory.InteractiveInventory
import me.wanttobee.maseg.systems.utils.teams.Team
import me.wanttobee.maseg.systems.utils.teams.TeamSet
import org.bukkit.ChatColor
import org.bukkit.Material

abstract class ICard(private val associatedTeam : Team,val taskAmount : Int,val teamAmount : Int, inventorySlotAmount : Int, inventoryTitle: String) : InteractiveInventory(inventorySlotAmount,inventoryTitle) {
    val publicTeamIcon = MASEGUtil.itemFactory(
        MASEGUtil.colorMaterial(associatedTeam.color, Material.WHITE_STAINED_GLASS_PANE), "${associatedTeam.color}$associatedTeam", null)
    protected val privateTeamIcon = MASEGUtil.itemFactory(
        MASEGUtil.colorMaterial(associatedTeam.color, Material.WHITE_CONCRETE), "${associatedTeam.color}This Card", null, true)

    init{
        updateTeamIcon()
        displayStatic()
    }
    abstract fun displayTask(tasks: Array<ITask>) : Boolean
    abstract fun displayStatic()
    abstract fun displayTeams(teams : TeamSet<ICardManager>) : Boolean// its own teamIcon shouldn't use the publicTeamIcon, it's for outside viewers

    fun setIconAmount(amount : Int){
        val amountText = "${ChatColor.GRAY}$amount/$taskAmount"
        publicTeamIcon.amount = amount
        privateTeamIcon.amount = amount

        val publicMeta = publicTeamIcon.itemMeta!!
        val publicLore = publicMeta.lore!!
        publicLore[publicLore.size - 1] = amountText
        publicMeta.lore = publicLore
        publicTeamIcon.itemMeta = publicMeta

        val privateMeta = privateTeamIcon.itemMeta!!
        val privateLore = privateMeta.lore!!
        privateLore[privateLore.size - 1] = amountText
        privateMeta.lore = privateLore
        privateTeamIcon.itemMeta = privateMeta
    }
    fun setIconClickable(value : Boolean) {
        if(value){
            publicTeamIcon.type = MASEGUtil.colorMaterial(associatedTeam.color, Material.WHITE_CONCRETE)
            this.itemClickEvent(publicTeamIcon) { player -> this.open(player) }
        } else {
            publicTeamIcon.type = MASEGUtil.colorMaterial(associatedTeam.color, Material.WHITE_STAINED_GLASS_PANE)
            this.itemClickEvent(publicTeamIcon, null)
        }
    }
    fun updateTeamIcon(){
        var memberString = "${ChatColor.GRAY}"
        val teamMembers = associatedTeam.getMembers()
        for(memberID in teamMembers.indices){
            memberString += teamMembers[memberID].name
            if(memberID != teamMembers.size -1)
                memberString += ", "
        }
        val amountText = "${ChatColor.GRAY}0/$taskAmount"
        val publicMeta = publicTeamIcon.itemMeta!!
        publicMeta.lore = listOf(memberString,amountText )
        publicTeamIcon.itemMeta = publicMeta

        val privateMeta = privateTeamIcon.itemMeta!!
        privateMeta.lore = listOf(memberString,amountText)
        privateTeamIcon.itemMeta = privateMeta
    }

    companion object{
        val emptyTeamIcon = MASEGUtil.itemFactory(Material.GRAY_STAINED_GLASS, "${ChatColor.RESET}${ChatColor.GRAY}Empty Card", null)
    }
}