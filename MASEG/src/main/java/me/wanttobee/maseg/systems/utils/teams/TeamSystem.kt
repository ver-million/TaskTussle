package me.wanttobee.maseg.systems.utils.teams

import me.wanttobee.maseg.MASEGPlugin
import me.wanttobee.maseg.MASEGUtil
import me.wanttobee.maseg.systems.utils.interactiveItem.InteractiveItem
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent


object TeamSystem: Listener {
    private val plugin = MASEGPlugin.instance
    const val version = "v1.2 Teams"
    private val activeTeams : MutableList<Team> = mutableListOf()

    fun addTeam(team : Team){
        if(!activeTeams.contains(team))
            activeTeams.add(team)
    }
    fun removeTeam(team: Team){
        activeTeams.remove(team)
    }

    fun deleteAll(){
        for(tID in activeTeams.indices)
            activeTeams[0].clearTeam()

    }


    fun listTeams(sender : Player){
        if(activeTeams.isEmpty()){
            sender.sendMessage("§cThere are no teams to list")
            return
        }
        sender.sendMessage("${MASEGPlugin.title} §aTeams List: ")
        for(team in activeTeams)
            sender.sendMessage("§6${team}")
    }

    fun makeXTeams(amount : Int) : Array<Team>{
        val onlinePlayers = MASEGPlugin.instance.server.onlinePlayers.shuffled()
        val playerCount = onlinePlayers.size
        val teamSize = playerCount / amount
        val remainingPlayers = playerCount % amount
        var currentPlayerIndex = 0
        return  Array(amount) { i ->
            val size = teamSize + if (i < remainingPlayers) 1 else 0
            val team = Team(i)
            for (j in 0 until size) {
                team.addMember(onlinePlayers[currentPlayerIndex])
                currentPlayerIndex++
            }
            team
        }
    }

    fun teamMaker(command:Player , amount: Int, effect : (Array<Team>) -> Unit, maxTeamSize : Int = 64){
        if(amount == 1){
            command.sendMessage("${ChatColor.RED}with 1 team to chose from there isn't any choice, so it has been completed already")
            val team = Team(0)
            team.addMember(plugin.server.onlinePlayers)
            effect.invoke(arrayOf(team))
            return
        }
        if(amount <= 1){
            command.sendMessage("${ChatColor.RED} the team amount has to be a number above 1")
            return
        }
        if(maxTeamSize <= 1){
            command.sendMessage("${ChatColor.RED}it isn't really fun to play with empty teams")
            return
        }
        if(amount > 54){
            command.sendMessage("${ChatColor.RED}BRO, why do you need so many teams, due to the visualizer it is not possible to make them, you will have to let them be random generated for you")
            return
        }
        if(maxTeamSize > 64){
            command.sendMessage("${ChatColor.RED}BRO, why do you need teams so big!!, due to the visualizer it is not possible to make them, you will have to let them be random generated for you")
            return
        }

        val teamMakerInventory = TeamMakerInventory(amount,maxTeamSize)
        val teamMakerItem = InteractiveItem()
            .setSlot(8)
            .setItem(MASEGUtil.itemFactory(Material.TNT, "${ChatColor.RED}Team ${ChatColor.WHITE}Navigator ${ChatColor.RED}Tool", "${ChatColor.GRAY}Use this item to chose your team", true))
            .setRightClickEvent { player -> teamMakerInventory.open(player) }
        for(p in plugin.server.onlinePlayers) {
            teamMakerItem.giveToPlayer(p)
        }
        val teamFinisher = InteractiveItem()
            .setSlot(7)
            .setItem(MASEGUtil.itemFactory(Material.FIRE_CHARGE, "${ChatColor.GOLD}Detonate Current Teams", "${ChatColor.GRAY}Use these teams", true))
        teamFinisher.setRightClickEvent { player ->
                teamMakerItem.clear()
                val teams = teamMakerInventory.getTeams()
                effect.invoke(teams)
                teamFinisher.clear()
            }
        teamFinisher.giveToPlayer(command)
    }

    private val quitPlayers : MutableList<Player> = mutableListOf()
    @EventHandler
    fun onPlayerLeave(event : PlayerQuitEvent){
        val player = event.player
        quitPlayers.add(player)

        for(team in activeTeams){
            if(team.leaveTeamOnQuit && team.containsMember(player))
                team.removeMember(player)
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent){
        val player = event.player
        for(leftPlayer in quitPlayers){
            if(player.uniqueId == leftPlayer.uniqueId){
                for(team in activeTeams){
                    if(team.containsMember(leftPlayer))
                        team.swapPlayer(leftPlayer, player)
                }
                quitPlayers.remove(leftPlayer)
                return
            }
        }
    }
}