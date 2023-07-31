package me.wanttobee.tasktussle.utils.teams

import me.wanttobee.tasktussle.TTUtil
import me.wanttobee.tasktussle.utils.interactiveInventory.InteractiveInventory
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class TeamMakerInventory(private val teamAmount : Int,private val maxTeamSize : Int = 64)  : InteractiveInventory(9 + 9 * (teamAmount/9),"${ChatColor.GOLD}Team Maker") {

    private lateinit var teams : Array<Team>

    init {
        if (maxTeamSize in 1..64){
            teams = Array(teamAmount) { i -> Team(i+1) }
            refreshTeamVisualizer()
        }

    }
    //refreshes the inventories items when any changes happen (when someone clicks on another team to move for example)
    //this way the items stay up to date
    private fun refreshTeamVisualizer(){
        for(index in 0 until teamAmount){
            val team = teams[index]
            val teamMembers = team.getMembers()
            var item: ItemStack
            if(teamMembers.isEmpty()){
                item = TTUtil.itemFactory( TTUtil.colorMaterial(team.color, Material.WHITE_STAINED_GLASS),
                    team.getDisplayName(),"${ChatColor.DARK_GRAY} Empty",1 )
            }
            else{
                var memberString = "${ChatColor.GRAY}"
                for(memberID in teamMembers.indices){
                    memberString += teamMembers[memberID].name
                    if(memberID != teamMembers.size -1)
                        memberString += ", "
                }
                item = TTUtil.itemFactory( TTUtil.colorMaterial(team.color, Material.WHITE_CONCRETE),
                        team.getDisplayName(),memberString,teamMembers.size , true)
            }

            this.itemClickEvent(item) { player ->
                val thisTeam = teams[index]
                if(thisTeam.containsMember(player))
                    thisTeam.removeMember(player)
                else {
                    for(t in teams)
                        t.removeMember(player)
                    thisTeam.addMember(player)
                }
                refreshTeamVisualizer()
            }
            this.inventory.setItem(index,item)
        }
    }

    fun exportTeams() : Array<Team>{
        this.clear()
        return teams.filter { team -> team.getMembers().isNotEmpty() }.toTypedArray()
    }

}