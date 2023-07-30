package me.wanttobee.maseg.systems.games.taskTussle

import me.wanttobee.maseg.MASEGPlugin
import me.wanttobee.maseg.MASEGUtil
import me.wanttobee.maseg.systems.games.taskTussle.base.ITaskGameSystem
import me.wanttobee.maseg.systems.games.taskTussle.base.ITask
import me.wanttobee.maseg.systems.games.taskTussle.base.TaskFactory
import me.wanttobee.maseg.systems.utils.interactiveInventory.InteractiveInventorySystem
import me.wanttobee.maseg.systems.utils.interactiveItem.InteractiveItem
import me.wanttobee.maseg.systems.utils.interactiveItem.InteractiveItemSystem
import me.wanttobee.maseg.systems.utils.teams.Team
import me.wanttobee.maseg.systems.utils.teams.TeamSystem
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

//system for starting en controlling parts of the game
object TaskTussleSystem {
    const val version = "v1.0 TaskTussle using [${TeamSystem.version}] [${InteractiveItemSystem.version}] [${InteractiveInventorySystem.version}]"

    //global values to control the game
    private val plugin = MASEGPlugin.instance
    private var gameSystem : ITaskGameSystem<*>? = null
    const val clickItemName = "Task Tussle Card"
    val clickItem : InteractiveItem = InteractiveItem().setSlot(8)
        .setItem(MASEGUtil.itemFactory(Material.PAPER,"${ChatColor.GREEN}$clickItemName" , "${ChatColor.GRAY}right-click to open", true))
        .setRightClickEvent { player -> open(player) }

    const val ignoreTeamSize = true
    var completeTasksLocked = false

    //tt settings (common settings, settings that are for every game)
    var choseTeamsBeforehand = true
    var gameTime = 60
    var hideCard = false
    var easyRatio = 13
    var normalRatio = 8
    var hardRatio = 4

    fun start(commander: Player, teamAmount : Int, game:ITaskGameSystem<*> ){
        gameSystem = game
        gameSystem!!.startGame(commander, teamAmount)
    }

    fun stop(commander: Player){
        if(gameSystem == null) {
            commander.sendMessage("${ChatColor.RED}Its hard to stop a game that doesn't exists")
            return
        }
        if(gameSystem!!.endGame())
            commander.sendMessage("${ChatColor.YELLOW}the game was already broken or something")
        gameSystem = null
        commander.sendMessage("${ChatColor.GREEN}stopped the running game")
    }

    private fun open(p : Player){
       if(gameSystem == null) {
           p.sendMessage("${ChatColor.RED}no game to open")
           return
       }
        val game = gameSystem!!.game ?: run {
            p.sendMessage("${ChatColor.RED}no game to open")
            return
        }
        game.applyToOwnT(p) { openCard(p) }
    }

    fun getTasks(associatedTeam : Team, amount: Int) : Array<ITask>?{
        return TaskFactory.createTasks(associatedTeam,amount ,easyRatio, normalRatio, hardRatio)
    }



    fun debugStatus(commander: Player){
        commander.sendMessage("${ChatColor.GOLD}GAME")
        if(gameSystem == null)
            commander.sendMessage("no game is running")
        else{
            var gameClass = gameSystem!!::class.simpleName
            if(gameClass == null) commander.sendMessage("no game is running")
            else{
                commander.sendMessage("- $gameClass")
                gameSystem!!.debugStatus(commander)
            }
        }
        commander.sendMessage(" ")
        commander.sendMessage("${ChatColor.GOLD}UTIL")
        commander.sendMessage("${ChatColor.GRAY}for the teams thing, just use ${ChatColor.WHITE}/mu teams list${ChatColor.GRAY}, wont be displayed here")
        commander.sendMessage(" ")
        InteractiveInventorySystem.debugStatus(commander)
        commander.sendMessage(" ")
    }
}