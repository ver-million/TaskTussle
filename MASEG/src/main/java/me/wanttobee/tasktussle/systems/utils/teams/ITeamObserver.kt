package me.wanttobee.tasktussle.systems.utils.teams

import org.bukkit.entity.Player

interface ITeamObserver {

    fun onTeamClear()
    fun onAddMember(member : Player)
    fun onRemoveMember(member: Player)
    fun onSwapMember(leave: Player, enter:Player)
}