package me.wanttobee.maseg.systems.games.taskTussle.tasks

import me.wanttobee.maseg.commands.commandTree.CommandBoolLeaf
import me.wanttobee.maseg.commands.commandTree.CommandStringLeaf
import me.wanttobee.maseg.commands.commandTree.ICommandBranch
import me.wanttobee.maseg.systems.games.taskTussle.base.ITask
import me.wanttobee.maseg.systems.games.taskTussle.base.ITaskManager
import me.wanttobee.maseg.systems.utils.teams.Team
import org.bukkit.ChatColor
import org.bukkit.Material

object ObtainTaskManager : ITaskManager<ObtainTask> {
    private var fileName = "default.yml"
    var enabled = true
        private set
    var handInItem = false
        private set

    override fun generateTasks(associatedTeam: Team, amounts: Triple<Int, Int, Int>, skip: List<ITask> ): Array<ObtainTask>? {
        val taskPool = ObtainTaskFiles.readFile(fileName) ?: return null
        val realSkip :  List<ObtainTask> = skip.filter { task -> task is ObtainTask } as  List<ObtainTask>
        val easyPool  =  taskPool.first .filter{mat -> !realSkip.any { task -> task.itemToObtain == mat }}.shuffled()
        val normalPool = taskPool.second.filter{mat -> !realSkip.any { task -> task.itemToObtain == mat }}.shuffled()
        val hardPool  =  taskPool.third .filter{mat -> !realSkip.any { task -> task.itemToObtain == mat }}.shuffled()

        val realEasyAmount = if(amounts.first > easyPool.size) easyPool.size else amounts.first
        val realHardAmount = if(amounts.third > hardPool.size) hardPool.size else amounts.third

        var normalAmount = amounts.second
        normalAmount += amounts.first - realEasyAmount
        normalAmount += amounts.third - realHardAmount
        var realNormalAmount = if(normalAmount > normalPool.size) normalPool.size else normalAmount

        val selectedMaterials = mutableListOf<Material>()
        selectedMaterials.addAll(easyPool.take(realEasyAmount))
        selectedMaterials.addAll(normalPool.take(realNormalAmount))
        selectedMaterials.addAll(hardPool.take(realHardAmount))
        selectedMaterials.shuffle()
        return Array(selectedMaterials.size) {i -> ObtainTask(selectedMaterials[i],associatedTeam ) }
    }

    override val settings: Array<ICommandBranch> = arrayOf(
            CommandBoolLeaf("obtainTask_handIntItem",
                    { p,arg -> handInItem = arg; p.sendMessage("${ChatColor.GRAY}(obtainTask) ${ChatColor.GOLD}handIntItem${ChatColor.WHITE} is changed to: ${ChatColor.GOLD}$arg") },
                    { p -> p.sendMessage("${ChatColor.GRAY}(obtainTask) ${ChatColor.GOLD}handIntItem${ChatColor.WHITE} is currently: ${ChatColor.GOLD}${handInItem}") }),
            CommandBoolLeaf("obtainTask_enabled",
                    { p,arg -> enabled = arg; p.sendMessage("${ChatColor.GRAY}(obtainTask) ${ChatColor.GOLD}enabled${ChatColor.WHITE} is changed to: ${ChatColor.GOLD}$arg") },
                    { p -> p.sendMessage("${ChatColor.GRAY}(obtainTask) ${ChatColor.GOLD}enabled${ChatColor.WHITE} is currently: ${ChatColor.GOLD}${enabled}") }),
            CommandStringLeaf("obtainTask_fileName", {ObtainTaskFiles.getAllFileNames()},
                    { p,arg -> fileName = arg; p.sendMessage("${ChatColor.GRAY}(obtainTask) ${ChatColor.GOLD}fileName${ChatColor.WHITE} is changed to: ${ChatColor.GOLD}$arg") },
                    { p -> p.sendMessage("${ChatColor.GRAY}(obtainTask) ${ChatColor.GOLD}fileName${ChatColor.WHITE} is currently: ${ChatColor.GOLD}${fileName}") }),
            )
}