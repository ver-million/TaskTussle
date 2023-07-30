package me.wanttobee.maseg.systems.games.taskTussle.bingo

import me.wanttobee.maseg.systems.games.taskTussle.base.ICard
import me.wanttobee.maseg.systems.games.taskTussle.base.ICardManager
import me.wanttobee.maseg.systems.games.taskTussle.base.ITask
import me.wanttobee.maseg.systems.games.taskTussle.base.TeamIcon
import me.wanttobee.maseg.systems.utils.teams.Team
import me.wanttobee.maseg.systems.utils.teams.TeamSet

class BingoCardInventory(associatedTeam : Team) : ICard(associatedTeam, 25,10,45,"${associatedTeam.getDisplayName()} - Bingo") {
    override val teamIcon = TeamIcon(this, associatedTeam, 25)

    override fun displayTask(tasks: Array<ITask>) : Boolean {
        if(tasks.size != 25) return false
        for(i in 0 until 25)
            tasks[i].icon.addToInventory(2 + (i%5) + 9*(i/5), this)
            //this.inventory.setItem(2 + (i%5) + 9*(i/5), tasks[i].icon.item )
        return true
    }

    override fun displayStatic() {
        for(i in 0 until 5){
            this.inventory.setItem(1 + 9*i,separator)
            this.inventory.setItem(7 + 9*i,separator)
        }
    }

    override fun displayTeams(teams: TeamSet<ICardManager>) : Boolean {
        val allTeams = teams.toPairList()
        if(allTeams.size > 10) return false

        for(index in 0 until 10 ){
            val spot = 9*(index%5) + 8*(index/5)
            if(index >= allTeams.size) inventory.setItem( spot, emptyTeamIcon )
            else {
                allTeams[index].second.card.teamIcon.addToInventory(spot,this)
            }
        }
        return true
    }
}