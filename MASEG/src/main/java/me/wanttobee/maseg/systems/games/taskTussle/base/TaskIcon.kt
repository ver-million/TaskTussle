package me.wanttobee.maseg.systems.games.taskTussle.base

import me.wanttobee.maseg.MASEGUtil
import me.wanttobee.maseg.systems.games.taskTussle.tasks.TaskState
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class TaskIcon(private val icon: Material,private val taskTitle : String, private val description : List<String>) {
    val item : ItemStack = MASEGUtil.itemFactory(Material.BARRIER,"null",null )

    init{ setHidden() }

    private fun enchant(yes : Boolean){
        if (yes) { item.addUnsafeEnchantment(Enchantment.DURABILITY, 1) }
        else item.removeEnchantment(Enchantment.DURABILITY)
        val meta = item.itemMeta!!
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        item.setItemMeta(meta)
    }

    fun setState(state: TaskState, teamColor: ChatColor? = null, teamTitle : String? = null){
        when (state){
            TaskState.ACTIVE -> setActive()
            TaskState.FAILED -> setFailed()
            TaskState.COMPLETED -> setCompleted()
            TaskState.LOCKED -> setLocked()
            TaskState.HIDDEN -> setHidden()
            TaskState.COMPLETED_BY -> {
                if(teamColor != null && teamTitle != null)
                    setCompletedBy(teamColor,teamTitle)
                else setFailed()
            }
        }
    }

    private fun setActive(){
        item.type = icon
        val meta = item.itemMeta!!
        meta.lore = description
        meta.setDisplayName("${ChatColor.GOLD}Task: ${ChatColor.RESET}$taskTitle")
        item.setItemMeta(meta)
        enchant(false)
    }
    private fun setHidden(){
        item.type = Material.GRAY_STAINED_GLASS_PANE
        val meta = item.itemMeta!!
        meta.lore = listOf(" ${ChatColor.GRAY}this task is not available (yet)")
        meta.setDisplayName("${ChatColor.GRAY}Task: ????")
        item.setItemMeta(meta)
        enchant(false)
    }
    private fun setLocked(){
        item.type = Material.GRAY_STAINED_GLASS_PANE
        val meta = item.itemMeta!!
        meta.lore = listOf(" ${ChatColor.GRAY}this task is not available (yet)")
        meta.setDisplayName("${ChatColor.GRAY}Task: $taskTitle")
        item.setItemMeta(meta)
        enchant(false)
    }
    private fun setCompletedBy(teamColor : ChatColor, teamTitle: String){
        item.type = MASEGUtil.colorMaterial(teamColor, Material.WHITE_STAINED_GLASS)
        val meta = item.itemMeta!!
        meta.lore = listOf(" ${teamColor}Completed by $teamTitle")
        meta.setDisplayName("${ChatColor.GOLD}Task: $teamColor$taskTitle")
        item.setItemMeta(meta)
        enchant(true)
    }
    private fun setCompleted(){
        item.type = Material.GREEN_STAINED_GLASS_PANE
        val meta = item.itemMeta!!
        meta.lore = listOf(" ${ChatColor.GREEN}Completed")
        meta.setDisplayName("${ChatColor.GOLD}Task: ${ChatColor.GREEN}$taskTitle")
        item.setItemMeta(meta)
        enchant(true)
    }
    private fun setFailed(){
        item.type = Material.RED_STAINED_GLASS_PANE
        val meta = item.itemMeta!!
        meta.lore = listOf(" ${ChatColor.RED}Failed")
        meta.setDisplayName("${ChatColor.GOLD}Task: ${ChatColor.RED}$taskTitle")
        item.setItemMeta(meta)
        enchant(true)
    }
}