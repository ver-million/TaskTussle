package me.wanttobee.maseg

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType


object MASEGUtil {
    val IDKey: NamespacedKey = NamespacedKey(MASEGPlugin.instance, "maseg_identifier")
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