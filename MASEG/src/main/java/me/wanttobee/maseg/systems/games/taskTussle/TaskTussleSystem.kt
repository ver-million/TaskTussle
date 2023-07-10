package me.wanttobee.maseg.systems.games.taskTussle

import me.wanttobee.maseg.MASEGPlugin

import me.wanttobee.maseg.systems.utils.interactiveInventory.InteractiveInventorySystem
import me.wanttobee.maseg.systems.utils.interactiveItem.InteractiveItemSystem
import me.wanttobee.maseg.systems.utils.teams.Team

object TaskTussleSystem {
    const val version = "v1.0 Task Tussle using [${InteractiveItemSystem.version}] [${InteractiveInventorySystem.version}]"

    private val plugin = MASEGPlugin.instance
    private var tussleGames : Array<Array<ITussleTask>>? = null
}