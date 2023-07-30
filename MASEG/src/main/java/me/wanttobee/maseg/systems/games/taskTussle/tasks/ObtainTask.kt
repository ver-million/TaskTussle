package me.wanttobee.maseg.systems.games.taskTussle.tasks

import me.wanttobee.maseg.MASEGPlugin
import me.wanttobee.maseg.MASEGUtil
import me.wanttobee.maseg.systems.games.taskTussle.TaskTussleSystem
import me.wanttobee.maseg.systems.games.taskTussle.base.ITask
import me.wanttobee.maseg.systems.games.taskTussle.base.TaskIcon
import me.wanttobee.maseg.systems.games.taskTussle.base.TaskSystem
import me.wanttobee.maseg.systems.utils.teams.Team
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerPickupItemEvent

class ObtainTask(val itemToObtain : Material, associatedTeam : Team) : ITask(associatedTeam)  {
    override val icon: TaskIcon = TaskIcon(itemToObtain, MASEGUtil.getRealName(itemToObtain), listOf("${ChatColor.GRAY}obtain this item") )
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