package me.wanttobee.maseg.systems.utils.randomPlayer

import me.wanttobee.maseg.MASEGPlugin
import me.wanttobee.maseg.systems.utils.teams.Team
import me.wanttobee.maseg.systems.utils.teams.TeamSystem
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.pow
import kotlin.math.cos
import kotlin.math.sin

object RandomPlayerSystem {
    const val version = "v2.0 Random Player using [${TeamSystem.version}]"
    private val plugin = MASEGPlugin.instance

    private const val pauseTicks: Int = 4
    private const val animationTicks : Int = 8
    private const val height: Float = 1f
    private val jump: (Float) -> Float = { t -> (-(t * 2f - 1f).pow(2) + height) + 1.8f }
    private const val morePlayersJump = 2



    fun choseRandomTeam(commander:Player, amount : Int, winningProcess : ((Team) -> Unit)? = null){ return choseRandomTeam(commander,amount,plugin.server.onlinePlayers,winningProcess ) }
    fun choseRandomTeam(commander:Player, amount : Int, playerPool : Collection<Player>, winningProcess : ((Team) -> Unit)? = null){
        val noDuplicateList : MutableList<Player> = mutableListOf()
        for(player in playerPool){
            if(!noDuplicateList.contains(player))
                noDuplicateList.add(player)
        }
        if(amount <= 0 ) {
            commander.sendMessage("${ChatColor.RED} something went wrong, trying to randomize 0 players")
            return
        }
        if(amount > noDuplicateList.size ){
            commander.sendMessage("${ChatColor.RED} something went wrong, trying to chose ${ChatColor.GRAY}$amount${ChatColor.RED} out of the total ${ChatColor.GRAY}${noDuplicateList.size}")
            return
        }
        recursiveRandomTeam(Team(ChatColor.WHITE),amount, noDuplicateList.random() , noDuplicateList, winningProcess, (Math.random()*6).toInt() + 2)
    }

    private fun recursiveRandomTeam(team : Team, amountLeft : Int, jumpFrom : Player, playerPool : Collection<Player>,winningProcess : ((Team) -> Unit)?, iterationsLeft : Int ){
        if(amountLeft == playerPool.size) {
            for(p in playerPool)
                defaultChoseEffect(p, winningProcess == null)
            team.addMember(playerPool)
            chosenTeam(team,winningProcess)
            return
        }
        plugin.server.onlinePlayers.forEach { op ->
            op.playSound(jumpFrom, Sound.BLOCK_AMETHYST_BLOCK_HIT, SoundCategory.MASTER, 1f, 1f)
            op.playSound(jumpFrom, Sound.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.MASTER, 1f, 1f)
            op.playSound(jumpFrom, Sound.BLOCK_BEEHIVE_DRIP, SoundCategory.MASTER, 1f, 1f)
        }
        val nextPlayer = playerPool.filter { player -> player != jumpFrom }.random()

        var currentTick = 0
        var taskID = -1

        val task =  task@ {
            val t = ((currentTick++).toFloat() / animationTicks.toFloat())
            animateStep(t, jumpFrom.location, nextPlayer.location)
            if (t < 0.999f) return@task
            if(!team.containsMember(jumpFrom)) jumpFrom.removePotionEffect(PotionEffectType.GLOWING)
            nextPlayer.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, animationTicks *20, 0))

            if(iterationsLeft <= 0){
                team.addMember(nextPlayer)
                defaultChoseEffect(nextPlayer,winningProcess==null )
                if(amountLeft - 1 == 0){
                    chosenTeam(team,winningProcess)
                }
                else{
                    plugin.server.scheduler.scheduleSyncDelayedTask(plugin,
                        {  recursiveRandomTeam(team, amountLeft-1, nextPlayer,
                            playerPool.filter { player -> player != nextPlayer },
                            winningProcess, morePlayersJump ) },
                        pauseTicks.toLong())
                }
            }
            else{
                plugin.server.scheduler.scheduleSyncDelayedTask(plugin,
                    {  recursiveRandomTeam(team, amountLeft, nextPlayer, playerPool, winningProcess, iterationsLeft-1 ) },
                    pauseTicks.toLong())
            }
            plugin.server.scheduler.cancelTask(taskID)
        }
        taskID = plugin.server.scheduler.scheduleSyncRepeatingTask(plugin, task , 0 ,1)
    }

    private fun chosenTeam(team: Team, winningProcess : ((Team) -> Unit)?){
        team.applyToMembers { member ->
            plugin.server.onlinePlayers.forEach{ player ->
                player.playSound(member, Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER, 1f, 1f)
                player.playSound(member, Sound.BLOCK_BELL_RESONATE, SoundCategory.MASTER, 1f, 1f)
                player.playSound(member, Sound.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.MASTER, 1f, 1f)
            }
        }
        if (winningProcess != null)
            winningProcess(team)
        else team.clear()
    }


    private fun defaultChoseEffect(p : Player, title: Boolean){
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