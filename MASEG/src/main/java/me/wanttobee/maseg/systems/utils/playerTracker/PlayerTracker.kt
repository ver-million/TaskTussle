package me.wanttobee.maseg.systems.utils.playerTracker

import me.wanttobee.maseg.MASEGPlugin
import me.wanttobee.maseg.MASEGUtil
import me.wanttobee.maseg.systems.utils.interactiveItem.InteractiveItem
import me.wanttobee.maseg.systems.utils.interactiveItem.RefreshInteractiveItem
import me.wanttobee.maseg.systems.utils.teams.ITeamObserver
import me.wanttobee.maseg.systems.utils.teams.Team
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.CompassMeta
import org.bukkit.persistence.PersistentDataType
import kotlin.contracts.contract

class PlayerTracker(private val tracking : Team, private val refreshTime: Int,private val focusClosest: Boolean,private val saveWorldLocation: Boolean) : ITeamObserver {

    private val interactiveItems : MutableList<RefreshInteractiveItem> = mutableListOf()
    private val playerLocations : MutableMap<Player, Array<Location?>> = mutableMapOf()
    private var refreshTaskID = -1
    private var closestTaskID = -1

    private val plugin = MASEGPlugin.instance

    private val notFollowing = "${ChatColor.GRAY}"
    private val following = "${ChatColor.WHITE}"

    init{
        if(tracking.getMembers().isNotEmpty())
            this.init()
        else{
            for(p in plugin.server.onlinePlayers)
                p.sendMessage("${ChatColor.RED}while creating the tracker something went wrong, the specified team doesn't have any members")
        }
    }
    private fun init(){
        tracking.subscribe(this)
        //making the list of locations
        for(p in tracking.getMembers()){
            val worldLocationList = arrayOfNulls<Location?>( plugin.server.worlds.size )
            val worldIndex = plugin.server.worlds.indexOf(p.world)
            worldLocationList[worldIndex] = p.location
            playerLocations[p] = worldLocationList
        }
        if(focusClosest && refreshTime > 20){
            refreshTaskID = plugin.server.scheduler.scheduleSyncRepeatingTask(plugin, { refreshLocations() }, 0L, refreshTime.toLong() )
            closestTaskID = plugin.server.scheduler.scheduleSyncRepeatingTask(plugin, { refreshCompass() }, 0L, 20L  )
        }
        else refreshTaskID = plugin.server.scheduler.scheduleSyncRepeatingTask(plugin, { refreshLocations(); refreshCompass()  }, 0L, refreshTime.toLong() )
    }

    private fun refreshCompass(){
        for(iItem in interactiveItems)
            iItem.doRefresh()
    }
    private fun refreshLocations(){
        for((p,l) in playerLocations){
            val worldIndex = plugin.server.worlds.indexOf(p.world)
            l[worldIndex] = p.location
        }
    }

    fun givePlayerItem(p : Player,slot : Int = 8){
        if(focusClosest) giveClosetsItem(p,slot)
        else giveFocusTargetItem(p,slot)
    }


    private fun giveClosetsItem(p : Player,slot : Int){
        val teamMembers = tracking.getMembers()
        val memberList = List(teamMembers.size) { i -> "$notFollowing${teamMembers[i].name}" }
        val title = "${ChatColor.GREEN}Tracker:${ChatColor.GOLD} Closest"
        val realItem = MASEGUtil.itemFactory(Material.COMPASS, title,memberList )
        val realMeta = realItem.itemMeta as CompassMeta
        realMeta.isLodestoneTracked = false
        realItem.itemMeta = realMeta

        val interactiveItem = RefreshInteractiveItem()
        interactiveItem.setSlot(slot).setItem(realItem)

        interactiveItem.setRefreshEffect { meta ->
            //make compass data
            val compassMeta = meta as CompassMeta
            val members = tracking.getMembers()
            if(members.size != compassMeta.lore?.size )
                compassMeta.lore = List(members.size) { i -> "$notFollowing${members[i].name}" }

            //get the closest target
            var closestLocation : Location? = null
            var currentDistance = Double.MAX_VALUE
            val ownerWorldIndex = plugin.server.worlds.indexOf(p.world)
            if(ownerWorldIndex > playerLocations.entries.first().value.size) {
                p.sendMessage("${ChatColor.RED}Something went wrong while refreshing your tracker, a world must have been created after the game already started. No further refreshes will be made to this item")
                interactiveItem.clear()
                return@setRefreshEffect
            }
            //loop through every player that is being tracked
            for(mem in members) {
                val locations = playerLocations[mem]!!
                val targetLoc = if(saveWorldLocation) locations[ownerWorldIndex]
                    else locations[plugin.server.worlds.indexOf(mem.world)]
                targetLoc ?: continue
                if(targetLoc.world != p.world) continue
                val newDistance = p.location.distance(targetLoc)
                if(newDistance < currentDistance){
                    currentDistance = newDistance
                    closestLocation = targetLoc
                }
            }
            //update location
            val fakeLocation = p.location.clone()
            fakeLocation.world = members.first().world
            compassMeta.lodestone = closestLocation ?: fakeLocation
            //make sure the player gets the updated item
            interactiveItem.updateMeta(compassMeta)
        }

        interactiveItems.add(interactiveItem)
        interactiveItem.doRefresh()
        interactiveItem.giveToPlayer(p)
    }



    private fun giveFocusTargetItem(p : Player,slot : Int){
        val teamMembers = tracking.getMembers()
        val memberList = List(teamMembers.size) { i ->
            if(i == 0) "$following${teamMembers[i].name}"
            else "$notFollowing${teamMembers[i].name}"
        }
        val title = "${ChatColor.GREEN}Tracker:${ChatColor.GOLD} ${teamMembers.first().name}"
        val realItem = MASEGUtil.itemFactory(Material.COMPASS, title,memberList )
        val realMeta = realItem.itemMeta as CompassMeta
        realMeta.isLodestoneTracked = false
        val focusTargetKey = NamespacedKey(plugin, p.uniqueId.toString())
        realMeta.persistentDataContainer.set(focusTargetKey, PersistentDataType.INTEGER, 0)
        realItem.itemMeta = realMeta

        val interactiveItem = RefreshInteractiveItem()
        interactiveItem.setSlot(slot).setItem(realItem)

        interactiveItem.setRefreshEffect { meta ->
            //make compass data
            val compassMeta = meta as CompassMeta
            val members = tracking.getMembers()
            val realIndex = compassMeta.persistentDataContainer.get(focusTargetKey, PersistentDataType.INTEGER) ?: run {
                    p.sendMessage("${ChatColor.RED}Something went wrong while refreshing your tracker, turns out you are following no-one. No further refreshes will be made to this item")
                    interactiveItem.clear()
                    return@setRefreshEffect
                }
            val focusIndex = realIndex% members.size
            if(focusIndex != realIndex)
                compassMeta.persistentDataContainer.set(focusTargetKey, PersistentDataType.INTEGER, focusIndex)
            //update lore
            compassMeta.lore = List(members.size) { i ->
                if(i == focusIndex) "$following${members[i].name}"
                else "$notFollowing${members[i].name}"
            }
            //get the target
            val target = members[focusIndex]
            compassMeta.setDisplayName("${ChatColor.GREEN}Tracker:${ChatColor.GOLD} ${target.name}")
            val locations = playerLocations[target]!!
            val world = if(saveWorldLocation) p.world else target.world
            val worldID = plugin.server.worlds.indexOf(world)
            if(worldID > locations.size) {
                p.sendMessage("${ChatColor.RED}Something went wrong while refreshing your tracker, a world must have been created after the game already started. No further refreshes will be made to this item")
                interactiveItem.clear()
                return@setRefreshEffect
            }
            //update location
            compassMeta.lodestone = locations[worldID]
            //make sure the player gets the updated item
            interactiveItem.updateMeta(compassMeta)
        }

        interactiveItem.setRightClickEvent { clicker,meta ->
            //change to the new index
            val realIndex = meta.persistentDataContainer.get(focusTargetKey, PersistentDataType.INTEGER)?: run {
                clicker.sendMessage("${ChatColor.RED}Something went wrong while refreshing your tracker, turns out you are following no-one. No further refreshes will be made to this item")
                interactiveItem.clear()
                return@setRightClickEvent
            }
            val newIndex =( realIndex+1) % tracking.getMembers().size
            meta.persistentDataContainer.set(focusTargetKey, PersistentDataType.INTEGER, newIndex)
            //make sure the player gets the updated item
            interactiveItem.updateMeta(meta)
            interactiveItem.doRefresh()

            if(tracking.getMembers().size > 1) clicker.playSound(clicker, Sound.BLOCK_DISPENSER_DISPENSE, SoundCategory.MASTER, 0.5f, 1f )
        }

        interactiveItems.add(interactiveItem)
        interactiveItem.doRefresh()
        interactiveItem.giveToPlayer(p)
    }

    fun stopTracking(){
        for(iItem in interactiveItems)
            iItem.clear()
        interactiveItems.clear()
        if(refreshTaskID != -1)
            plugin.server.scheduler.cancelTask(refreshTaskID)
        refreshTaskID = -1
        if(closestTaskID != -1)
            plugin.server.scheduler.cancelTask(closestTaskID)
        closestTaskID = -1
        tracking.unsubscribe(this)
    }


    override fun onTeamClear() { stopTracking() }
    override fun onAddMember(member: Player) {
        val worldLocationList = arrayOfNulls<Location?>( plugin.server.worlds.size )
        val worldIndex = plugin.server.worlds.indexOf(member.world)
        worldLocationList[worldIndex] = member.location
        playerLocations[member] = worldLocationList
        for(iItem in interactiveItems)
            iItem.doRefresh()
    }
    override fun onRemoveMember(member: Player) {
        if(tracking.getMembers().isEmpty())
            stopTracking()
        else
            for(iItem in interactiveItems)
                iItem.doRefresh()
    }

    override fun onSwapMember(leave: Player, enter: Player) {
        playerLocations[enter] = playerLocations[leave]!!
        playerLocations.remove(leave)

        for(iItem in interactiveItems)
            iItem.doRefresh()
    }

    override fun toString(): String {
        var value = "Tracking: "
        for(members in tracking.getMembers()){
            value += members.name
            if(members != tracking.getMembers().last()) value += ", "
        }
        return value
    }
}