package me.wanttobee.maseg.systems.utils.teams

import me.wanttobee.maseg.MASEGUtil
import me.wanttobee.maseg.systems.utils.interactiveInventory.InteractiveInventory
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class TeamMakerInventory(private val teamAmount : Int,private val maxTeamSize : Int = 64)  : InteractiveInventory(9 + 9 * (teamAmount/9),"${ChatColor.GOLD}Team Maker") {

    private lateinit var teams : Array<Team>

    init {
        if (maxTeamSize in 1..64){
            teams = Array(teamAmount) { i -> Team(i) }
            refreshTeams()
        }

    }
    private fun refreshTeams(){
        for(index in 0 until teamAmount){
            val team = teams[index]
            val teamMembers = team.getMembers()
            var item: ItemStack
            if(teamMembers.isEmpty()){
                item = MASEGUtil.itemFactory( MASEGUtil.colorMaterial(team.getColor(), Material.WHITE_STAINED_GLASS),
                    "${ChatColor.BOLD}${team.getColor()}Team ${team.getColor().name}","${ChatColor.DARK_GRAY} Empty",1 )
            }
            else{
                var memberString = "${ChatColor.GRAY}"
                for(memberID in teamMembers.indices){
                    memberString += teamMembers[memberID].name
                    if(memberID != teamMembers.size -1)
                        memberString += ", "
                }
                item = MASEGUtil.itemFactory( MASEGUtil.colorMaterial(team.getColor(), Material.WHITE_CONCRETE),
                    "${ChatColor.BOLD}${team.getColor()}Team ${team.getColor().name}",memberString,teamMembers.size , true)
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
                refreshTeams()
            }
            this.inventory.setItem(index,item)
        }
    }

    fun getTeams() : Array<Team>{
        this.clear()
        return teams.filter { team -> team.getMembers().isNotEmpty() }.toTypedArray()
    }

}