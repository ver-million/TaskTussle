package me.wanttobee.maseg.systems.games.manHunt

import me.wanttobee.maseg.MASEGPlugin
import me.wanttobee.maseg.commands.ISystemCommand
import me.wanttobee.maseg.commands.commandTree.*
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object ManHuntCommand : ISystemCommand {
    private val plugin = MASEGPlugin.instance
    override val exampleCommand = "/mg manHunt start [amount/Int] [must/Players]+ ![don't/Players]+"
    override val helpText = "selects a random player that is online to be hunted"



    private val settingsTree = CommandTree("setting", arrayOf(
        CommandBoolLeaf("compassEnabled",
            { p,arg -> ManHuntSystem.compassEnabled = arg; p.sendMessage("${ChatColor.GOLD}compassEnabled${ChatColor.WHITE} is changed to: ${ChatColor.GOLD}$arg") },
            { p -> p.sendMessage("${ChatColor.GOLD}compassEnabled${ChatColor.WHITE} is currently: ${ChatColor.GOLD}${ManHuntSystem.compassEnabled}") }),
        CommandBoolLeaf("compassFocusClosest",
            { p,arg -> ManHuntSystem.compassFocusClosest = arg; p.sendMessage("${ChatColor.GOLD}compassFocusClosest${ChatColor.WHITE} is changed to: ${ChatColor.GOLD}$arg") },
            { p -> p.sendMessage("${ChatColor.GOLD}compassFocusClosest${ChatColor.WHITE} is currently: ${ChatColor.GOLD}${ManHuntSystem.compassFocusClosest}") }),
        CommandIntLeaf("compassUpdateDuration",1, null,
            { p,arg -> ManHuntSystem.compassUpdateDuration  = arg; p.sendMessage("${ChatColor.GOLD}compassUpdateDuration${ChatColor.WHITE} is changed to: ${ChatColor.GOLD}$arg") },
            { p -> p.sendMessage("${ChatColor.GOLD}compassUpdateDuration${ChatColor.WHITE} is currently: ${ChatColor.GOLD}${ManHuntSystem.compassUpdateDuration}") }),
        CommandBoolLeaf("saveWorldLocation",
            { p,arg -> ManHuntSystem.saveWorldLocation = arg; p.sendMessage("${ChatColor.GOLD}saveWorldLocation${ChatColor.WHITE} is changed to: ${ChatColor.GOLD}$arg") },
            { p -> p.sendMessage("${ChatColor.GOLD}saveWorldLocation${ChatColor.WHITE} is currently: ${ChatColor.GOLD}${ManHuntSystem.saveWorldLocation}") }),
        CommandIntLeaf("slowDuration",0, null,
            { p,arg -> ManHuntSystem.slowDuration = arg; p.sendMessage("${ChatColor.GOLD}slowDuration${ChatColor.WHITE} is changed to: ${ChatColor.GOLD}$arg") },
            { p -> p.sendMessage("${ChatColor.GOLD}slowDuration${ChatColor.WHITE} is currently: ${ChatColor.GOLD}${ManHuntSystem.slowDuration}") }),
    ))

    private val startTree = CommandIntLeaf("start", 1, null,
        { p,v -> ManHuntSystem.startHunt(p, v, null) },)

    private val stopTree = CommandEmptyLeaf("stop") { p -> ManHuntSystem.stopHunt(p) }

    override val baseTree = CommandTree("manHunt", arrayOf(
        settingsTree,
        startTree,
        stopTree,
    ))
}