package me.wanttobee.maseg.systems.utils.teams

import me.wanttobee.maseg.MASEGPlugin
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.Objects


class TeamSet<T>(private val defaultValue : () -> T, private val title : String = "") {
//private val entityClass : Class<T>,
    private val teams : MutableMap<Team, T> = mutableMapOf()
    private val observers : MutableList<ITeamSetObserver> = mutableListOf()
    fun subscribe(sub : ITeamSetObserver){
        if(!observers.contains(sub))
            observers.add(sub)
    }
    fun unSubscribe(sub : ITeamSetObserver){
        observers.remove(sub)
    }


    init{
        TeamSystem2.addTeamSet(this)
    }
    fun clear(){
        for(ob in observers)
            ob.onSetClear()
        TeamSystem2.removeTeamSet(this)
    }

    fun setLeaveOnQuit(value : Boolean){
        for((team,_) in teams){
            team.setLeaveTeamOnQuit(value)
        }
    }
    fun setLeaveOnDeath(value: Boolean){
        for((team,_) in teams){
            team.setLeaveTeamOnDeath(value)
        }
    }

    fun addTeam(team : Team, value : T){
        teams[team]= value
    }
    fun addTeam(team: Team) {
        return addTeam(team, defaultValue.invoke())
    }

    fun getTeam(player: Player): Team? {
        for ((team, _) in teams) {
            if (team.containsMember(player))
                return team
        }
        return null
    }

    fun getTeam(value: T): Team? {
        return teams.entries.find { it.value == value }?.key
    }

    fun getT(team: Team): T? {
        return teams[team]
    }
    fun getT(player: Player): T? {
        for ((team, value) in teams) {
            if (team.containsMember(player))
                return value
        }
        return null
    }

    fun containsTeam(team: Team): Boolean {
        return teams.containsKey(team)
    }
    fun containsPlayer(player: Player): Boolean {
        for ((team, _) in teams) {
            if (team.containsMember(player))
                return true
        }
        return false
    }

    override fun toString(): String {
        var stringBuffer = "${ChatColor.GOLD}Set${ChatColor.RESET}: $title"
        for((team,value) in teams)
            stringBuffer += "\n- $team ${ChatColor.GRAY}$value"
        return stringBuffer
    }

    fun onPlayerLeave(player : Player){
        for(team in teams.keys){
            if(team.containsMember(player) && team.leaveTeamOnQuit)
                team.removeMember(player)
        }
    }

    fun onPlayerJoin(leftPlayer : Player, newPlayer : Player){
        for(team in teams.keys){
            if(team.containsMember(leftPlayer))
                team.swapPlayer(leftPlayer, newPlayer)
        }
    }
    fun onPlayerDeath(player: Player){
        for((team,_) in teams){
            if(team.leaveTeamOnDeath && team.containsMember(player))
                team.removeMember(player)
        }
    }

    fun broadcast(message : String){
        for((team, _) in teams){
            for(member in team.getMembers())
                member.sendMessage(message)
        }
    }
    fun applyToTeams( effect: (Team,T) -> Unit ){
        for((team,T) in teams)
            effect.invoke(team,T)
    }
    fun applyToAllMembers(effect: (Player) -> Unit){
        for((team,_) in teams){
            for(member in team.getMembers())
                effect.invoke(member)
        }
    }
}