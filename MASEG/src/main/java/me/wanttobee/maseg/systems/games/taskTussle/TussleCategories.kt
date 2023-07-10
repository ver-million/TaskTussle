package me.wanttobee.maseg.systems.games.taskTussle

import org.bukkit.Material
import org.bukkit.entity.EntityType

//for all of these categories there are a lot more, but they should be fun
object TussleCategories {

    val breedable = arrayOf(
        EntityType.CHICKEN,
        EntityType.COW,
        EntityType.PIG,
        EntityType.SHEEP,
        EntityType.BEE,
        EntityType.TURTLE
    )
    val easyKillable = arrayOf(
        EntityType.CHICKEN,
        EntityType.COW,
        EntityType.PIG,
        EntityType.SHEEP,
        EntityType.COD,
        EntityType.SALMON,
        EntityType.BOAT,
        EntityType.ZOMBIE,
        EntityType.SKELETON,
        EntityType.CREEPER,
        EntityType.SPIDER,
        EntityType.HORSE,
        EntityType.SQUID
    )
    val otherKillable = arrayOf(
        EntityType.TURTLE,
        EntityType.BEE,
        EntityType.DROWNED,
        EntityType.WOLF,
        EntityType.BAT,
    )
    val breakBlocks = arrayOf(
        Material.STONE,
        Material.DIRT,
        Material.DIRT_PATH,
        Material.GRASS_BLOCK,
        Material.GRASS,
        Material.SAND,
    )

    //try to find if it is inside the flower catagory
    val breakGroups = mapOf(
        Pair("Flowers",
            arrayOf(
                Material.DANDELION,
                Material.POPPY,
                Material.BLUE_ORCHID,
                Material.ALLIUM,
                Material.AZURE_BLUET,
                Material.RED_TULIP,
                Material.ORANGE_TULIP,
                Material.PINK_TULIP,
                Material.WHITE_TULIP,
                Material.WHITE_TULIP,
                Material.OXEYE_DAISY,
                Material.CORNFLOWER,
                Material.SUNFLOWER,
                Material.TORCHFLOWER,
                Material.WITHER_ROSE,
                Material.PINK_PETALS,
                Material.LILAC,
                Material.ROSE_BUSH,
                Material.PEONY,
                Material.PITCHER_PLANT,
                Material.LILY_OF_THE_VALLEY)
        ),
        Pair("Planks",
            arrayOf(
                Material.ACACIA_PLANKS,
                Material.BAMBOO_PLANKS,
                Material.BIRCH_PLANKS,
                Material.CHERRY_PLANKS,
                Material.CRIMSON_PLANKS,
                Material.DARK_OAK_PLANKS,
                Material.JUNGLE_PLANKS,
                Material.MANGROVE_PLANKS,
                Material.OAK_PLANKS,
                Material.SPRUCE_PLANKS,
                Material.WARPED_PLANKS,)
        ),
        Pair("Logs",
            arrayOf(
                Material.ACACIA_LOG,
                Material.BIRCH_LOG,
                Material.CHERRY_LOG,
                Material.CRIMSON_STEM,
                Material.DARK_OAK_LOG,
                Material.JUNGLE_LOG,
                Material.MANGROVE_LOG,
                Material.OAK_LOG,
                Material.SPRUCE_LOG,
                Material.WARPED_STEM,)
        ),
        Pair("Ores",
            arrayOf(
                Material.COAL_ORE,
                Material.IRON_ORE,
                Material.COPPER_ORE,
                Material.GOLD_ORE,
                Material.REDSTONE_ORE,
                Material.LAPIS_ORE,
                Material.DIAMOND_ORE,
                Material.EMERALD_ORE,
                Material.DEEPSLATE_COAL_ORE,
                Material.DEEPSLATE_IRON_ORE,
                Material.DEEPSLATE_COPPER_ORE,
                Material.DEEPSLATE_GOLD_ORE,
                Material.DEEPSLATE_REDSTONE_ORE,
                Material.DEEPSLATE_LAPIS_ORE,
                Material.DEEPSLATE_DIAMOND_ORE,
                Material.DEEPSLATE_EMERALD_ORE,
                Material.NETHER_GOLD_ORE,
                Material.NETHER_QUARTZ_ORE,)
        ),
    )

}