package me.wanttobee.maseg.systems.games.bingo

import me.wanttobee.maseg.MASEGPlugin
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import java.io.File


object BingoFileSystem {

    private val plugin = MASEGPlugin.instance
    private val bingoFolder = File(plugin.dataFolder, File.separator + "BingoPools")

    fun createDefaultBingoPool(name: String): Boolean {
        val file = File(bingoFolder, File.separator + name + ".yml")
        val bingoPool: FileConfiguration = YamlConfiguration.loadConfiguration(file)
        if (file.exists()) return !file.exists()

        val testInv = Bukkit.createInventory(null, InventoryType.CHEST)

        plugin.server.scheduler.runTaskAsynchronously(plugin) { _ ->
            bingoPool.createSection("Easy")
            bingoPool.createSection("Normal")
            bingoPool.createSection("Hard")
            val firstCheck = Material.values().filter itemCheck@ {
                if(it.name.contains("COMMAND_BLOCK")) return@itemCheck false
                if(it.name.contains("SPAWN_EGG")) return@itemCheck false
                if(it.name.contains("POTION")) return@itemCheck false
                if(it.name.contains("HEAD")) return@itemCheck false
                if(it.name.contains("INFESTED")) return@itemCheck false
                if(it == Material.KNOWLEDGE_BOOK) return@itemCheck false
                if(it == Material.TIPPED_ARROW) return@itemCheck false
                if(it == Material.STRUCTURE_BLOCK) return@itemCheck false
                if(it == Material.STRUCTURE_VOID) return@itemCheck false
                if(it == Material.BARRIER) return@itemCheck false
                if(it == Material.JIGSAW) return@itemCheck false
                if(it == Material.DEBUG_STICK) return@itemCheck false
                if(it == Material.BEDROCK) return@itemCheck false
                if(it == Material.BUNDLE) return@itemCheck false
                if(it == Material.END_PORTAL_FRAME) return@itemCheck false
                if(it == Material.BUDDING_AMETHYST) return@itemCheck false
                if(it == Material.LARGE_FERN) return@itemCheck false
                if(it == Material.PETRIFIED_OAK_SLAB) return@itemCheck false
                if(it == Material.SPAWNER) return@itemCheck false
                if(it == Material.FARMLAND) return@itemCheck false
                if(it == Material.REINFORCED_DEEPSLATE) return@itemCheck false
                if(it == Material.LIGHT) return@itemCheck false
                if(it == Material.DIRT_PATH) return@itemCheck false
                if(it == Material.FROGSPAWN) return@itemCheck false
                if(it == Material.GLOBE_BANNER_PATTERN) return@itemCheck false
                val disableForInstantLoss = true
                if(disableForInstantLoss){
                    if(it.name.contains("WEATHERED")) return@itemCheck false //takes to long
                    if(it.name.contains("OXIDIZED")) return@itemCheck false //takes to long
                    if(it == Material.DRAGON_EGG) return@itemCheck false //only for 1 team
                    if(it == Material.TALL_GRASS) return@itemCheck false //only obtainable from a specific village chest (with super low chance)
                }
                val disableForSuperHard = true
                if(disableForSuperHard){
                    if(it == Material.CONDUIT) return@itemCheck false
                    if(it == Material.NETHER_STAR) return@itemCheck false
                    if(it == Material.BEACON) return@itemCheck false
                    if(it.name.contains("END_STONE")) return@itemCheck false //we don't just check for "end" because ender-chest is pretty reasonable
                    if(it.name.contains("SHULKER")) return@itemCheck false
                    if(it.name.contains("PURPUR")) return@itemCheck false
                    if(it.name.contains("CHORUS")) return@itemCheck false
                    if(it == Material.END_ROD) return@itemCheck false
                    if(it == Material.ELYTRA) return@itemCheck false
                    if(it == Material.DRAGON_BREATH) return@itemCheck false
                    if(it == Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE) return@itemCheck false
                    if(it == Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE) return@itemCheck false
                    if(it == Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE) return@itemCheck false
                    if(it == Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE) return@itemCheck false
                    if(it == Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE) return@itemCheck false
                    if(it == Material.WARD_ARMOR_TRIM_SMITHING_TEMPLATE) return@itemCheck false
                    if(it == Material.WILD_ARMOR_TRIM_SMITHING_TEMPLATE) return@itemCheck false
                    if(it == Material.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE) return@itemCheck false
                }


                //if its an item that cant be placed in the inventory, then its also not obtainable, (like a flower inside a flowerpot)
                testInv.setItem(0, ItemStack(it))
                val item = testInv.getItem(0)
                item?.type == it
            }
            val materialList = firstCheck.map { it.name }
            bingoPool.set("Normal", materialList)

            bingoPool.save(file)
        }
        return true
    }



    fun getBingoPoolMaterials(name: String): Triple<Array<Material>, Array<Material>, Array<Material>>? {
        val file = getBingoPoolFile(name) ?: return null
        val bingoPool: FileConfiguration = YamlConfiguration.loadConfiguration(file)

        val easyMaterials = bingoPool.getStringList("Easy").mapNotNull { Material.getMaterial(it) }.toTypedArray()

        val normalMaterials = bingoPool.getStringList("Normal").mapNotNull { Material.getMaterial(it) }.toTypedArray()
        val hardMaterials = bingoPool.getStringList("Hard").mapNotNull { Material.getMaterial(it) }.toTypedArray()

        return Triple(easyMaterials, normalMaterials, hardMaterials)
    }


    fun getBingoPoolFile(name: String): File? {
        val fileName = if(name.endsWith(".yml")) name else "$name.yml"
        val file = File(bingoFolder, File.separator + fileName)
        return if (file.exists()) file else null
    }

    fun getAllBingoPools(): Array<String> {
        if (!bingoFolder.exists() || !bingoFolder.isDirectory) return emptyArray()
        return bingoFolder.list { _, name -> name.endsWith(".yml") } ?: emptyArray()
    }

    fun deleteBingoPool(name: String): Boolean {
        val file = getBingoPoolFile(name) ?: return false
        return file.delete()
    }
}
