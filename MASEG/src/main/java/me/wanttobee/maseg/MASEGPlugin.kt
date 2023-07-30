package me.wanttobee.maseg;

import me.wanttobee.maseg.commands.MGCommands
import me.wanttobee.maseg.commands.MUCommands
import me.wanttobee.maseg.systems.games.bingo.BingoSystem
import me.wanttobee.maseg.systems.games.manHunt.ManHuntSystem
import me.wanttobee.maseg.systems.games.taskTussle.TaskTussleCommand
import me.wanttobee.maseg.systems.games.taskTussle.base.TaskSystem
import me.wanttobee.maseg.systems.games.taskTussle.tasks.ObtainTaskFiles
import me.wanttobee.maseg.systems.utils.interactiveInventory.InteractiveInventorySystem
import me.wanttobee.maseg.systems.utils.interactiveItem.InteractiveItemSystem
import me.wanttobee.maseg.systems.utils.playerTracker.PlayerTrackerSystem
import me.wanttobee.maseg.systems.utils.randomPlayer.RandomPlayerSystem
import me.wanttobee.maseg.systems.utils.teams.TeamSystem
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.annotation.command.Command
import org.bukkit.plugin.java.annotation.command.Commands
import org.bukkit.plugin.java.annotation.dependency.Library
import org.bukkit.plugin.java.annotation.plugin.ApiVersion
import org.bukkit.plugin.java.annotation.plugin.Description
import org.bukkit.plugin.java.annotation.plugin.Plugin
import org.bukkit.plugin.java.annotation.plugin.author.Author


@Plugin(name = "MASEG", version ="S-1.0")
@ApiVersion(ApiVersion.Target.v1_20)
@Author("WantToBeeMe")
@Description("Minecraft Awsome Super Epic Games")

@Commands(
    Command(name = "mg", aliases = ["masegGames"], usage = "/mg help"),
    Command(name = "mu", aliases = ["masegUtil"], usage = "/mu help"),
    Command(name = "taskTussle", aliases = ["tt"], usage = "/taskTussle help"),
)

@Library("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.22") //kotlin !!
class MASEGPlugin : JavaPlugin() {
    companion object {
        lateinit var instance: MASEGPlugin
        val title = "${ChatColor.GRAY}[${ChatColor.GOLD}MASEG${ChatColor.GRAY}]${ChatColor.RESET}"
    }

    override fun onEnable() {
        instance = this

        getCommand("mg")?.setExecutor(MGCommands)
        getCommand("mg")?.tabCompleter = MGCommands

        getCommand("mu")?.setExecutor(MUCommands)
        getCommand("mu")?.tabCompleter = MUCommands

        getCommand("taskTussle")?.setExecutor(TaskTussleCommand)
        getCommand("taskTussle")?.tabCompleter = TaskTussleCommand

        ObtainTaskFiles.generateDefaultFolder()
        logVersions()

        server.pluginManager.registerEvents(ManHuntSystem, this)
        server.pluginManager.registerEvents(BingoSystem, this)
        server.pluginManager.registerEvents(InteractiveInventorySystem, this)
        server.pluginManager.registerEvents(InteractiveItemSystem, this)
        server.pluginManager.registerEvents(TeamSystem,this)
        server.pluginManager.registerEvents(TaskSystem, this)

        StartUpTests.run()
    }

    override fun onDisable() {}

    private fun logVersions(){
        this.logger.info(ManHuntSystem.version)
        this.logger.info(BingoSystem.version)
        this.logger.info(RandomPlayerSystem.version)
        this.logger.info(PlayerTrackerSystem.version)
        this.logger.info(TeamSystem.version)
        this.logger.info(InteractiveInventorySystem.version)
        this.logger.info(InteractiveItemSystem.version)
    }



}
