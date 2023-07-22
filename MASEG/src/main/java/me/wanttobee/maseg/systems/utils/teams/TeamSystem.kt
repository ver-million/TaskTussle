package me.wanttobee.maseg.systems.utils.teams

import me.wanttobee.maseg.MASEGPlugin
import me.wanttobee.maseg.MASEGUtil
import me.wanttobee.maseg.systems.utils.interactiveItem.InteractiveItem
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

object TeamSystem: Listener {
    private val plugin = MASEGPlugin.instance
    const val version = "v2.0 Teams"
    private val activeTeamSets : MutableList<TeamSet<*>> = mutableListOf()

    fun addTeamSet(teamSet : TeamSet<*>){ activeTeamSets.add(teamSet) }
    fun removeTeamSet(teamSet : TeamSet<*>){ activeTeamSets.remove(teamSet) }

    fun clearAll(){
        for(amount in activeTeamSets.indices)
            activeTeamSets[0].clear()
    }
    fun listTeams(sender : Player){
        if(activeTeamSets.isEmpty()){
            sender.sendMessage("§cThere are no teams to list")
            return
        }
        sender.sendMessage("${MASEGPlugin.title} §aTeams List: ")
        for(set in activeTeamSets)
            sender.sendMessage("§6$set")
    }

    fun <T> makeXTeams(setSize : Int, defaultValues : (Team) -> T) : TeamSet<T> {return makeXTeams(setSize, "Unknown Set", null, defaultValues)}
    fun <T> makeXTeams(setSize : Int, title : String , defaultValues : (Team) -> T) : TeamSet<T>{ return makeXTeams(setSize,title, null, defaultValues )}
    fun <T> makeXTeams(setSize : Int, title : String , preGeneratedValues : Array<T>?, defaultValues : (Team) -> T ) : TeamSet<T>{
        val onlinePlayers = MASEGPlugin.instance.server.onlinePlayers.shuffled()
        val playerCount = onlinePlayers.size
        val teamSize = playerCount / setSize
        val remainingPlayers = playerCount % setSize
        var currentPlayerIndex = 0
        val set = TeamSet<T>(defaultValues,title)
        for(i in 0 until setSize){
            val size = teamSize + if (i < remainingPlayers) 1 else 0
            val team = Team(i)
            for (j in 0 until size) {
                team.addMember(onlinePlayers[currentPlayerIndex])
                currentPlayerIndex++
            }
            MASEGPlugin.instance.logger.info("MAKE 1-$i")
            if(preGeneratedValues != null && preGeneratedValues.size > i)
                set.addTeam(team, preGeneratedValues[i])
            else
                set.addTeam(team)
        }
        return set
    }

    fun <T> teamMaker(sender:Player, defaultValues : (Team) -> T, setSize: Int, title:String, preGeneratedValues : Array<T>?, effect: (TeamSet<T>) -> Unit) {return teamMaker(sender,defaultValues,setSize,title,64,preGeneratedValues,effect)}
    fun <T> teamMaker(sender:Player, defaultValues : (Team) -> T, setSize: Int, title:String, effect:(TeamSet<T>) -> Unit) {return teamMaker(sender,defaultValues, setSize,title,64,null,effect)}
    fun <T> teamMaker(sender:Player, defaultValues : (Team) -> T, setSize: Int, effect: (TeamSet<T>) -> Unit){return teamMaker(sender,defaultValues, setSize, "Unknown Set", 64,null, effect)}
    fun <T> teamMaker(sender:Player, defaultValues : (Team) -> T, setSize: Int, title : String, maxTeamSize : Int, preGeneratedValues : Array<T>?, effect : (TeamSet<T>) -> Unit){
        if(setSize == 1){
            sender.sendMessage("${ChatColor.RED}with 1 team to chose from there isn't any choice, so it has been completed already")
            val team = Team(0)
            team.addMember(plugin.server.onlinePlayers)
            val set = TeamSet<T>(defaultValues,title)
            if(preGeneratedValues != null &&  preGeneratedValues.isNotEmpty())
                set.addTeam(team, preGeneratedValues[0])
            else
                set.addTeam(team)
            effect.invoke(set)
            return
        }
        if(setSize <= 1){
            sender.sendMessage("${ChatColor.RED}the team amount has to be a number above 1")
            return
        }
        if(maxTeamSize < 1){
            sender.sendMessage("${ChatColor.RED}it isn't really fun to play with empty teams, teamSize has to be 1 or above")
            return
        }
        if(setSize > 54){
            sender.sendMessage("${ChatColor.RED}due to minecraft inventory limit its not possible to create a visualizer for this, you will have to let them be random generated for you (but what do you need all these teams for anyway)")
            return
        }
        if(maxTeamSize > 64){
            sender.sendMessage("${ChatColor.RED}due to minecraft stack limit its not possible to create a visualizer for this, you will have to let them be random generated for you (but what do you need such big teams anyway)")
            return
        }

       val teamMakerInventory = TeamMakerInventory(setSize,maxTeamSize)
       val teamMakerItem = InteractiveItem()
           .setSlot(8)
           .setItem(MASEGUtil.itemFactory(Material.TNT, "${ChatColor.RED}Team ${ChatColor.WHITE}Navigator ${ChatColor.RED}Tool", "${ChatColor.GRAY}Use this item to chose your team", true))
           .setRightClickEvent { player -> teamMakerInventory.open(player) }
       for(p in MASEGPlugin.instance.server.onlinePlayers) {
           teamMakerItem.giveToPlayer(p)
       }
       val teamFinisher = InteractiveItem()
           .setSlot(7)
           .setItem(MASEGUtil.itemFactory(Material.FIRE_CHARGE, "${ChatColor.GOLD}Detonate Current Teams", "${ChatColor.GRAY}Use these teams", true))
       teamFinisher.setRightClickEvent { player ->
           teamMakerItem.clear()
           val teams = teamMakerInventory.exportTeams()
           val set = TeamSet(defaultValues,title)
           for(teamIndex in teams.indices){
               if(preGeneratedValues != null && preGeneratedValues.size > teamIndex )
                   set.addTeam(teams[teamIndex], preGeneratedValues[teamIndex])
               else set.addTeam(teams[teamIndex])
           }
           effect.invoke(set)
           teamFinisher.clear()
       }
       teamFinisher.giveToPlayer(sender)
    }




    private val quitPlayers : MutableList<Player> = mutableListOf()
    @EventHandler
    fun onPlayerLeave(event : PlayerQuitEvent){
        val player = event.player
        quitPlayers.add(player)
        for(set in activeTeamSets)
            set.onPlayerLeave(player)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent){
        val player = event.player
        for(leftPlayer in quitPlayers){
            if(player.uniqueId == leftPlayer.uniqueId){
                for(set in activeTeamSets){
                    if(set.containsPlayer(leftPlayer))
                        set.onPlayerJoin(leftPlayer, player)
                }
                quitPlayers.remove(leftPlayer)
                return
            }
        }
    }

    @EventHandler
    fun onPlayerDeath(event : PlayerDeathEvent){
        val player = event.entity
        for(set in activeTeamSets)
            set.onPlayerDeath(player)
    }
}