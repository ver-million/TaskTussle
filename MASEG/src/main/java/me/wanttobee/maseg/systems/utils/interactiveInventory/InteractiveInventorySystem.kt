package me.wanttobee.maseg.systems.utils.interactiveInventory

import me.wanttobee.maseg.MASEGPlugin
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent

object InteractiveInventorySystem : Listener {
    val version = "v1.0 Interactive Inventory"
    private val inventories : MutableList<InteractiveInventory> = mutableListOf()

    fun addInventory(inv : InteractiveInventory){
        if(!inventories.contains(inv)){
            inventories.add(inv)
        }
    }
    fun removeInventory(inv : InteractiveInventory) :Boolean{
        return inventories.remove(inv)
    }

    @EventHandler
    fun iClick(event: InventoryClickEvent) {
        val clickedInventory = event.clickedInventory
        val player = event.whoClicked as? Player ?: return
        for(inv in inventories){
            if(inv.inventory == clickedInventory){
                inv.onClick(player, event)
                event.isCancelled = true;
                return
            }
        }
    }
    @EventHandler
    fun iDrag(event: InventoryDragEvent) {
        val clickedInventory = event.inventory
        val player = event.whoClicked as? Player ?: return
        for(inv in inventories){
            if(inv.inventory == clickedInventory){
                inv.onDrag(player, event)
                event.isCancelled = true;
                return
            }
        }
    }
}