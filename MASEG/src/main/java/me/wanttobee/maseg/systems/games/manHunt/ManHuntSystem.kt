package me.wanttobee.maseg.systems.games.manHunt

import me.wanttobee.maseg.MASEGPlugin
import me.wanttobee.maseg.systems.utils.playerTracker.PlayerTrackerSystem
import me.wanttobee.maseg.systems.utils.randomPlayer.RandomPlayerSystem
import me.wanttobee.maseg.systems.utils.teams.Team
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.EnderDragon
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object ManHuntSystem : Listener {
    val version = "v2.0 Manhunt using [${PlayerTrackerSystem.version}] [${RandomPlayerSystem.version}]"
    private val plugin = MASEGPlugin.instance

    private var runners : Team? = null
    private var trackerID = -1

    var compassFocusClosest = false
    var saveWorldLocation = false
    var compassEnabled = true
    var compassUpdateDuration = 15*20
    var slowDuration = 30*20

    //if you want to be the random runners will be one of the yesPlayers if you make it not null
    fun startHunt(commander: Player, runnersAmount : Int, yesPlayers : Collection<Player>? ){
        if(runners != null){
            commander.sendMessage("${ChatColor.RED}Cant start a hunt when one is already running")
            return
        }
        val randomPlayerPool = yesPlayers ?: plugin.server.onlinePlayers
        if( randomPlayerPool.size <= runnersAmount){
            commander.sendMessage("${ChatColor.RED}Cant start manhunt when there are no hunters")
            return
        }
        if( runnersAmount == 0){
            commander.sendMessage("${ChatColor.RED}Cant start manhunt when there are no runners")
            return
        }
        val duplicateCheck : MutableList<Player> = mutableListOf()
        for(p in randomPlayerPool){
            if(duplicateCheck.contains(p)){
                commander.sendMessage("${ChatColor.RED}Cant start manhunt, the given players contain a duplicate: $p")
                return
            }
            duplicateCheck.add(p)
        }
        RandomPlayerSystem.choseRandomTeam(commander, runnersAmount, randomPlayerPool) { team ->
            beginHuntFor(team)
        }
    }

    fun stopHunt(stopper : Player){
        if(runners == null){
            stopper.sendMessage("§cCant stop a hunt that hasn't started")
            return
        }
        val allPlayers = plugin.server.onlinePlayers
        allPlayers.forEach {player ->
            player.sendMessage("§cThe hunt has been stopped §4(by ${stopper.name})")
        }
        clearGame()
    }


    private fun beginHuntFor(team: Team){
        runners = team
        if(compassEnabled)
            trackerID = PlayerTrackerSystem.startTracker(runners!!, compassUpdateDuration, compassFocusClosest, saveWorldLocation)
        var names = " "
        for(member in team.getMembers()){
            names += member.name
            if(member != team.getMembers().last())
                names += ", "
        }

        plugin.server.onlinePlayers.forEach { player ->
            player.sendTitle("§c$names", "§4Will be hunted §c>:)", 10, 40, 10)
            player.sendMessage("§c$names§4 will be hunted")

            if(!team.containsMember(player)){
                player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, slowDuration, 2))
                player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, slowDuration, 1))
                if(compassEnabled)
                    PlayerTrackerSystem.giveTracker(player, trackerID)
            }
        }
    }

    private fun clearGame(){
        PlayerTrackerSystem.stopTracking(trackerID)
        trackerID = -1

        runners?.clearTeam()
        runners = null
    }

    private fun finishGame(runnerWins : Boolean){
        plugin.server.onlinePlayers.forEach { player ->
            player.gameMode = GameMode.SPECTATOR
            val winMessage = if(runnerWins) "${ChatColor.GREEN}Runners Win!" else "${ChatColor.RED}Hunters Win!"
            player.sendTitle("${ChatColor.GOLD}Game Ended", winMessage, 10, 40, 10)
            player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*10, 100))
        }
        clearGame()
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if(runners != null && runners?.containsMember(event.entity) == true){
            runners?.removeMember(event.entity)
            if(runners!!.getMembers().isEmpty())
                finishGame(false)
            else
                event.entity.gameMode = GameMode.SPECTATOR
        }
    }

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        if(runners == null) return
        val entity = event.entity
        if (entity is EnderDragon)
            finishGame(true)
    }
}