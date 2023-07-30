package me.wanttobee.tasktussle

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

//a class with different utilities that don't quit fit in any other class
//-=Public Methods=-
//
//itemFactory(...) : ItemStack -> A method to makes it easier to create ItemStacks
//
// colorMaterial(ChatColor, Material) : Material -> A method that return the corresponding material that correspond with the color
// and white version of that material
// for example :   (ChatColor.RED, Material.WHITE_WOOL) -> MATERIAL.RED_WOOL
// returns the input material if there is no match (or you put chatColor WHITE)
//
// getRealName(Material) : String -> A method that returns the in-game name of a material (because for some reason there is no easier way)
object TTUtil {
    val IDKey: NamespacedKey = NamespacedKey(TTPlugin.instance, "maseg_identifier")
    private var id = 0
    fun itemFactory(material: Material, title: String, lore: List<String>?,amount: Int, enchanted : Boolean = false): ItemStack {
        val itemStack = ItemStack(material, amount)
        val itemMeta = itemStack.itemMeta
        itemMeta?.setDisplayName(title)
        itemMeta?.persistentDataContainer?.set(IDKey, PersistentDataType.INTEGER, id++)
        itemMeta?.lore = lore
        itemStack.itemMeta = itemMeta
        if (enchanted) {
            itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1)
            val meta = itemStack.itemMeta
            meta?.addItemFlags(ItemFlag.HIDE_ENCHANTS)
            itemStack.itemMeta = meta
        }
        return itemStack
    }
    fun itemFactory(material: Material, title: String, lore: String, enchanted : Boolean = false): ItemStack {
        return itemFactory(material, title, listOf(lore), 1,enchanted)
    }
    fun itemFactory(material: Material, title: String,  lore: List<String>?, enchanted : Boolean = false): ItemStack {
        return itemFactory(material, title, lore, 1,enchanted)
    }
    fun itemFactory(material: Material, title: String, lore: String, amount:Int, enchanted : Boolean = false): ItemStack {
        return itemFactory(material, title, listOf(lore), amount,enchanted)
    }

    fun colorMaterial(color: ChatColor, whiteMaterial: Material): Material {
        val colorName : String= when(color){
            ChatColor.DARK_BLUE -> "BLUE"
            ChatColor.BLUE -> "BLUE"
            ChatColor.DARK_AQUA -> "CYAN"
            ChatColor.AQUA -> "LIGHT_BLUE"
            ChatColor.DARK_GREEN -> "GREEN"
            ChatColor.GREEN -> "LIME"
            ChatColor.YELLOW -> "YELLOW"
            ChatColor.GOLD -> "ORANGE"
            ChatColor.RED -> "RED"
            ChatColor.DARK_RED -> "BROWN"
            ChatColor.LIGHT_PURPLE -> "MAGENTA"
            ChatColor.DARK_PURPLE -> "PURPLE"
            ChatColor.BLACK -> "BLACK"
            ChatColor.DARK_GRAY -> "GRAY"
            ChatColor.GRAY -> "LIGHT_GRAY"
            else -> return whiteMaterial //if its white, just return the original material
        }
        val whiteMaterialName = whiteMaterial.name.removePrefix("WHITE")

        val coloredMaterialName = colorName + whiteMaterialName

        return try {
            Material.valueOf(coloredMaterialName)
        } catch (e: IllegalArgumentException) {
            whiteMaterial
        }
    }

    fun getRealName(material: Material): String {
        val name = material.name.lowercase()
        var words = name.split("_")

        if (words.size == 2 && words[1] == "minecart") {
            words = listOf(words[1], "with", words[0])
        }
        else if(words.contains("template")) return "Smithing Template"
        else if(words.contains("music")) return "Music Disc"

        val formattedWords = words.map { word ->
            when (word) {
                "of", "on", "a", "with" -> word
                "tnt" -> word.uppercase()
                else -> word.capitalize()
            }
        }
        return formattedWords.joinToString(" ")
    }
}
