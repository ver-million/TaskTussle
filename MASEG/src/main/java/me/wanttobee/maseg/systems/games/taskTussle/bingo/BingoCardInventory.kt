package me.wanttobee.maseg.systems.games.taskTussle.bingo

import me.wanttobee.maseg.systems.games.taskTussle.base.ICard
import me.wanttobee.maseg.systems.games.taskTussle.base.ICardManager
import me.wanttobee.maseg.systems.games.taskTussle.tasks.ITask
import me.wanttobee.maseg.systems.utils.teams.Team
import me.wanttobee.maseg.systems.utils.teams.TeamSet

class BingoCardInventory(associatedTeam : Team) : ICard(associatedTeam, 25,10,45,"${associatedTeam.color}$associatedTeam - Bingo") {

    override fun displayTask(tasks: Array<ITask>) : Boolean {
        if(tasks.size != 25) return false
        for(i in 0 until 25)
            this.inventory.setItem(2 + (i%5) + 9*(i/5), tasks[i].displayIcon() )
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
            if(index >= allTeams.size)
                this.inventory.setItem( spot, emptyTeamIcon )
            else{
                val card = allTeams[index].second.card
                if(card == this) this.inventory.setItem( spot, this.privateTeamIcon )
                else this.inventory.setItem( spot, card.publicTeamIcon )
            }
        }
        return true
    }
}