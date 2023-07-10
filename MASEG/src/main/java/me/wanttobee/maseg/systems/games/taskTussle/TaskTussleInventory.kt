package me.wanttobee.maseg.systems.games.taskTussle

import me.wanttobee.maseg.MASEGUtil
import me.wanttobee.maseg.systems.utils.interactiveInventory.InteractiveInventory
import org.bukkit.ChatColor
import org.bukkit.Material

class TaskTussleInventory(private val color:ChatColor,private val taskRows : Int) : InteractiveInventory(9 * (3 + taskRows) , "${ChatColor.BOLD}${color}Team ${color.name} - Task Tussle"){


    init{
        createBorder()
    }

    private fun createBorder(){
        for(i in 0 until 9){
            this.inventory.setItem(i,separator)
            this.inventory.setItem(9*(taskRows+1) + i,separator)
        }
    }

    fun setTeams(){

    }

    fun setTasks(){

    }

}