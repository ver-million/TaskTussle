package me.wanttobee.maseg.systems.games.taskTussle

interface ITussleTask {
    val icon : TussleIcon

    fun isComplete()

    open fun activate(){
        icon.setAvailable()
    }
}