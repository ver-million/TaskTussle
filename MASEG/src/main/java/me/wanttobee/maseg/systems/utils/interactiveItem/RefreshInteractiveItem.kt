package me.wanttobee.maseg.systems.utils.interactiveItem

import me.wanttobee.maseg.MASEGPlugin
import org.bukkit.inventory.meta.ItemMeta

open class RefreshInteractiveItem : InteractiveItem(){

    private lateinit var refreshMetaEffect : (ItemMeta) -> Unit
    private var refreshInterval = -1
    private var refreshID = -1
    fun setRefreshEffect(effect : (ItemMeta) -> Unit) : RefreshInteractiveItem{
        refreshMetaEffect = effect
        return this
    }
    fun setRefreshInterval(intervalTicks : Int) : RefreshInteractiveItem{
        refreshInterval = intervalTicks
        return this
    }

    fun startRefreshingMeta() : Boolean{
        if(refreshID != -1 || refreshInterval == -1) return false
        refreshID = MASEGPlugin.instance.server.scheduler.scheduleSyncRepeatingTask(MASEGPlugin.instance, { doRefresh() },0L, refreshInterval.toLong())
        return true
    }
    fun stopRefreshingMeta() : Boolean{
        if(refreshID != -1) {
            MASEGPlugin.instance.server.scheduler.cancelTask(refreshID)
            return true
        }
        return false
    }

    fun doRefresh(){
        if(!::refreshMetaEffect.isInitialized) return
        val newMeta = refreshMetaEffect.invoke(this.item.itemMeta!!)
        //for(p in MASEGPlugin.instance.server.onlinePlayers){
        //    if(isThisItem( p.inventory.getItem(slot)))
        //        p.inventory.getItem(slot)!!.itemMeta = newMeta
        //}
        //item.itemMeta = newMeta
    }

    override fun clear() {
        if(refreshID != -1) MASEGPlugin.instance.server.scheduler.cancelTask(refreshID)
        super.clear()
    }

}