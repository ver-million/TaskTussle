package me.wanttobee.tasktussle.utils.interactiveItem

import me.wanttobee.tasktussle.TTPlugin
import me.wanttobee.tasktussle.TTUtil
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

open class InteractiveItem {

    protected lateinit var item : ItemStack
    private lateinit var rightClickEvent : (Player) -> Unit
    private lateinit var rightClickMetaEvent : (Player, ItemMeta) -> Unit
    private lateinit var leftClickEvent : (Player) -> Unit
    private lateinit var dropEvent : (Player) -> Unit
    private lateinit var swapEvent : (Player) -> Unit
    private var consumable = false
    var slot = 0
        private set

    init{
        InteractiveItemSystem.addItem(this)
    }
    open fun clear(){
        InteractiveItemSystem.removeItem(this)
        removeFromEveryone()
    }

    fun removeFromEveryone(){
        for(player in TTPlugin.instance.server.onlinePlayers){
            if(isThisItem(player.inventory.getItem(slot)))
                player.inventory.setItem(slot, ItemStack(Material.AIR))
        }
    }

    fun getCoolItem() : ItemStack?{
        return if (!::item.isInitialized) null
        else item.clone()
    }
    fun setItem(material : Material) : InteractiveItem { return setItem(ItemStack(material)) }
    fun setItem(i : ItemStack): InteractiveItem {
        item = i;
        return this }

    fun setRightClickEvent(e : (Player) -> Unit) : InteractiveItem {
        rightClickEvent = e
        return this
    }
    fun setRightClickEvent(e : (Player, ItemMeta) -> Unit) : InteractiveItem {
        rightClickMetaEvent = e
        return this
    }
    fun setLeftClickEvent(e : (Player) -> Unit) : InteractiveItem {
        leftClickEvent = e
        return this
    }
    fun setDropEvent(e : (Player) -> Unit): InteractiveItem {
        dropEvent = e
        return this
    }
    fun setSwapEvent(e : (Player) -> Unit): InteractiveItem {
        swapEvent = e
        return this
    }
    fun setSlot(s : Int) : InteractiveItem {
        slot = s
        return this
    }
    fun setConsumeOnUse( value : Boolean ) : InteractiveItem {
        consumable = value
        return this
    }

    //the hotBar is from 0 to 8
    fun giveToPlayer(p : Player){
        if (!::item.isInitialized) return
        p.inventory.setItem(slot, item)
    }

    fun doRightClickEvent(p : Player){
        if(::rightClickMetaEvent.isInitialized){
            rightClickMetaEvent.invoke(p,item.itemMeta!!)
            return
        }
        if (!::rightClickEvent.isInitialized) return
        rightClickEvent.invoke(p)
        if(consumable) decreaseAmount()
    }
    fun doLeftClickEvent(p : Player){
        if (!::leftClickEvent.isInitialized) return
        leftClickEvent.invoke(p)
        if(consumable) decreaseAmount()
    }
    fun doDropEvent(p : Player){
        if (!::dropEvent.isInitialized) return
        dropEvent.invoke(p)
    }
    fun doSwapEvent(p :Player){
        if (!::swapEvent.isInitialized) return
        swapEvent.invoke(p)
    }


    fun isThisItem(i : ItemStack?): Boolean{
        if (!::item.isInitialized) return false
        if(i == null) return false
        val metaI = i.itemMeta ?: return false
        val iID = metaI.persistentDataContainer.get(TTUtil.IDKey, PersistentDataType.INTEGER) ?: return false
        val itemID = item.itemMeta!!.persistentDataContainer.get(TTUtil.IDKey, PersistentDataType.INTEGER) ?: return false
        return iID == itemID
    }


    private fun setAmount(newAmount: Int){
        if (!::item.isInitialized) return
        if(newAmount <= 0) return clear()
        for(p in TTPlugin.instance.server.onlinePlayers){
            if(isThisItem( p.inventory.getItem(slot)))
                p.inventory.getItem(slot)!!.amount = newAmount
        }
        item.amount = newAmount
    }

    private fun decreaseAmount(){
        if (!::item.isInitialized) return
        val old = item.amount
        setAmount(old - 1)
    }

    fun updateMeta(newMeta : ItemMeta){
        for(p in TTPlugin.instance.server.onlinePlayers){
            if(isThisItem( p.inventory.getItem(slot)))
                p.inventory.getItem(slot)!!.itemMeta = newMeta
        }
        item.itemMeta = newMeta
    }




}