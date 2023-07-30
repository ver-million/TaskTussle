package me.wanttobee.maseg.systems.games.taskTussle.base

import me.wanttobee.maseg.MASEGPlugin
import me.wanttobee.maseg.systems.games.taskTussle.TaskTussleSystem
import me.wanttobee.maseg.systems.utils.teams.TeamSystem
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import java.io.File

//system for checking if sustain task are completed yes or no
//this is so ugly written, but I just gave up
object TaskSystem : Listener {
    const val version = "v1.0 Task using [${TeamSystem.version}]"
    val taskFolder = File(MASEGPlugin.instance.dataFolder, File.separator + "Tasks")
    //global values to control the game
    private val plugin = MASEGPlugin.instance
    val tickObservers : MutableList<ITask> = mutableListOf()
    val playerInteractObservers : MutableList<ITask> = mutableListOf()
    val playerPickupItemObservers : MutableList<ITask> = mutableListOf()
    val blockBreakObservers : MutableList<ITask> = mutableListOf()
    val blockPlaceObservers : MutableList<ITask> = mutableListOf()
    val entityDeathObservers : MutableList<ITask> = mutableListOf()
    val playerDeathObservers : MutableList<ITask> = mutableListOf()
    val inventoryClickObservers : MutableList<ITask> = mutableListOf()


    @EventHandler
    fun taskSystemEventHandler(event: PlayerInteractEvent) {
        if(TaskTussleSystem.completeTasksLocked) return
        val completed : MutableList<()->Unit> = mutableListOf()
        for(task in playerInteractObservers) {
            val complete = task.checkTask(event) ?: continue
            completed.add(complete)
        }
        for(com in completed) com.invoke()
    }
    @EventHandler
    fun taskSystemEventHandler(event: PlayerPickupItemEvent) {
        if(TaskTussleSystem.completeTasksLocked) return
        val completed : MutableList<()->Unit> = mutableListOf()
        for(task in playerPickupItemObservers) {
            val complete = task.checkTask(event) ?: continue
            completed.add(complete)
        }
        for(com in completed) com.invoke()
    }
    @EventHandler
    fun taskSystemEventHandler(event: BlockBreakEvent) {
        if(TaskTussleSystem.completeTasksLocked) return
        val completed : MutableList<()->Unit> = mutableListOf()
        for(task in blockBreakObservers) {
            val complete = task.checkTask(event) ?: continue
            completed.add(complete)
        }
        for(com in completed) com.invoke()
    }
    @EventHandler
    fun taskSystemEventHandler(event: BlockPlaceEvent) {
        val completed : MutableList<()->Unit> = mutableListOf()
        for(task in blockPlaceObservers) {
            val complete = task.checkTask(event) ?: continue
            completed.add(complete)
        }
        for(com in completed) com.invoke()
    }
    @EventHandler
    fun taskSystemEventHandler(event: EntityDeathEvent) {
        if(TaskTussleSystem.completeTasksLocked) return
        val completed : MutableList<()->Unit> = mutableListOf()
        for(task in entityDeathObservers) {
            val complete = task.checkTask(event) ?: continue
            completed.add(complete)
        }
        for(com in completed) com.invoke()
    }
    @EventHandler
    fun taskSystemEventHandler(event: PlayerDeathEvent) {
        if(TaskTussleSystem.completeTasksLocked) return
        val completed : MutableList<()->Unit> = mutableListOf()
        for(task in playerDeathObservers) {
            val complete = task.checkTask(event) ?: continue
            completed.add(complete)
        }
        for(com in completed) com.invoke()
    }

    @EventHandler
    fun taskSystemEventHandler(event: InventoryClickEvent){
        if(TaskTussleSystem.completeTasksLocked) return
        val completed : MutableList<()->Unit> = mutableListOf()
        for(task in inventoryClickObservers) {
            val complete = task.checkTask(event) ?: continue
            completed.add(complete)
        }
        for(com in completed) com.invoke()
    }
}