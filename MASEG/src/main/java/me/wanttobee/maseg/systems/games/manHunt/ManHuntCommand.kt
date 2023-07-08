package me.wanttobee.maseg.systems.games.manHunt

import me.wanttobee.maseg.MASEGPlugin
import me.wanttobee.maseg.commands.ISystemCommand
import me.wanttobee.maseg.commands.commandTree.*
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object ManHuntCommand : ISystemCommand {
    override val exampleCommand = "/mg manHunt start [amount/Int] [must/Players]+ ![don't/Players]+"
    override val helpText = "selects a random player that is online to be hunted"
    override val key = "manHunt"
    override val aliases = arrayOf<String>()

    private val plugin = MASEGPlugin.instance

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
        CommandIntLeaf("randomisingJumps",0, null,
            { p,arg -> ManHuntSystem.randomisingJumps = arg; p.sendMessage("${ChatColor.GOLD}randomisingJumps${ChatColor.WHITE} is changed to: ${ChatColor.GOLD}$arg") },
            { p -> p.sendMessage("${ChatColor.GOLD}randomisingJumps${ChatColor.WHITE} is currently: ${ChatColor.GOLD}${ManHuntSystem.randomisingJumps}") }),
        CommandBoolLeaf("saveWorldLocation",
            { p,arg -> ManHuntSystem.saveWorldLocation = arg; p.sendMessage("${ChatColor.GOLD}saveWorldLocation${ChatColor.WHITE} is changed to: ${ChatColor.GOLD}$arg") },
            { p -> p.sendMessage("${ChatColor.GOLD}saveWorldLocation${ChatColor.WHITE} is currently: ${ChatColor.GOLD}${ManHuntSystem.saveWorldLocation}") }),
        CommandIntLeaf("slowDuration",0, null,
            { p,arg -> ManHuntSystem.slowDuration = arg; p.sendMessage("${ChatColor.GOLD}slowDuration${ChatColor.WHITE} is changed to: ${ChatColor.GOLD}$arg") },
            { p -> p.sendMessage("${ChatColor.GOLD}slowDuration${ChatColor.WHITE} is currently: ${ChatColor.GOLD}${ManHuntSystem.slowDuration}") }),
    ))

    private val startTree = CommandIntLeaf("start", 1, null,
        { p,v -> ManHuntSystem.startHunt(p, v, emptyList(), emptyList()) },)

    private val stopTree = CommandEmptyLeaf("stop") { p -> ManHuntSystem.stopHunt(p) }

    private val manHuntTree = CommandTree("manHunt", arrayOf(
        settingsTree,
        startTree,
        stopTree,
    ))



    override fun onCommand(sender: Player, args : Array<String>) { manHuntTree.onCommand(sender, args) }
    override fun onTabComplete(sender: Player,  args : Array<String>) : List<String> { return manHuntTree.getTabComplete(sender, args) }
}