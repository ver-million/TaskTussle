package me.wanttobee.tasktussle.taskTussleSystem.games.bingo

import me.wanttobee.tasktussle.taskTussleSystem.games.misc.ICard
import me.wanttobee.tasktussle.taskTussleSystem.games.misc.ICardManager
import me.wanttobee.tasktussle.taskTussleSystem.tasks.misc.ITask
import me.wanttobee.tasktussle.taskTussleSystem.games.misc.TeamIcon
import me.wanttobee.tasktussle.utils.teams.Team
import me.wanttobee.tasktussle.utils.teams.TeamSet

class BingoCardInventory15(associatedTeam : Team) : ICard(associatedTeam, 25,15,45,"${associatedTeam.getDisplayName()} - Bingo") {
    override val teamIcon = TeamIcon(this, associatedTeam, 25)

    override fun displayTask(tasks: Array<ITask>) : Boolean {
        if(tasks.size != 25) return false
        for(i in 0 until 25)
            tasks[i].icon.addToInventory(4 + (i%5) + 9*(i/5), this)
            //this.inventory.setItem(2 + (i%5) + 9*(i/5), tasks[i].icon.item )
        return true
    }

    override fun displayStatic() {
        for(i in 0 until 5){
            this.inventory.setItem(3 + 9*i,separator)
        }
    }

    override fun <T : ICardManager> displayTeams(teams: TeamSet<T>): Boolean {
        val allTeams = teams.toPairList()
        if(allTeams.size > 15) return false

        for(index in 0 until 15 ){
            val spot = 9*(index%5) + 1*(index/5)
            if(index >= allTeams.size) inventory.setItem( spot, emptyTeamIcon )
            else {
                allTeams[index].second.card.teamIcon.addToInventory(spot,this)
            }
        }
        return true
    }
}