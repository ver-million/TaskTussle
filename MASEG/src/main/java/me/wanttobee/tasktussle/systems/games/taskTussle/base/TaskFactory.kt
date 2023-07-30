package me.wanttobee.tasktussle.systems.games.taskTussle.base

import me.wanttobee.tasktussle.systems.games.taskTussle.tasks.ObtainTaskManager
import me.wanttobee.tasktussle.systems.utils.teams.Team

object TaskFactory {

    private val taskManagers = listOf(
        ObtainTaskManager,
        )




    fun <T:ITask, U:ITask> combineTasks(bases: Array<T>, extra : Array<U>) : Array<ITask>{
        if(bases.isEmpty() &&  extra.isEmpty()) return emptyArray()
        val copyTeam = if(bases.isNotEmpty()) bases[0].associatedTeam else extra[0].associatedTeam
        return Array(bases.size + extra.size) {i ->
            if(i < bases.size) bases[i]
            else extra[i].clone(copyTeam)
        }
    }
    private fun calculateAmount(amount: Int, easyRatio:Int, normalRatio:Int, hardRatio:Int, seperatedBy : Int) : Triple<Int,Int,Int> {
        val realAmount = amount/seperatedBy
        val totalRatio = easyRatio + normalRatio + hardRatio
        val easyAmount = (easyRatio.toDouble() / totalRatio.toDouble() * realAmount).toInt()
        val hardAmount = (hardRatio.toDouble() / totalRatio.toDouble() * realAmount).toInt()
        val normalAmount = realAmount - easyAmount - hardAmount
        return Triple(easyAmount,normalAmount,hardAmount)
    }


    fun createTasks(associatedTeam : Team, amount : Int, easyRatio : Int, normalRatio : Int, hardRatio: Int, skip: List<ITask> = emptyList() ) : Array<ITask>?{
        val totalRatio = easyRatio + normalRatio + hardRatio
        if(totalRatio == 0) return null
        val enabledManagersList = taskManagers.shuffled().filter{ manager -> manager.enabled }
        if(enabledManagersList.isEmpty()) return null

        var generationAMount = amount
        var tasks : Array<ITask> = emptyArray()
        for(i in enabledManagersList.indices) {
            val generationRatio = calculateAmount( generationAMount, easyRatio, normalRatio, hardRatio, enabledManagersList.size - i)
            val partA = tasks
            val partB = enabledManagersList[i].generateTasks(associatedTeam, generationRatio, skip)
            if(partB != null){
                generationAMount -= partB.size
                tasks = combineTasks(partA, partB)
            }
        }

        return tasks
    }

}