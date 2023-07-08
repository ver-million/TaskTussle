package me.wanttobee.maseg.systems.utils.playerCompass

import org.bukkit.Location
import org.bukkit.entity.Player

data class PlayerCompassData(val tracking : MutableMap<Player,Array<Location?>>, val saveWorldLocation : Boolean,val itemFocusClosest: Boolean, val itemID: String, var loopID: Int)
