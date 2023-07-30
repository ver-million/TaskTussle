package me.wanttobee.tasktussle.systems.games.taskTussle.base

import me.wanttobee.tasktussle.TTPlugin
import me.wanttobee.tasktussle.TTUtil
import me.wanttobee.tasktussle.systems.utils.interactiveInventory.InteractiveInventory
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class TaskIcon(private val icon: Material,private val taskTitle : String, private val description : List<String>) {
    private val item : ItemStack = TTUtil.itemFactory(Material.BARRIER,"null",null )

    private val updateInventory: MutableList<()->Unit> = mutableListOf()
    fun addToInventory(slot: Int, inv : InteractiveInventory){
        inv.inventory.setItem(slot,item)
        updateInventory.add { inv.inventory.setItem(slot,item) }
    }

    init{ setHidden() }

    private fun enchant(yes : Boolean){
        if (yes) { item.addUnsafeEnchantment(Enchantment.DURABILITY, 1) }
        else item.removeEnchantment(Enchantment.DURABILITY)
        val meta = item.itemMeta ?: run{
            TTPlugin.instance.logger.info("(TaskIcon) ERROR: cant access itemMeta for taskIcon: $taskTitle")
            return
        }
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        item.setItemMeta(meta)
    }

    fun setState(state: TaskState, teamColor: ChatColor? = null, teamTitle : String? = null){
        when (state) {
            TaskState.ACTIVE -> setActive()
            TaskState.FAILED -> setFailed()
            TaskState.COMPLETED -> setCompleted()
            TaskState.LOCKED -> setLocked()
            TaskState.HIDDEN -> setHidden()
            TaskState.COMPLETED_BY -> {
                if (teamColor != null && teamTitle != null)
                    setCompletedBy(teamColor, teamTitle)
                else setFailed()
            }
        }
        for(up in updateInventory)
            up.invoke()
    }

    private fun setActive(){
        item.type = icon
        val meta = item.itemMeta ?: run{
            TTPlugin.instance.logger.info("(TaskIcon) ERROR: cant access itemMeta for taskIcon: $taskTitle")
            return
        }
        meta.lore = description
        meta.setDisplayName("${ChatColor.GOLD}Task: ${ChatColor.RESET}$taskTitle")
        item.setItemMeta(meta)
        enchant(false)
    }
    private fun setHidden(){
        item.type = Material.GRAY_STAINED_GLASS_PANE
        val meta = item.itemMeta ?: run{
            TTPlugin.instance.logger.info("(TaskIcon) ERROR: cant access itemMeta for taskIcon: $taskTitle")
            return
        }
        meta.lore = listOf(" ${ChatColor.GRAY}this task is not available (yet)")
        meta.setDisplayName("${ChatColor.GRAY}Task: ????")
        item.setItemMeta(meta)
        enchant(false)
    }
    private fun setLocked(){
        item.type = Material.GRAY_STAINED_GLASS_PANE
        val meta = item.itemMeta ?: run{
            TTPlugin.instance.logger.info("(TaskIcon) ERROR: cant access itemMeta for taskIcon: $taskTitle")
            return
        }
        meta.lore = listOf(" ${ChatColor.GRAY}this task is not available (yet)")
        meta.setDisplayName("${ChatColor.GRAY}Task: $taskTitle")
        item.setItemMeta(meta)
        enchant(false)
    }
    private fun setCompletedBy(teamColor : ChatColor, teamTitle: String){
        item.type = TTUtil.colorMaterial(teamColor, Material.WHITE_STAINED_GLASS)
        val meta = item.itemMeta ?: run{
            TTPlugin.instance.logger.info("(TaskIcon) ERROR: cant access itemMeta for taskIcon: $taskTitle")
            return
        }
        meta.lore = listOf(" ${teamColor}Completed by $teamTitle")
        meta.setDisplayName("${ChatColor.GOLD}Task: $teamColor$taskTitle")
        item.setItemMeta(meta)
        enchant(true)
    }
    private fun setCompleted(){
        item.type = Material.LIME_STAINED_GLASS_PANE
        val meta = item.itemMeta ?: run{
            TTPlugin.instance.logger.info("(TaskIcon) ERROR: cant access itemMeta for taskIcon: $taskTitle")
            return
        }
        meta.lore = listOf(" ${ChatColor.GREEN}Completed")
        meta.setDisplayName("${ChatColor.GOLD}Task: ${ChatColor.GREEN}$taskTitle")
        item.setItemMeta(meta)
        enchant(true)
    }
    private fun setFailed(){
        item.type = Material.RED_STAINED_GLASS_PANE
        val meta = item.itemMeta ?: run{
            TTPlugin.instance.logger.info("(TaskIcon) ERROR: cant access itemMeta for taskIcon: $taskTitle")
            return
        }
        meta.lore = listOf(" ${ChatColor.RED}Failed")
        meta.setDisplayName("${ChatColor.GOLD}Task: ${ChatColor.RED}$taskTitle")
        item.setItemMeta(meta)
        enchant(true)
    }
}