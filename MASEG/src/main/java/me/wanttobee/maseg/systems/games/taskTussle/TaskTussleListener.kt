package me.wanttobee.maseg.systems.games.taskTussle

import org.bukkit.event.Listener

object TaskTussleListener : Listener {

    private val tickObservers : MutableList<ITussleTask> = mutableListOf()
    private val breakBlockObservers : MutableList<ITussleTask> = mutableListOf()
    private val placeBlockObservers : MutableList<ITussleTask> = mutableListOf()
    private val killObservers : MutableList<ITussleTask> = mutableListOf()

}