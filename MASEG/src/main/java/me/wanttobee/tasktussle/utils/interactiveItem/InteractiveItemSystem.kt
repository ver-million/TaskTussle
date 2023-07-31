package me.wanttobee.tasktussle.utils.interactiveItem

import me.wanttobee.tasktussle.TTPlugin
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent

object InteractiveItemSystem: Listener {
    private val plugin = TTPlugin.instance
    const val version = "v1.1 Interactive Item"
    private val hotBarItems : MutableList<InteractiveItem> = mutableListOf()


    fun addItem(inv : InteractiveItem){
        if(!hotBarItems.contains(inv)){
            hotBarItems.add(inv)
        }
    }
    fun removeItem(inv : InteractiveItem) :Boolean{
        return hotBarItems.remove(inv)
    }


    @EventHandler
    fun onHotBarClick(event: InventoryClickEvent) {
        // MASEGPlugin.instance.logger.info("CLICK")
        val player = event.whoClicked as? Player ?: return
        if (player.gameMode == GameMode.CREATIVE) return

        val item = event.currentItem ?: return
        for(hotBarItem in hotBarItems) {
            if (hotBarItem.isThisItem(item)) {
                event.isCancelled = true
                return
            }
        }
    }

    @EventHandler
    fun onHotBarDrag(event: InventoryDragEvent) {
        // MASEGPlugin.instance.logger.info("DRAG")
        val player = event.whoClicked as? Player ?: return
        if (player.gameMode == GameMode.CREATIVE) return

        val item = event.cursor ?: return
        for(hotBarItem in hotBarItems) {
            if (hotBarItem.isThisItem(item)) {
                event.isCancelled = true
                return
            }
        }
    }


    @EventHandler
    fun onHotBarDropItem(event: PlayerDropItemEvent) {
        // MASEGPlugin.instance.logger.info("DROP")
        val player = event.player

        val item = event.itemDrop.itemStack
        for(hotBarItem in hotBarItems) {
            if (hotBarItem.isThisItem(item)) {
                hotBarItem.doDropEvent(player)
                event.isCancelled = true
                return
            }
        }
    }

    @EventHandler
    fun onSwapHandItems(event: PlayerSwapHandItemsEvent) {
       // MASEGPlugin.instance.logger.info("SWAP")
        val player = event.player
        for(hotBarItem in hotBarItems){
            if(hotBarItem.isThisItem(event.mainHandItem) || hotBarItem.isThisItem((event.offHandItem))){
                hotBarItem.doSwapEvent(player)
                event.isCancelled = true
                return
            }
        }
    }

    @EventHandler
    fun onHotBarInteract(event: PlayerInteractEvent) {
        // MASEGPlugin.instance.logger.info("INTERACT")
        val player = event.player
        val action = event.action

        if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            val item = event.item ?: return
            for(hotBarItem in hotBarItems) {
                if (hotBarItem.isThisItem(item)) {
                    hotBarItem.doRightClickEvent(player)
                    event.isCancelled = true
                    return
                }
            }
        }
        if(action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK){
            val item = event.item ?: return
            for(hotBarItem in hotBarItems) {
                if (hotBarItem.isThisItem(item)) {
                    hotBarItem.doLeftClickEvent(player)
                    event.isCancelled = true
                    return
                }
            }
        }
    }

    private val playerDeathItems: MutableList<Pair<Player, InteractiveItem>> = mutableListOf()
    @EventHandler
    fun onPlayerDeathHotBarCheck(event: PlayerDeathEvent) {
        val drops = event.drops
        for(hotBarItem in hotBarItems){
            val itemStack = drops.find {item -> hotBarItem.isThisItem(item)} ?: continue
            playerDeathItems.add(Pair(event.entity,hotBarItem))
            drops.remove(itemStack)
        }
    }

    @EventHandler
    fun onPlayerRespawnHotBarCheck(event: PlayerRespawnEvent) {
        val player = event.player
        for(pair in playerDeathItems){
            if(pair.first == player){
                pair.second.giveToPlayer(pair.first)
                playerDeathItems.remove(pair)
                return
            }
        }
    }

}