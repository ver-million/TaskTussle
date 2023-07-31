package me.wanttobee.tasktussle.taskTussleSystem.tasks.misc

import me.wanttobee.tasktussle.TTPlugin
import me.wanttobee.tasktussle.TTUtil
import me.wanttobee.tasktussle.utils.interactiveInventory.InteractiveInventory
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class TaskIcon(private val icon: Material,private val taskTitle : String, taskCategory: String,private val progression: ()->String, description : List<String>) {
    private val item : ItemStack = TTUtil.itemFactory(Material.BARRIER,"null",null )
    private var lore : MutableList<String>

    private val updateInventory: MutableList<()->Unit> = mutableListOf()
    fun addToInventory(slot: Int, inv : InteractiveInventory){
        inv.inventory.setItem(slot,item)
        updateInventory.add { inv.inventory.setItem(slot,item) }
    }

    init{
        setHidden()
        lore = mutableListOf(
            "${ChatColor.GOLD}Task: ${ChatColor.WHITE}$taskCategory",
            "${ChatColor.GREEN}Progress: ${ChatColor.WHITE}${progression.invoke()}",
        )
        val newDesc : MutableList<String> = description.toMutableList()
        for(descLine in newDesc.indices){
            if(descLine == 0)newDesc[descLine] = "${ChatColor.AQUA}Description: ${ChatColor.WHITE}${newDesc[descLine]}"
            else newDesc[descLine] = "${ChatColor.WHITE}${newDesc[descLine]}"
        }
        lore += newDesc

    }


    fun updateProgression(){
        lore[1] = "${ChatColor.GREEN}Progress: ${ChatColor.WHITE}${progression.invoke()}"
        val meta = item.itemMeta ?: run{
            TTPlugin.instance.logger.info("(TaskIcon) ERROR: cant access itemMeta for taskIcon: $taskTitle")
            return
        }
        meta.lore = lore
        item.setItemMeta(meta)
    }

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
        meta.lore = lore
        meta.setDisplayName("${ChatColor.WHITE}$taskTitle")
        item.setItemMeta(meta)
        enchant(false)
    }
    private fun setHidden(){
        item.type = Material.GRAY_STAINED_GLASS_PANE
        val meta = item.itemMeta ?: run{
            TTPlugin.instance.logger.info("(TaskIcon) ERROR: cant access itemMeta for taskIcon: $taskTitle")
            return
        }
        meta.lore = listOf("${ChatColor.GRAY}this task is not available (yet)")
        meta.setDisplayName("${ChatColor.GRAY}????")
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)
        item.setItemMeta(meta)
        enchant(false)
    }
    private fun setLocked(){
        item.type = Material.GRAY_STAINED_GLASS_PANE
        val meta = item.itemMeta ?: run{
            TTPlugin.instance.logger.info("(TaskIcon) ERROR: cant access itemMeta for taskIcon: $taskTitle")
            return
        }
        meta.lore = listOf("${ChatColor.GRAY}this task is not available (yet)")
        meta.setDisplayName("${ChatColor.GRAY}$taskTitle")
        item.setItemMeta(meta)
        enchant(false)
    }
    private fun setCompletedBy(teamColor : ChatColor, teamTitle: String){
        item.type = TTUtil.colorMaterial(teamColor, Material.WHITE_STAINED_GLASS)
        val meta = item.itemMeta ?: run{
            TTPlugin.instance.logger.info("(TaskIcon) ERROR: cant access itemMeta for taskIcon: $taskTitle")
            return
        }
        meta.lore = listOf("${teamColor}Completed by $teamTitle")
        meta.setDisplayName("$teamColor$taskTitle")
        item.setItemMeta(meta)
        enchant(true)
    }
    private fun setCompleted(){
        item.type = Material.LIME_STAINED_GLASS_PANE
        val meta = item.itemMeta ?: run{
            TTPlugin.instance.logger.info("(TaskIcon) ERROR: cant access itemMeta for taskIcon: $taskTitle")
            return
        }
        meta.lore = listOf("${ChatColor.GREEN}Completed")
        meta.setDisplayName("${ChatColor.GREEN}$taskTitle")
        item.setItemMeta(meta)
        enchant(true)
    }
    private fun setFailed(){
        item.type = Material.RED_STAINED_GLASS_PANE
        val meta = item.itemMeta ?: run{
            TTPlugin.instance.logger.info("(TaskIcon) ERROR: cant access itemMeta for taskIcon: $taskTitle")
            return
        }
        meta.lore = listOf("${ChatColor.RED}Failed")
        meta.setDisplayName("${ChatColor.RED}$taskTitle")
        item.setItemMeta(meta)
        enchant(true)
    }
}