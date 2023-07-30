package me.wanttobee.tasktussle

object StartUpTests {

    private val plugin = TTPlugin.instance
    fun run(){
        //itemTest()
    }

    //private fun itemTest(){
    //    val players = plugin.server.onlinePlayers
    //    val inv = InteractiveInventory(9, "aaa")
    //    val item = MASEGUtil.itemFactory(Material.STONE,"aa", null)
    //    inv.inventory.setItem(1,item)
    //    inv.open(players.elementAt(0))
    //    inv.itemClickEvent(item){p ->
    //        p.sendMessage("aaa")
    //        val meta = item.itemMeta!!
    //        meta.setDisplayName("aaa")
    //        item.setItemMeta(meta)
    //    }
    //}



    // private fun worldTest(){
    //     val wc = WorldCreator("New World")
    //     wc.environment(World.Environment.NORMAL)
    //     wc.type(WorldType.NORMAL)
    //     wc.createWorld()
    // }
}