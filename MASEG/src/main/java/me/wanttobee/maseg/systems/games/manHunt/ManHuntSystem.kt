package me.wanttobee.maseg.systems.games.manHunt

import me.wanttobee.maseg.MASEGPlugin
import me.wanttobee.maseg.systems.utils.playerCompass.PlayerCompassSystem
import me.wanttobee.maseg.systems.utils.randomPlayer.RandomPlayerSystem
import org.bukkit.GameMode
import org.bukkit.entity.EnderDragon
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

object ManHuntSystem : Listener {
    val version = "v1.0 Manhunt using [${PlayerCompassSystem.version}] [${RandomPlayerSystem.version}]"
    private val plugin = MASEGPlugin.instance

    private var runners : Array<Player>? = null
    private var playerCompassID = -1

    var compassFocusClosest = false;
    var saveWorldLocation = false;
    var compassEnabled = true;
    var compassUpdateDuration = 15*20
    var slowDuration = 30*20;
    var randomisingJumps = 5


    fun startHunt(commander: Player, amount : Int, yesPlayers : List<Player>, noPlayers : List<Player> ){
        if(playerCompassID != -1){
            commander.sendMessage("§cCant start a hunt when one is already running")
            return
        }
        val allPlayers = plugin.server.onlinePlayers
        if( allPlayers.size <= amount){
            commander.sendMessage("§cCant start manhunt when there are no hunters")
            return
        }
        if( amount == 0){
            commander.sendMessage("§cCant start manhunt when there are no runners")
            return
        }
        if(yesPlayers.size > amount){
            commander.sendMessage("§cCant start manhunt, the given amount doesn't match selected players")
            return
        }
        val check : MutableList<Player> = mutableListOf()
        for(p in yesPlayers){
            if(check.contains(p)){
                commander.sendMessage("§cCant start manhunt, the given players contain a duplicate: $p")
                return
            }
            check.add(p)
        }
        if(yesPlayers.size == amount){
            beginHuntFor(yesPlayers.toTypedArray())
            return
        }
        RandomPlayerSystem.doRandomPlayerProcess( randomisingJumps ,amount - yesPlayers.size, noPlayers.toTypedArray() ,null) { players ->
            beginHuntFor(players+yesPlayers.toTypedArray())
        }
    }

    fun stopHunt(stopper : Player){
        if(playerCompassID == -1){
            stopper.sendMessage("§cCant stop a hunt that hasn't started")
            return
        }
        val allPlayers = plugin.server.onlinePlayers
        allPlayers.forEach {player ->
            player.sendMessage("§cThe hunt has been stopped §4(by ${stopper.name})")
        }
        clearGame()
    }


    private fun beginHuntFor(p : Array<Player>){
        runners = p
        playerCompassID = PlayerCompassSystem.startCompass(compassUpdateDuration,compassFocusClosest,saveWorldLocation, p)
        var names = p[0].name
        for(i in 1 until p.size){
            names += ", ${p[i].name}"
        }
        plugin.server.onlinePlayers.forEach { player ->
            player.sendTitle("§c$names", "§4Will be hunted >:)", 10, 40, 10)
            player.sendMessage("§c$names§4 will be hunted")

            if(!p.contains(player)){
                player.addPotionEffect(PotionEffect(PotionEffectType.SLOW, slowDuration, 2))
                player.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, slowDuration, 1))
                if(compassEnabled) PlayerCompassSystem.giveCompassFor(player,playerCompassID)
            }
        }
    }

    fun getCompass(giveTo :Player){
        if(playerCompassID == -1){
            giveTo.sendMessage("§cCant give a compass if there is no game")
            return
        }
        if(!compassEnabled){
            giveTo.sendMessage("§cCant give a compass if they aren't enabled from the beginning")
            return
        }
        PlayerCompassSystem.giveCompassFor(giveTo,playerCompassID)
    }


    private fun clearGame(){
        runners = null
        PlayerCompassSystem.stopCompass(playerCompassID)
        playerCompassID = -1
    }

    private fun finishGame(runnerWins : Boolean){
        plugin.server.onlinePlayers.forEach { player ->
            player.gameMode = GameMode.SPECTATOR
            if(runners!!.contains(player)){
                if(runnerWins) player.sendTitle("§aGood Job", "§2Skill issue", 10, 40, 10)
                else player.sendTitle("§cYou Failed", "§4good luck next time", 10, 40, 10)
            }
            else{
                if(!runnerWins) player.sendTitle("§aGood Job", "§2Skill issue", 10, 40, 10)
                else  player.sendTitle("§cYou Failed", "§4good luck next time", 10, 40, 10)
            }
            player.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*10, 100))
        }
        clearGame()
    }

    private fun runnerDeath(p : Player){
        if(playerCompassID == -1) return
        if(runners == null) return
        if(!runners!!.contains(p)) return

        val list : MutableList<Player> = mutableListOf()
        for(r in runners!!){
            if(r!=p)list.add(r)
        }
        if(list.isEmpty()){
            finishGame(false)
        }
        else{
            p.gameMode = GameMode.SPECTATOR
            runners = list.toTypedArray()
            PlayerCompassSystem.removePlayerFromCompass(p, playerCompassID)
        }
    }


    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        if(runners == null) return
        if(runners!!.contains(event.entity)) runnerDeath(event.entity)
    }

    @EventHandler
    fun onRespawn(event : PlayerRespawnEvent){
        if(runners != null) getCompass(event.player)
    }

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        if(runners == null) return
        val entity = event.entity
        if (entity is EnderDragon)
            finishGame(true)
    }
}