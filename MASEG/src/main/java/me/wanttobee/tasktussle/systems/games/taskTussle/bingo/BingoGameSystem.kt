package me.wanttobee.tasktussle.systems.games.taskTussle.bingo

import me.wanttobee.tasktussle.TTPlugin
import me.wanttobee.tasktussle.systems.games.taskTussle.TaskTussleSystem
import me.wanttobee.tasktussle.systems.games.taskTussle.base.ICardManager
import me.wanttobee.tasktussle.systems.games.taskTussle.base.ITaskGameSystem
import me.wanttobee.tasktussle.systems.games.taskTussle.tasks.ObtainTaskManager
import me.wanttobee.tasktussle.systems.utils.teams.Team
import me.wanttobee.tasktussle.systems.utils.teams.TeamSet
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.Player

object BingoGameSystem : ITaskGameSystem<BingoCardManager> {
    override var game: TeamSet<BingoCardManager>? = null
    override val defaultValue: (Team) -> BingoCardManager = {t -> BingoCardManager(t)}
    override val teamRange: IntRange = 1..10
    val possibleConditions = arrayOf("1_line","2_lines","3_lines","horizontal_line","vertical_line","diagonal_line", "full_card")

    //settings
    var mutualItems = 15
    var winningCondition = "1_line"

    override fun startGame(teams: TeamSet<BingoCardManager>) {

        TaskTussleSystem.completeTasksLocked = false
        val color = ChatColor.GREEN
        val itemName = "${ChatColor.GOLD}${TaskTussleSystem.clickItemName}$color"
        teams.applyToAllMembers { player ->
            player.sendMessage("${color}Click with the $itemName in your hand to see your teams progress.")
            if(ObtainTaskManager.handInItem)
                player.sendMessage("${color}To submit an item, go in your inventory, drag the item you want to submit, and click with this item on the $itemName.")
            else
                player.sendMessage("${color}To submit an item to the card, you will need to pick it up, or drag it on to your $itemName.")
            player.sendMessage("${color}The goal is set to: ${ChatColor.GOLD}$winningCondition")
        }
        //val tasks = TaskTussleSystem.getTasks(25)
        teams.applyToTeams{ team, cardManager ->
            //MASEGPlugin.instance.logger.info("${team.getDisplayName()} - 1") //----------------------------------------------------------------------------------------
            cardManager.setTeams(teams as TeamSet<ICardManager>)
            //MASEGPlugin.instance.logger.info("${team.getDisplayName()} - 2") //----------------------------------------------------------------------------------------
            cardManager.setTasks(TaskTussleSystem.getTasks(team,25)!!)
            //MASEGPlugin.instance.logger.info("${team.getDisplayName()} - 3") //----------------------------------------------------------------------------------------
            team.applyToMembers { member -> cardManager.openCard(member) }
            //MASEGPlugin.instance.logger.info("${team.getDisplayName()} - 4") //----------------------------------------------------------------------------------------
        }
    }

    override fun finishGame(winningTeam: Team) {
        TaskTussleSystem.completeTasksLocked = true

        if(TaskTussleSystem.hideCard){
            game!!.applyToTeams { _, cardManager ->
                cardManager.card.teamIcon.setClickable(true)
            }
        }
        game!!.applyToAllMembers  { p ->
            game!!.getT(winningTeam)?.openCard(p)
            p.playSound(p.location, Sound.BLOCK_END_PORTAL_SPAWN, SoundCategory.MASTER, 0.2f, 1f)
            p.playSound(p.location, Sound.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, SoundCategory.MASTER, 0.9f, 2f)
            p.playSound(p.location, Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER, 1f, 1f)
        }

    }

    override fun cardCallback(cardManager: BingoCardManager) {
        val completed = cardManager.getCompletedLines()
        if(game == null) return
        val sum = completed.first + completed.second + completed.third
        TTPlugin.instance.logger.info("$sum - ${completed.first}/${completed.second}/${completed.third} - $winningCondition ")
        val finished = when(winningCondition){
            "horizontal_line" -> completed.first >= 1
            "vertical_line" -> completed.second >= 1
            "diagonal_line" -> completed.third >= 1
            "1_line" -> sum >= 1
            "2_lines" -> sum >= 2
            "3_lines" -> sum >= 3
            "full_card" -> completed.first == 5
            else -> false
        }
        if(finished) finishGame(game!!.getTeam(cardManager)!!)
    }

    override fun debugStatus(commander: Player) {
        if(game == null) commander.sendMessage("null")
        else{
            val pairs = game!!.toPairList()
            for(p in pairs)
                commander.sendMessage(" - ${p.first.getDisplayName()}")
        }

    }
}