package me.wanttobee.maseg.systems.utils.interactiveInventory

import me.wanttobee.maseg.MASEGUtil
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemStack

open class InteractiveInventory(size : Int,private val title : String) {
    protected val separator = MASEGUtil.itemFactory(Material.BLACK_STAINED_GLASS_PANE, " ", null)
    val inventory = Bukkit.createInventory(null, size, title)
    private val clickEvents : MutableMap<ItemStack, (Player) -> Unit> = mutableMapOf()

    init{ InteractiveInventorySystem.addInventory(this) }
    fun closeViewers(){
        for (viewerID in inventory.viewers.indices) {
            if (inventory.viewers.size > viewerID
                && inventory.viewers[viewerID] is Player
                && inventory.viewers[viewerID].openInventory.topInventory == inventory) {
                inventory.viewers[viewerID].closeInventory()
            }
        }
    }
    fun clear(){
        closeViewers()
        InteractiveInventorySystem.removeInventory(this)
    }

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

    fun debugStatus(commander : Player){
        commander.sendMessage("${ChatColor.AQUA}IInv - $title ${ChatColor.AQUA}clickEvents:")
        for((stack, _) in clickEvents)
             commander.sendMessage("${ChatColor.WHITE} - ${ChatColor.AQUA}${stack.itemMeta?.displayName}")
    }

}