package me.wanttobee.maseg.systems.games.taskTussle.bingo

import me.wanttobee.maseg.MASEGUtil
import me.wanttobee.maseg.systems.games.taskTussle.TaskTussleSystem
import me.wanttobee.maseg.systems.games.taskTussle.base.ICard
import me.wanttobee.maseg.systems.games.taskTussle.base.ICardManager
import me.wanttobee.maseg.systems.games.taskTussle.base.ITask
import me.wanttobee.maseg.systems.games.taskTussle.tasks.ObtainTask
import me.wanttobee.maseg.systems.games.taskTussle.base.TaskState
import me.wanttobee.maseg.systems.utils.teams.Team
import me.wanttobee.maseg.systems.utils.teams.TeamSet
import org.bukkit.ChatColor

class BingoCardManager(private val associatedTeam : Team) : ICardManager {
    override val card: ICard = BingoCardInventory(associatedTeam)
    private lateinit var taskSet : Array<ITask>

    override fun setTasks(tasks: Array<ITask>): Boolean {
        taskSet = tasks
        for (task in taskSet)
            task.setActive(this)
        return card.displayTask(taskSet)
    }

    override fun setTeams(teams: TeamSet<ICardManager>): Boolean {
        return card.displayTeams(teams)
    }

    override fun onTaskDisabled(task: ITask) {
        if(task.stateCode != TaskState.COMPLETED || task !is ObtainTask){
            associatedTeam.applyToMembers { p -> p.sendMessage("${ChatColor.RED} a wrong disableCode (${task.stateCode}) has been given in your card") }
            return
        }
        val obtainTask = task as ObtainTask
        if(TaskTussleSystem.hideCard){
            BingoGameSystem.game?.applyToTeams{ team,cm ->
                if(cm == this)
                    team.applyToMembers { p -> p.sendMessage("\"${associatedTeam.getDisplayName()}${ChatColor.RESET} got a task ${ChatColor.DARK_GRAY}(${MASEGUtil.getRealName(obtainTask.itemToObtain)})") }
                else
                    team.applyToMembers { p -> p.sendMessage("\"${associatedTeam.getDisplayName()}${ChatColor.RESET} got a task")}
            }
        }
        else{
            BingoGameSystem.game?.applyToAllMembers { p ->  p.sendMessage("${associatedTeam.getDisplayName()}${ChatColor.RESET} got task: ${ChatColor.GOLD}${MASEGUtil.getRealName(obtainTask.itemToObtain)}")  }
        }
        card.teamIcon.setAmount(getCompletedAmount())
        BingoGameSystem.cardCallback(this)
    }

    fun getCompletedAmount() : Int {
        var value = 0
        for(t in taskSet)
            if(t.stateCode == TaskState.COMPLETED) value++
        return value
    }
    //triple<Horizontal, Vertical, Diagonal>
    fun getCompletedLines() : Triple<Int,Int,Int> {
        var horizontal = 0
        var vertical = 0
        var diagonal = 0

        var diagonalBool1 = true
        var diagonalBool2 = true
        for(i in 0 until 5){
            if( taskSet[0+i*5].stateCode == TaskState.COMPLETED &&
                taskSet[1+i*5].stateCode == TaskState.COMPLETED &&
                taskSet[2+i*5].stateCode == TaskState.COMPLETED &&
                taskSet[3+i*5].stateCode == TaskState.COMPLETED &&
                taskSet[4+i*5].stateCode == TaskState.COMPLETED )
                horizontal++

            if( taskSet[i+0*5].stateCode == TaskState.COMPLETED &&
                taskSet[i+1*5].stateCode == TaskState.COMPLETED &&
                taskSet[i+2*5].stateCode == TaskState.COMPLETED &&
                taskSet[i+3*5].stateCode == TaskState.COMPLETED &&
                taskSet[i+4*5].stateCode == TaskState.COMPLETED)
                vertical++

            if(taskSet[i + i*5].stateCode != TaskState.COMPLETED) diagonalBool1 = false
            if(taskSet[(4-i) + i*5].stateCode != TaskState.COMPLETED) diagonalBool2 = false
        }
        if(diagonalBool1) diagonal++
        if(diagonalBool2) diagonal++
        //if( taskSet[0 + 0*5].stateCode == TaskState.COMPLETED &&
        //    taskSet[1 + 1*5].stateCode == TaskState.COMPLETED &&
        //    taskSet[2 + 2*5].stateCode == TaskState.COMPLETED &&
        //    taskSet[3 + 3*5].stateCode == TaskState.COMPLETED &&
        //    taskSet[4 + 4*5].stateCode == TaskState.COMPLETED)
        //    diagonal++

        //if( taskSet[4 + 0*5].stateCode == TaskState.COMPLETED &&
        //    taskSet[3 + 1*5].stateCode == TaskState.COMPLETED &&
        //    taskSet[2 + 2*5].stateCode == TaskState.COMPLETED &&
        //    taskSet[1 + 3*5].stateCode == TaskState.COMPLETED &&
        //    taskSet[0 + 4*5].stateCode == TaskState.COMPLETED)
        //    diagonal++

        return Triple(horizontal,vertical,diagonal)
    }
}