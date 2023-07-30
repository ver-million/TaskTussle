package me.wanttobee.maseg.systems.games.taskTussle.base

import me.wanttobee.maseg.MASEGPlugin
import me.wanttobee.maseg.MASEGUtil
import me.wanttobee.maseg.systems.utils.interactiveInventory.InteractiveInventory
import me.wanttobee.maseg.systems.utils.teams.Team
import org.bukkit.ChatColor
import org.bukkit.Material

class TeamIcon(private val ownerInventory : InteractiveInventory,private val associatedTeam : Team, private val totalTaskAmount : Int?){
    private val publicTeamIcon = MASEGUtil.itemFactory(
            MASEGUtil.colorMaterial(associatedTeam.color, Material.WHITE_STAINED_GLASS), associatedTeam.getDisplayName(), null)
    private val privateTeamIcon = MASEGUtil.itemFactory(
            MASEGUtil.colorMaterial(associatedTeam.color, Material.WHITE_CONCRETE), "${associatedTeam.color}This Card", null, true)

    private val updateInventory: MutableList<()->Unit> = mutableListOf()
    private val removeClickEvent : MutableList<()->Unit> = mutableListOf()
    private val addClickEvent : MutableList<()->Unit> = mutableListOf()


    init{
        updateTeam()
        setAmount(0)
    }

    private var clickable = false

    fun addToInventory(slot: Int, inv : InteractiveInventory){
        if(inv == ownerInventory){
            inv.inventory.setItem(slot,privateTeamIcon)
            updateInventory.add { inv.inventory.setItem(slot,privateTeamIcon) }
        } else {
            inv.inventory.setItem(slot,publicTeamIcon)
            if(clickable){
                inv.itemClickEvent(publicTeamIcon) { player -> ownerInventory.open(player) }
            }
            updateInventory.add { inv.inventory.setItem(slot,publicTeamIcon) }
            removeClickEvent.add { inv.itemClickEvent(publicTeamIcon,null) }
            addClickEvent.add {
                inv.itemClickEvent(publicTeamIcon) { player ->
                ownerInventory.open(player)
            } }
        }
    }

    private fun updateIcon(){
        for(thing in updateInventory)
            thing.invoke()
        if(clickable) {
            for(thing in addClickEvent)
                thing.invoke()
        }
    }


    fun setAmount(amount : Int){
        if(clickable) for(thing in removeClickEvent)
            thing.invoke()

        val amountText = "${ChatColor.GRAY}$amount/$totalTaskAmount"
        publicTeamIcon.amount = if(amount < 1) 1 else amount
        privateTeamIcon.amount = if(amount < 1) 1 else amount

        val publicMeta = publicTeamIcon.itemMeta!!
        val publicLore = publicMeta.lore!!
        publicLore[publicLore.size - 1] = amountText
        publicMeta.lore = publicLore
        publicTeamIcon.itemMeta = publicMeta

        val privateMeta = privateTeamIcon.itemMeta!!
        val privateLore = privateMeta.lore!!
        privateLore[privateLore.size - 1] = amountText
        privateMeta.lore = privateLore
        privateTeamIcon.itemMeta = privateMeta
        updateIcon()
    }

    fun setClickable(value : Boolean) {
        if(clickable) for(thing in removeClickEvent)
            thing.invoke()

        clickable = value
        if(value) publicTeamIcon.type = MASEGUtil.colorMaterial(associatedTeam.color, Material.WHITE_CONCRETE)
        else publicTeamIcon.type = MASEGUtil.colorMaterial(associatedTeam.color, Material.WHITE_STAINED_GLASS)
        updateIcon()
    }

    fun updateTeam(){
        if(clickable) for(thing in removeClickEvent)
            thing.invoke()

        val memberString = "${ChatColor.GRAY}${associatedTeam.getMemberString()}"
        val amountText = "${ChatColor.GRAY}0/$totalTaskAmount"
        val publicMeta = publicTeamIcon.itemMeta!!
        publicMeta.lore = listOf(memberString,amountText )
        publicTeamIcon.itemMeta = publicMeta

        val privateMeta = privateTeamIcon.itemMeta!!
        privateMeta.lore = listOf(memberString,amountText)
        privateTeamIcon.itemMeta = privateMeta
        updateIcon()
    }
}