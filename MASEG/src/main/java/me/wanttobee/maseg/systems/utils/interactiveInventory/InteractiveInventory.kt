package me.wanttobee.maseg.systems.utils.interactiveInventory

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemStack

open class InteractiveInventory(size : Int, title : String) {
    val inventory = Bukkit.createInventory(null, size, title)
    private val clickEvents : MutableMap<ItemStack, (Player) -> Unit> = mutableMapOf()

    init{ InteractiveInventorySystem.addInventory(this) }
    fun clear(){ InteractiveInventorySystem.removeInventory(this) }

    fun open(player : Player){
        player.openInventory(inventory)
    }
    fun itemClickEvent(item : ItemStack, onClick : ((Player) -> Unit)? = null){
        if(onClick != null)
            clickEvents[item] = onClick
        else
            clickEvents.remove(item)
    }
    fun moveClickEvent(from : ItemStack, to:ItemStack){
        if(clickEvents.containsKey(from))
            clickEvents[to] = clickEvents[from]!!
        clickEvents.remove(from)
    }

    fun onClick(player: Player, event : InventoryClickEvent){
        val item = event.currentItem ?: return
        val itemEvent  = clickEvents[item] ?: return
        itemEvent.invoke(player)
    }
    fun onDrag(player: Player, event : InventoryDragEvent){}

}