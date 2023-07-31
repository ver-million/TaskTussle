package me.wanttobee.tasktussle.taskTussleSystem.tasks.obtainTask

import me.wanttobee.tasktussle.TTUtil
import me.wanttobee.tasktussle.taskTussleSystem.TaskTussleSystem
import me.wanttobee.tasktussle.taskTussleSystem.tasks.misc.TaskIcon
import me.wanttobee.tasktussle.taskTussleSystem.tasks.misc.TaskSystem
import me.wanttobee.tasktussle.taskTussleSystem.tasks.misc.ITask
import me.wanttobee.tasktussle.utils.teams.Team
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerPickupItemEvent

class ObtainTask(val itemToObtain : Material, associatedTeam : Team) : ITask(associatedTeam)  {
    override val icon: TaskIcon = TaskIcon(itemToObtain, TTUtil.getRealName(itemToObtain),"Obtain Item", {"0/1"} ,
        if(TTUtil.getSubTitle(itemToObtain) == null) listOf("obtain this item") else listOf("obtain this item","${ChatColor.GRAY}${TTUtil.getSubTitle(itemToObtain)}") )

    private var handIn = false
    override fun enable() {
        if(!ObtainTaskManager.handInItem) TaskSystem.playerPickupItemObservers.add(this)
        TaskSystem.inventoryClickObservers.add(this)
        handIn = ObtainTaskManager.handInItem
    }
    override fun disable() {
        TaskSystem.playerPickupItemObservers.remove(this)
        TaskSystem.inventoryClickObservers.remove(this)
    }

   override fun checkTask(event: PlayerPickupItemEvent) : (() -> Unit)? {
       if(handIn) return null
       if(associatedTeam.containsMember(event.player)){
           if(event.item.itemStack.type == itemToObtain)
               return {this.setCompleted()}
       }
       return null
   }

    override fun checkTask(event: InventoryClickEvent): (() -> Unit)? {
        val player = event.whoClicked as? Player ?: return null
        val cursorItem = event.cursor ?: return null
        val cardItem = event.currentItem ?: return null
        if(TaskTussleSystem.clickItem.isThisItem(cardItem)
                && cursorItem.type == itemToObtain
                && associatedTeam.containsMember(player) ){
            return {
                this.setCompleted()
                if(handIn) cursorItem.amount  = cursorItem.amount -1
            }
        }
        return null
    }

    override fun clone(otherTeam : Team): ObtainTask {
        return ObtainTask(itemToObtain,otherTeam)
    }


}