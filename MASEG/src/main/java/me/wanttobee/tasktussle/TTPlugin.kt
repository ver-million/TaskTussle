package me.wanttobee.tasktussle;

import me.wanttobee.tasktussle.systems.utils.TTUCommands
import me.wanttobee.tasktussle.systems.games.taskTussle.TaskTussleCommand
import me.wanttobee.tasktussle.systems.games.taskTussle.base.TaskSystem
import me.wanttobee.tasktussle.systems.games.taskTussle.tasks.ObtainTaskFiles
import me.wanttobee.tasktussle.systems.utils.interactiveInventory.InteractiveInventorySystem
import me.wanttobee.tasktussle.systems.utils.interactiveItem.InteractiveItemSystem
import me.wanttobee.tasktussle.systems.utils.teams.TeamSystem
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.plugin.java.annotation.command.Command
import org.bukkit.plugin.java.annotation.command.Commands
import org.bukkit.plugin.java.annotation.dependency.Library
import org.bukkit.plugin.java.annotation.plugin.ApiVersion
import org.bukkit.plugin.java.annotation.plugin.Description
import org.bukkit.plugin.java.annotation.plugin.Plugin
import org.bukkit.plugin.java.annotation.plugin.author.Author


@Plugin(name = "TaskTussle", version ="S-1.0")
@ApiVersion(ApiVersion.Target.v1_20)
@Author("WantToBeeMe")
@Description("A plugin with a lot of possibilities for all kinds of task based games ")

@Commands(
        Command(name = "taskTussle", aliases = ["tt"], usage = "/taskTussle help"),
        Command(name = "taskTussleUtil", aliases = ["ttu"], usage = "/taskTussleUtil help"),
)

@Library("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.22") //kotlin !!
class TTPlugin : JavaPlugin() {
    companion object {
        lateinit var instance: TTPlugin
        val title = "${ChatColor.GRAY}[${ChatColor.GOLD}Task Tussle${ChatColor.GRAY}]${ChatColor.RESET}"
    }

    override fun onEnable() {
        instance = this

        getCommand("taskTussleUtil")?.setExecutor(TTUCommands)
        getCommand("taskTussleUtil")?.tabCompleter = TTUCommands

        getCommand("taskTussle")?.setExecutor(TaskTussleCommand)
        getCommand("taskTussle")?.tabCompleter = TaskTussleCommand

        ObtainTaskFiles.generateDefaultFolder()

        server.pluginManager.registerEvents(InteractiveInventorySystem, this)
        server.pluginManager.registerEvents(InteractiveItemSystem, this)
        server.pluginManager.registerEvents(TeamSystem,this)
        server.pluginManager.registerEvents(TaskSystem, this)

        StartUpTests.run()
    }

    override fun onDisable() {}
}
