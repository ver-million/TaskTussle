package me.wanttobee.tasktussle.taskTussleSystem.games.misc

import me.wanttobee.tasktussle.TTPlugin
import me.wanttobee.tasktussle.taskTussleSystem.TaskTussleSystem
import me.wanttobee.tasktussle.utils.teams.Team
import me.wanttobee.tasktussle.utils.teams.TeamSet
import me.wanttobee.tasktussle.utils.teams.TeamSystem
import org.bukkit.ChatColor
import org.bukkit.entity.Player

interface ITaskGameSystem <T : ICardManager> {
    var game : TeamSet<T>?
    val defaultValue  : ((Team) -> T)
    val teamRange : IntRange

    fun startGame(commander: Player, teamAmount : Int) : Boolean{
        if(teamAmount !in teamRange){
            commander.sendMessage("${ChatColor.RED}team amount invalid, this game can have from ${teamRange.first} up to ${teamRange.last} teams total")
            return false
        }
        if(game != null)  {
            commander.sendMessage("${ChatColor.RED}there is already a game running")
            return false
        }
        val onlinePlayers = TTPlugin.instance.server.onlinePlayers.shuffled()
        if(teamAmount > onlinePlayers.size && !TaskTussleSystem.ignoreTeamSize) {
            commander.sendMessage("${ChatColor.RED}there are not enough players online to make${ChatColor.GRAY } $teamAmount ${ChatColor.RED}teams")
            return false
        }
        if(TaskTussleSystem.choseTeamsBeforehand){
            TeamSystem.teamMaker(commander,defaultValue,teamAmount, "Bingo") { set ->
                game = set
                set.applyToTeams{_, cardManager -> cardManager.card.teamIcon.setClickable(!TaskTussleSystem.hideCard) }
                set.applyToAllMembers { player -> TaskTussleSystem.clickItem.giveToPlayer(player) }
                startGame(commander,set) }
        }
        else {
            game = TeamSystem.makeXTeams(teamAmount, "Bingo", defaultValue)
            game!!.applyToTeams{_, cardManager -> cardManager.card.teamIcon.setClickable(!TaskTussleSystem.hideCard) }
            game!!.applyToAllMembers { player -> TaskTussleSystem.clickItem.giveToPlayer(player) }
            startGame(commander, game!!)
        }
        return true
    }

    fun startGame(commander: Player, teams: TeamSet<T>)
    fun finishGame(winningTeam : Team)

    open fun endGame() : Boolean{
        if(game == null) return false
        game!!.applyToTeams { _, t -> t.card.clear() }
        game!!.clear()
        game = null
        return true
    }

    fun cardCallback(cardManager : T)

    open fun debugStatus(commander: Player) {}
}