package me.wanttobee.maseg.systems.utils.playerCompass

import me.wanttobee.maseg.MASEGPlugin
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.SoundCategory
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.CompassMeta
import java.util.*

object PlayerCompassSystem: Listener {
    val version = "v1.0 Player Compass"
    private val plugin = MASEGPlugin.instance

    val playerCompasses : MutableMap<Int, PlayerCompassData> = mutableMapOf()
    private var compassID = 0

    fun printListTo(sender : Player){
        if(playerCompasses.isEmpty()){
            sender.sendMessage("§cThere are no player compasses to list")
            return
        }
        sender.sendMessage("§aPlayer Compasses List: ")
        for((id, data) in playerCompasses){
            var playerList = "§a("
            for(i in 0 until data.tracking.keys.size){
                if(i != 0) playerList += ", "
                playerList += data.tracking.keys.elementAt(i).name
            }
            playerList += ")"
            val closest = "§d"+(if(data.itemFocusClosest) "closest" else (if(data.saveWorldLocation)"worldLocation" else "trueLocation"))
            sender.sendMessage("§6$id: $closest $playerList ")
        }
    }

    fun removePlayerFromCompass(p: Player, id: Int){
        if(!playerCompasses.containsKey(id)) return
        playerCompasses[id]!!.tracking.remove(p)
    }

    fun addPlayerToCompass(p: Player, id: Int){
        if(!playerCompasses.containsKey(id)) return
        playerCompasses[id]!!.tracking[p] = Array(plugin.server.worlds.size){ _ -> null}
    }

    fun startCompass(refreshTime:Int,focusClosest:Boolean, saveWorldLocation:Boolean, targets: Array<Player>) : Int{
        val map : MutableMap<Player,Array<Location?>> = mutableMapOf()
        for(p in targets){
            if(!map.containsKey(p))
                map[p] = Array(plugin.server.worlds.size){ _ -> null}
        }
        val newCompassData = PlayerCompassData(map,saveWorldLocation,focusClosest,"§8${UUID.randomUUID()}", -1)
        val loopID =  plugin.server.scheduler.scheduleSyncRepeatingTask(plugin,
            {   saveNewLocations(newCompassData)
                updateCompasses(newCompassData, refreshTime >= 20*180) },
            0L, refreshTime.toLong() )

        newCompassData.loopID = loopID
        playerCompasses[compassID] = newCompassData
        return compassID++
    }

    fun stopCompass(id: Int) : Boolean{
        val data = playerCompasses[id] ?: return false
        plugin.server.scheduler.cancelTask(data.loopID)

        val players = plugin.server.onlinePlayers
        players.forEach { player ->
            val inventory = player.inventory
            inventory.forEach itemLoop@ { item ->
                if (item != null && item.type == Material.COMPASS) {
                    val compassMeta = item.itemMeta as CompassMeta? ?: return@itemLoop
                    val compassLore = compassMeta.lore ?: return@itemLoop
                    if (compassLore.contains(data.itemID)) {
                        inventory.removeItem(item) // Remove the compass from the player's inventory
                    }
                }
            }
        }
        playerCompasses.remove(id)
        return true
    }
    fun giveCompassFor(player: Player, id: Int) : Boolean{
        val data = playerCompasses[id] ?: return false
        player.inventory.addItem(createCompass(player,data))
        return true
    }

    private fun saveNewLocations(compassData : PlayerCompassData){
        if(compassData.saveWorldLocation){
            for((player, locationList) in compassData.tracking ){
                val locIndex = plugin.server.worlds.indexOf(player.world)
                if(locationList.size > locIndex ){
                    locationList[locIndex] = player.location
                }
                else{
                    plugin.logger.info("TRYING TO UPDATE LOCATIONS, A WORLD HAS BEEN CREATED WHILE A PLAYER COMPASS TRACKER WAS RUNNING")
                    plugin.logger.info("PROBLEM : ${player.name} - ${player.world}")
                }

            }
        }
    }
    private fun updateCompasses(compassData : PlayerCompassData, sound : Boolean){
        plugin.server.onlinePlayers.forEach { player ->
            player.inventory.forEach itemLoop@ { item ->
                if(item != null && item.type == Material.COMPASS){
                    val compassMeta = (item.itemMeta ?: return) as CompassMeta
                    val compassLore = compassMeta.lore ?: return
                    if(compassLore.contains(compassData.itemID)){
                        //from here we know that the item is one of them
                        compassMeta.isLodestoneTracked = false
                        if(compassData.itemFocusClosest) {
                            var distance = Double.MAX_VALUE
                            compassMeta.lodestone = compassData.tracking.keys.first().location //so the compass doesnt get stuck when non of them are in the same world
                            for(p in compassData.tracking.keys){
                                if(p.world == player.world){
                                    val newDistance = player.location.distance(p.location)
                                    if(newDistance < distance){
                                        compassMeta.lodestone = p.location
                                        distance =newDistance
                                    }
                                }
                            }
                        }
                        else{
                            val playerIndex = compassLore.last().toIntOrNull() ?: run {
                                player.sendMessage("§cCompassUpdateError: cant load player index")
                                return@itemLoop
                            }
                            compassMeta.lodestone = getPlayerLocation(playerIndex % compassData.tracking.keys.size,compassData,player)
                        }
                        item.itemMeta = compassMeta
                        if(sound){
                            player.playSound(player, Sound.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.MASTER, 0.3f, 1f)
                            player.playSound(player, Sound.BLOCK_BELL_RESONATE, SoundCategory.MASTER, 1f, 1f)
                        }
                    }
                }
            }
        }
    }

    private fun createCompass(createdFor: Player, compassData : PlayerCompassData ) : ItemStack {
        val compass = ItemStack(Material.COMPASS)
        val compassMeta = compass.itemMeta
        if(compassData.itemFocusClosest){
            compassMeta?.setDisplayName("§aTracking:§6 closest")
            compassMeta?.lore = listOf(compassData.itemID)//"§8$accuracy"
        }
        else{
            val firstPlayer : Player = compassData.tracking.keys.first()
            compassMeta?.setDisplayName("§aTracking:§6 ${firstPlayer.name}")
            compassMeta?.lore = listOf(compassData.itemID,"0")//"§8$accuracy"
        }
        compass.itemMeta = compassMeta
        return compass
    }

    private fun getPlayerLocation(playerIndex: Int, compassData:PlayerCompassData, inHandOf: Player) : Location{
        val focusPlayer = compassData.tracking.keys.elementAt(playerIndex)
        val focusLocation = focusPlayer.location
        //if the savedWorldLocation is turned on, take the location from this list, from the world he is in
        if(compassData.saveWorldLocation){
            val locIndex = plugin.server.worlds.indexOf(inHandOf.world)
            val locationList = compassData.tracking[focusPlayer] ?: run {
                inHandOf.sendMessage("§cCompassUpdateError: player has no world save list?!?")
                return focusLocation
            }
            if(locationList[locIndex] != null)
                return locationList[locIndex]!!
        }
        return focusLocation
    }

    private fun onRightClick(player: Player, item: ItemStack, compassData: PlayerCompassData ){
        if(compassData.itemFocusClosest) return
        val compassMeta = item.itemMeta as CompassMeta
        val compassLore = compassMeta.lore!!
        val currentIndex = compassLore.last().toIntOrNull() ?: return
        val newIndex = (currentIndex+1) % compassData.tracking.size
        compassLore[compassLore.size-1] = "$newIndex"
        compassMeta.setDisplayName("§aTracking:§6 ${compassData.tracking.keys.elementAt(newIndex).name}")
        compassMeta.isLodestoneTracked = false
        compassMeta.lodestone = getPlayerLocation(newIndex,compassData,player)
        compassMeta.lore = compassLore
        item.itemMeta = compassMeta
        if(compassData.tracking.size>1) player.playSound(player,Sound.BLOCK_DISPENSER_DISPENSE,SoundCategory.MASTER, 0.5f, 1f )
    }

    @EventHandler
    fun checkForRightClick(event: PlayerInteractEvent) {
        val player = event.player
        val item = event.item ?: return // Check if the player is holding an item
        if (event.action.name.contains("RIGHT") && item.type == Material.COMPASS) {
            val compassMeta = item.itemMeta as? CompassMeta ?: return
            val compassLore = compassMeta.lore ?: return
            for((key,compassData) in playerCompasses.entries){
                if (compassLore.contains(compassData.itemID))
                    onRightClick(player, item, compassData)
            }
        }
    }

}