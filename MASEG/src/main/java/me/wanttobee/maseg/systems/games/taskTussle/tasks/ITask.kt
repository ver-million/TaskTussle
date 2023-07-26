package me.wanttobee.maseg.systems.games.taskTussle.tasks

import me.wanttobee.maseg.systems.games.taskTussle.base.TaskIcon
import me.wanttobee.maseg.systems.games.taskTussle.base.ICardManager
import me.wanttobee.maseg.systems.utils.teams.Team
import org.bukkit.ChatColor
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.inventory.ItemStack

//the stateCode's in order are
// 0 -> active
// 1 -> Completed
// 2 -> CompletedBy ??
// 3 -> Hidden
// 4 -> Locked
// 5 -> Failed
abstract class ITask(val associatedTeam : Team) {
    protected abstract val icon : TaskIcon
    private var callBackCardManager : ICardManager? = null
    var stateCode : TaskState = TaskState.HIDDEN
        private set

    fun displayIcon() : ItemStack { return icon.item }
    fun setActive(callBackCard : ICardManager){
        stateCode = TaskState.ACTIVE
        icon.setState(stateCode)
        enable(callBackCard)
    }
    fun setCompleted(){
        stateCode = TaskState.COMPLETED
        icon.setState(stateCode)
        privateDisable()
    }
    fun setCompletedBy(teamColor : ChatColor, teamTitle : String){
        stateCode = TaskState.COMPLETED_BY
        icon.setState(stateCode,teamColor,teamTitle)
        privateDisable()
    }
    fun setHidden(){
        stateCode = TaskState.HIDDEN
        icon.setState(stateCode)
        privateDisable()
    }
    fun setLocked(){
        stateCode = TaskState.LOCKED
        icon.setState(stateCode)
        privateDisable()
    }
    fun setFailed(){
        stateCode = TaskState.FAILED
        icon.setState(stateCode)
        privateDisable()
    }

    private fun privateDisable(){
        //when it has never been enabled it also should be totally fine
        val temp = callBackCardManager
        callBackCardManager = null //.just in case you timidity want to change the callback or something, I don't know why you would, oh well,but here you go anyway
        temp?.onTaskDisabled(this)
        disable()
    }
    private fun enable(callBackCard : ICardManager){
        callBackCardManager = callBackCard
        enable()
    }
    protected abstract fun disable()
    protected abstract fun enable()

    abstract fun clone(otherTeam : Team) : ITask

    //all the different possible listeners
    open fun checkTask(){} //for the tick one
    open fun checkTask(event : PlayerPickupItemEvent): (()->Unit)? {return null}
    open fun checkTask(event : PlayerInteractEvent): (()->Unit)? {return null}
    open fun checkTask(event : BlockBreakEvent) : (()->Unit)? {return null}
    open fun checkTask(event : BlockPlaceEvent): (()->Unit)? {return null}
    open fun checkTask(event : EntityDeathEvent) : (()->Unit)? {return null}
    open fun checkTask(event : PlayerDeathEvent) : (()->Unit)? {return null}
}