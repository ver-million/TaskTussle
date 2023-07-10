package me.wanttobee.maseg.systems.games.bingo

import me.wanttobee.maseg.MASEGUtil
import me.wanttobee.maseg.systems.utils.interactiveInventory.InteractiveInventory
import me.wanttobee.maseg.systems.utils.teams.Team
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack


class BingoInventory(private val color:ChatColor) : InteractiveInventory(45,"${ChatColor.BOLD}${color}Team ${color.name} - Bingo") {
    private lateinit var items : Array<Material?>

    init{
        createBorder()
    }
    private fun createBorder(){
        for(i in 0 until 5){
            this.inventory.setItem(1 + 9*i,separator)
            this.inventory.setItem(7 + 9*i,separator)
        }
    }

    fun getCompleteAmount() : Int{
        var a = 0
        for(item in items)
            if(item == null) a++
        return a
    }

    fun shuffle(){
        val newItems = items.filterNotNull().shuffled()
        items = newItems.toTypedArray()
        for(i in 0 until 25){
            val item = MASEGUtil.itemFactory(newItems[i], "", "§7you haven't obtained this item yet")
            this.inventory.setItem(2 + (i%5) + 9*(i/5),item)
        }
    }

    fun putCard( itemSet : Array<Material>) : Boolean{
        if(itemSet.size < 25) return false
        items = Array(25) {i -> itemSet[i] }
        for(i in 0 until 25){
            val item = MASEGUtil.itemFactory(itemSet[i], "", "§7you haven't obtained this item yet")
            this.inventory.setItem(2 + (i%5) + 9*(i/5),item)
        }
        return true
    }

    fun generateTeamItems(game : Map<Team, BingoInventory>, seeOtherTeams : Boolean){
        val keys = game.keys
        for(index in 0 until 10 ){
            if(index >= keys.size){
                val item = MASEGUtil.itemFactory(Material.GRAY_STAINED_GLASS, "${ChatColor.RESET}${ChatColor.GRAY}Empty Card", null)
                this.inventory.setItem( 9*(index%5) + 8*(index/5),item)
                continue
            }
            val team = keys.elementAt(index)
            val card = game[team]

            val realAmount = card!!.getCompleteAmount()
            val amount = if(realAmount < 1) 1 else realAmount
            var memberString = "${ChatColor.GRAY}"
            val teamMembers = team.getMembers()
            for(memberID in teamMembers.indices){
                memberString += teamMembers[memberID].name
                if(memberID != teamMembers.size -1)
                    memberString += ", "
            }
            if(card == this){
                val item = MASEGUtil.itemFactory( MASEGUtil.colorMaterial(team.getColor(), Material.WHITE_CONCRETE),
                    "${ChatColor.BOLD}${team.getColor()}This Card", memberString,amount, true)
                this.inventory.setItem( 9*(index%5) + 8*(index/5),item)
            }else{
                val item = if(seeOtherTeams)  MASEGUtil.itemFactory( MASEGUtil.colorMaterial(team.getColor(), Material.WHITE_CONCRETE),
                    "${ChatColor.BOLD}${team.getColor()}Team ${team.getColor().name}",memberString,amount )
                else MASEGUtil.itemFactory( MASEGUtil.colorMaterial(team.getColor(), Material.WHITE_STAINED_GLASS),
                    "${ChatColor.BOLD}${team.getColor()}Team ${team.getColor().name}",memberString,amount )

                if(seeOtherTeams) this.itemClickEvent(item) { player -> card.open(player) }
                this.inventory.setItem(9*(index%5) + 8*(index/5),item)
            }
        }
    }

    fun completeItem(item: Material) : Boolean{
        val itemIndex= items.indexOf(item)
        if(itemIndex >= 0){
            items[itemIndex] = null
            val completeItem : ItemStack = MASEGUtil.itemFactory(
                MASEGUtil.colorMaterial(color, Material.WHITE_STAINED_GLASS_PANE),
                "$color${MASEGUtil.getRealName(item)}",
                "${ChatColor.GOLD}Complete", true
                )
            inventory.setItem(2 + (itemIndex%5) + 9*(itemIndex/5), completeItem )
            return true
        }
        return false
    }
    //triple<Horizontal, Vertical, Diagonal>
    fun getCompletedLines() : Triple<Int,Int,Int>{
        var horizontal = 0
        var vertical = 0
        var diagonal = 0

        for(i in 0 until 5){
            if( items[0+i*5] == null &&
                items[1+i*5] == null &&
                items[2+i*5] == null &&
                items[3+i*5] == null &&
                items[4+i*5] == null )
                horizontal++

            if( items[i+0*5] == null &&
                items[i+1*5] == null &&
                items[i+2*5] == null &&
                items[i+3*5] == null &&
                items[i+4*5] == null)
                vertical++
        }
        if( items[0 + 0*5] == null &&
            items[1 + 1*5] == null &&
            items[2 + 2*5] == null &&
            items[3 + 3*5] == null &&
            items[4 + 4*5] == null)
            diagonal++

        if( items[4 + 0*5] == null &&
            items[3 + 1*5] == null &&
            items[2 + 2*5] == null &&
            items[1 + 3*5] == null &&
            items[0 + 4*5] == null)
            diagonal++

        return Triple(horizontal,vertical,diagonal)
    }


}