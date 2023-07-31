package me.wanttobee.tasktussle.taskTussleSystem.tasks.misc

import me.wanttobee.tasktussle.taskTussleSystem.tasks.obtainTask.ObtainTaskManager
import me.wanttobee.tasktussle.utils.teams.Team

object TaskFactory {

    private val taskManagers = listOf(
        ObtainTaskManager,
        )




    fun <T: ITask, U: ITask> combineTasks(first: Array<T>, second : Array<U>, team: Team) : Array<ITask>{
        return Array(first.size + second.size) { i ->
            if(i < first.size) first[i].clone(team)
            else second[i-first.size].clone(team)
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
                tasks = combineTasks(partA, partB, associatedTeam)
            }
        }

        return tasks
    }

}