package me.wanttobee.maseg.systems.games.taskTussle

import me.wanttobee.maseg.MASEGUtil
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class TussleIcon(private val icon : Material, private val  title: String, private val lore : List<String>) {

    val item : ItemStack = MASEGUtil.itemFactory(Material.BARRIER, "null","null") //if everything goes right, you're not suppose to see this
    init{
        setHidden()
    }

    fun setHidden(){
        val meta = item.itemMeta!!
        meta.setDisplayName("${ChatColor.GRAY}???")
        meta.lore = listOf("${ChatColor.GRAY}This task is not available yet")
        item.itemMeta = meta
        item.type = Material.GRAY_STAINED_GLASS_PANE
        removeEnchantGlint()
    }
    fun setLocked(){
        val meta = item.itemMeta!!
        meta.setDisplayName("${ChatColor.GRAY}$title")
        meta.lore = listOf("${ChatColor.GRAY}Locked")
        item.itemMeta = meta
        item.type = Material.GRAY_STAINED_GLASS_PANE
        addEnchantGlint()
    }
    fun setAvailable(){
        val meta = item.itemMeta!!
        meta.setDisplayName("${ChatColor.GOLD}$title")
        val newLore = List(lore.size) {i -> "${ChatColor.WHITE}${lore[i]}"}
        meta.lore = newLore
        item.itemMeta = meta
        item.type = icon
        removeEnchantGlint()
    }
    fun setSucceeded(){
        val meta = item.itemMeta!!
        meta.setDisplayName("${ChatColor.GREEN}$title")
        meta.lore = listOf("${ChatColor.GREEN}Succeeded")
        item.itemMeta = meta
        item.type = Material.GREEN_STAINED_GLASS_PANE
        addEnchantGlint()
    }
    fun setFailed(){
        val meta = item.itemMeta!!
        meta.setDisplayName("${ChatColor.RED}$title")
        meta.lore = listOf("${ChatColor.RED}Failed")
        item.itemMeta = meta
        item.type = Material.RED_STAINED_GLASS_PANE
        addEnchantGlint()
    }

    private fun addEnchantGlint(){
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1)
        val meta = item.itemMeta!!
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        item.itemMeta = meta
    }
    private fun removeEnchantGlint(){
        item.removeEnchantment(Enchantment.DURABILITY)
    }

    fun clone() : TussleIcon{
        return TussleIcon(icon,title,lore)
    }
}

