package me.wanttobee.maseg

import me.wanttobee.maseg.systems.utils.interactiveItem.RefreshInteractiveItem
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.meta.CompassMeta
import java.util.*

object StartUpTests {

    private val plugin = MASEGPlugin.instance
    fun run(){

    }

  // private fun itemTest(){
  //     val players = plugin.server.onlinePlayers
  //     val item = RefreshInteractiveItem()
  //     .setRefreshEffect { meta ->
  //         val compassMeta = meta as CompassMeta
  //         compassMeta.setDisplayName("${ChatColor.GOLD}${UUID.randomUUID()}")
  //         compassMeta.isLodestoneTracked = false
  //         compassMeta.lodestone = players.elementAt(1).location
  //         compassMeta
  //     }.setRefreshInterval(20)
  //     item.setItem(Material.COMPASS)
  //     item.giveToPlayer(players.first())
  //     item.startRefreshingMeta()
  // }



    // private fun worldTest(){
    //     val wc = WorldCreator("New World")
    //     wc.environment(World.Environment.NORMAL)
    //     wc.type(WorldType.NORMAL)
    //     wc.createWorld()
    // }
}