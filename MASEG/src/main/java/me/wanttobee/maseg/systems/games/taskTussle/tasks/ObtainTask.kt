package me.wanttobee.maseg.systems.games.taskTussle.tasks

import me.wanttobee.maseg.MASEGUtil
import me.wanttobee.maseg.systems.games.taskTussle.base.TaskIcon
import me.wanttobee.maseg.systems.games.taskTussle.base.TaskSystem
import me.wanttobee.maseg.systems.utils.teams.Team
import org.bukkit.Material
import org.bukkit.event.player.PlayerPickupItemEvent

class ObtainTask(val itemToObtain : Material, associatedTeam : Team) : ITask(associatedTeam)  {
    override val icon: TaskIcon = TaskIcon(itemToObtain, "obtain ${MASEGUtil.getRealName(itemToObtain)}", listOf("you haven't obtained this item yet") )

    override fun enable() {
        TaskSystem.playerPickupItemObservers.add(this)
    }
    override fun disable() {
        TaskSystem.playerPickupItemObservers.remove(this)
    }

   override fun checkTask(event: PlayerPickupItemEvent) : (() -> Unit)? {
       if(associatedTeam.containsMember(event.player)){
           if(event.item.itemStack.type == itemToObtain)
               return {this.setCompleted()}
       }
       return null
   }

    override fun clone(otherTeam : Team): ObtainTask {
        return ObtainTask(itemToObtain,otherTeam)
    }

}