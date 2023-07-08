package me.wanttobee.maseg.systems.utils.randomPlayer

import me.wanttobee.maseg.MASEGPlugin
import me.wanttobee.maseg.systems.utils.playerCompass.PlayerCompassSystem
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.pow
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin

object RandomPlayerSystem {
    val version = "v1.1 Random Player"
    private val plugin = MASEGPlugin.instance

    private const val pauseTicks: Int = 4
    private const val animationTicks : Int = 8
    private const val height: Float = 1f
    private val jump: (Float) -> Float = { t -> (-(t * 2f - 1f).pow(2) + height) + 1.8f }
    private const val morePlayersJump = 2

    fun doRandomPlayerProcess( iterationsLeft : Int, amountOfPlayer:Int, dontPick : Array<Player>, startAt: Player?, winningProcess : ((Array<Player>) -> Unit)? = null){
        return this.doRandomPlayerProcess(iterationsLeft,amountOfPlayer,dontPick,startAt,winningProcess, arrayOf())
    }

    private fun doRandomPlayerProcess( iterationsLeft : Int, amountOfPlayer:Int, dontPick : Array<Player>, startAt: Player?, winningProcess : ((Array<Player>) -> Unit)? = null, alreadyChosen: Array<Player>){
        if(amountOfPlayer == 0)return
        val pitch = if(iterationsLeft == 0) 0.5f else 0.5f + 7f/iterationsLeft
        val allPlayers = plugin.server.onlinePlayers.filter { player -> !dontPick.contains(player) && !alreadyChosen.contains(player) }
        var t = 0.0f
        var currentTick = 0
        var taskID = -1
        // if there are no more other players, it doest really matter because these will always be choice, might as well just skip to it
        if( allPlayers.size <= amountOfPlayer - alreadyChosen.size){
            for(p in allPlayers)
                selectPlayer(p, winningProcess == null)
            for(p in alreadyChosen)
                selectPlayer(p, winningProcess == null)

            finishSelectedPlayers(allPlayers.toTypedArray() + alreadyChosen,winningProcess)
            return
        }
        val currentRandomPlayer = startAt ?: allPlayers.elementAt(Random().nextInt(allPlayers.size))
        val modifiedList = allPlayers.filter { player -> player != currentRandomPlayer }
        val nextRandomPlayer: Player = modifiedList.elementAt(Random().nextInt(modifiedList.size))


        val task = {
            t = ((currentTick++).toFloat() / animationTicks.toFloat())
            animateStep(t, currentRandomPlayer.location, nextRandomPlayer.location)
            if (t >= 0.999f) {
                currentRandomPlayer.removePotionEffect(PotionEffectType.GLOWING)
                nextRandomPlayer.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, animationTicks *20, 0))
                for(p in alreadyChosen){
                    p.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, animationTicks *20, 0))
                }

                //if the fake iterations are not done yet
                if(iterationsLeft > 1){
                    plugin.server.scheduler.scheduleSyncDelayedTask(
                            plugin,
                            {   plugin.server.onlinePlayers.forEach { op ->
                                  op.playSound(nextRandomPlayer, Sound.BLOCK_AMETHYST_BLOCK_HIT, SoundCategory.MASTER, 1f, pitch)
                                  op.playSound(nextRandomPlayer, Sound.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.MASTER, 1f, pitch)
                                  op.playSound(nextRandomPlayer, Sound.BLOCK_BEEHIVE_DRIP, SoundCategory.MASTER, 1f, pitch)
                                }
                                doRandomPlayerProcess( (iterationsLeft - 1),amountOfPlayer, dontPick,nextRandomPlayer, winningProcess, alreadyChosen) },
                            pauseTicks.toLong()
                    )
                }
                //if the fake iterations are done
                else{
                    val newAlreadyChose = alreadyChosen + nextRandomPlayer
                    selectPlayer(nextRandomPlayer, winningProcess == null)
                    plugin.server.scheduler.scheduleSyncDelayedTask(
                            plugin,
                            {
                                if(newAlreadyChose.size >= amountOfPlayer) finishSelectedPlayers(newAlreadyChose, winningProcess)
                                else {
                                    plugin.server.onlinePlayers.forEach { op ->
                                        op.playSound(nextRandomPlayer, Sound.BLOCK_AMETHYST_BLOCK_HIT, SoundCategory.MASTER, 1f, pitch)
                                        op.playSound(nextRandomPlayer, Sound.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.MASTER, 1f, pitch)
                                        op.playSound(nextRandomPlayer, Sound.BLOCK_BEEHIVE_DRIP, SoundCategory.MASTER, 1f, pitch)
                                    }
                                    doRandomPlayerProcess(morePlayersJump, amountOfPlayer, dontPick, nextRandomPlayer, winningProcess, newAlreadyChose)
                                } },
                            pauseTicks.toLong()
                    )
                }

                plugin.server.scheduler.cancelTask(taskID)
            }
        }
        taskID = plugin.server.scheduler.scheduleSyncRepeatingTask(plugin, task , 0 ,1)
    }

    private fun selectPlayer(p : Player, title: Boolean){
        // 1. particles
        val particleCount = 100
        val particleRadius = 1.0

        val particleRunnable = object : BukkitRunnable() {
            private var step = 0
            override fun run() {
                if (step >= particleCount) {
                    this.cancel()
                    return
                }
                val angle = 2 * Math.PI * step / particleCount
                val x = particleRadius * cos(angle)
                val z = particleRadius * sin(angle)
                val particleLocation = p.location.add(x, 0.0, z)
                p.world.spawnParticle(Particle.REDSTONE, particleLocation, 0, 0.0, 0.0, 0.0, 1.0, Particle.DustOptions(Color.RED, 1.0f))
                step++
            }
        }
        particleRunnable.runTaskTimer(plugin, 0L, 1L)
        if(title){
            p.sendTitle("§6You have been chosen", "", 10, 40, 10)
            plugin.server.onlinePlayers.forEach { player ->
                player.sendMessage("§6${p.name}§f has been chosen")
            }
        }
    }

    private fun finishSelectedPlayers(players: Array<Player>, after: ((Array<Player>) -> Unit)?) {
        for(p in players){
            plugin.server.onlinePlayers.forEach{ op ->
                op.playSound(p, Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER, 1f, 1f)
                op.playSound(p, Sound.BLOCK_BELL_RESONATE, SoundCategory.MASTER, 1f, 1f)
                op.playSound(p, Sound.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.MASTER, 1f, 1f)
            }
        }
        if (after != null) {
            after(players)
        }
    }


    private fun animateStep(t: Float, start: Location, end: Location) {
        val world: World = start.world!!
        val particleType: Particle = Particle.REDSTONE // Use the appropriate particle type for a white dust particle
        val particleData: Particle.DustOptions = Particle.DustOptions(Color.WHITE, 1.5f)

        val interpolatedX: Double = start.x + (end.x - start.x) * t.toDouble()
        val interpolatedY: Double = start.y + (end.y - start.y) * t.toDouble() + height * jump(t)
        val interpolatedZ: Double = start.z + (end.z - start.z) * t.toDouble()

        val particleLocation = Location(world, interpolatedX, interpolatedY, interpolatedZ)
        world.spawnParticle(particleType, particleLocation, 4,particleData)
    }


}