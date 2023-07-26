package me.wanttobee.maseg.systems.games.taskTussle

import me.wanttobee.maseg.MASEGPlugin
import me.wanttobee.maseg.MASEGUtil
import me.wanttobee.maseg.systems.games.taskTussle.base.ICardManager
import me.wanttobee.maseg.systems.utils.interactiveInventory.InteractiveInventorySystem
import me.wanttobee.maseg.systems.utils.interactiveItem.InteractiveItem
import me.wanttobee.maseg.systems.utils.interactiveItem.InteractiveItemSystem
import me.wanttobee.maseg.systems.utils.teams.Team
import me.wanttobee.maseg.systems.utils.teams.TeamSet
import me.wanttobee.maseg.systems.utils.teams.TeamSystem
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player

//system for starting en controlling parts of the game
object TaskTussleSystem {
    const val version = "v1.0 TaskTussle using [${TeamSystem.version}] [${InteractiveItemSystem.version}] [${InteractiveInventorySystem.version}]"

    //global values to control the game
    private val plugin = MASEGPlugin.instance
    var game : TeamSet<ICardManager>? = null
        private set
    private var clickItem : InteractiveItem = InteractiveItem().setSlot(8)
        .setItem(MASEGUtil.itemFactory(Material.PAPER, "${ChatColor.GREEN}Task Tussle Card", "${ChatColor.GRAY}right-click to open", true))
        .setRightClickEvent { player -> open(player) }


    //util settings
    var mutualTasks = 15
    var choseTeamsBeforehand = true

    //card settings
    var cardType = "a"
    var hideCard = false

    //task settings
    var obtainTask_handIn = false
    var obtainTask_easyRatio = 1
    var obtainTask_normalRatio = 3
    var obtainTask_hardRatio = 2

    //game specific settings
    private var bingo_pickupLocked = false
    var bingo_winningCondition = "aa"

    private fun open(p : Player){
       if(game == null) return
       game!!.applyToOwnT(p) { openCard(p) }
    }

    fun finishGame(team : Team){
        if(game == null) return
    }


}