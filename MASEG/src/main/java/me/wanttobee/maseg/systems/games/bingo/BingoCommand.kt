package me.wanttobee.maseg.systems.games.bingo

import me.wanttobee.maseg.MASEGPlugin
import me.wanttobee.maseg.commands.ISystemCommand
import me.wanttobee.maseg.commands.commandTree.*
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object BingoCommand : ISystemCommand {
    override val exampleCommand = "/mg bingo "
    override val helpText = "bingo yea"
    override val key = "bingo"
    override val aliases = arrayOf<String>()


    private val settingsTree = CommandTree("setting",arrayOf(
        CommandStringLeaf("winCondition", BingoSystem.possibleConditions,
            { p,arg -> BingoSystem.winCondition = arg; p.sendMessage("${ChatColor.GOLD}winCondition${ChatColor.WHITE} is changed to: ${ChatColor.GOLD}$arg") },
            { p -> p.sendMessage("${ChatColor.GOLD}winCondition${ChatColor.WHITE} is currently: ${ChatColor.GOLD}${BingoSystem.winCondition}") }),
        CommandIntLeaf("easyRatio",0, null,
            { p,arg -> BingoSystem.easyRatio = arg; p.sendMessage("${ChatColor.GOLD}easyRatio${ChatColor.WHITE} is changed to: ${ChatColor.GOLD}$arg") },
            { p -> p.sendMessage("${ChatColor.GOLD}easyRatio${ChatColor.WHITE} is currently: ${ChatColor.GOLD}${BingoSystem.easyRatio}") }),
        CommandIntLeaf("normalRatio",0, null,
            { p,arg -> BingoSystem.normalRatio = arg; p.sendMessage("${ChatColor.GOLD}normalRatio${ChatColor.WHITE} is changed to: ${ChatColor.GOLD}$arg") },
            { p -> p.sendMessage("${ChatColor.GOLD}normalRatio${ChatColor.WHITE} is currently: ${ChatColor.GOLD}${BingoSystem.normalRatio}") }),
        CommandIntLeaf("hardRatio",0, null,
            { p,arg -> BingoSystem.hardRatio = arg; p.sendMessage("${ChatColor.GOLD}hardRatio${ChatColor.WHITE} is changed to: ${ChatColor.GOLD}$arg") },
            { p -> p.sendMessage("${ChatColor.GOLD}hardRatio${ChatColor.WHITE} is currently: ${ChatColor.GOLD}${BingoSystem.hardRatio}") }),
        CommandBoolLeaf("seeOtherTeams",
            { p,arg -> BingoSystem.seeOtherTeams = arg; p.sendMessage("${ChatColor.GOLD}seeOtherTeams${ChatColor.WHITE} is changed to: ${ChatColor.GOLD}$arg") },
            { p -> p.sendMessage("${ChatColor.GOLD}seeOtherTeams${ChatColor.WHITE} is currently: ${ChatColor.GOLD}${BingoSystem.seeOtherTeams}") }),
        CommandBoolLeaf("handInItems",
            { p,arg -> BingoSystem.handInItem = arg; p.sendMessage("${ChatColor.GOLD}handInItems${ChatColor.WHITE} is changed to: ${ChatColor.GOLD}$arg") },
            { p -> p.sendMessage("${ChatColor.GOLD}handInItems${ChatColor.WHITE} is currently: ${ChatColor.GOLD}${BingoSystem.handInItem}") }),

        CommandIntLeaf("refreshTokens",0, 5,
            { p,arg -> BingoSystem.refreshToken = arg; p.sendMessage("${ChatColor.GOLD}refreshTokens${ChatColor.WHITE} is changed to: ${ChatColor.GOLD}$arg") },
            { p -> p.sendMessage("${ChatColor.GOLD}refreshTokens${ChatColor.WHITE} is currently: ${ChatColor.GOLD}${BingoSystem.refreshToken}") }),
        CommandIntLeaf("shuffleTokens",0, 5,
            { p,arg -> BingoSystem.shuffleToken = arg; p.sendMessage("${ChatColor.GOLD}shuffleTokens${ChatColor.WHITE} is changed to: ${ChatColor.GOLD}$arg") },
            { p -> p.sendMessage("${ChatColor.GOLD}shuffleTokens${ChatColor.WHITE} is currently: ${ChatColor.GOLD}${BingoSystem.shuffleToken}") }),
    ))

    private val fileTree = CommandTree("files",arrayOf(
        CommandEmptyLeaf("list") { p ->
            p.sendMessage("${ChatColor.GOLD}BINGO")
            for(fileName in BingoFileSystem.getAllBingoPools()){
                val materials = BingoFileSystem.getBingoPoolMaterials(fileName) ?: Triple(emptyArray(), emptyArray(),emptyArray())
                p.sendMessage("${ChatColor.YELLOW}- $fileName${ChatColor.GRAY}  ${materials.first.size}, ${materials.second.size}, ${materials.third.size}")
            }},
        CommandStringLeaf("create", null,
            {p,name ->
                if(BingoFileSystem.getBingoPoolFile(name) == null){
                    BingoFileSystem.createDefaultBingoPool(name)
                    p.sendMessage("${ChatColor.GREEN} created file ${ChatColor.GRAY}$name")
                } else  p.sendMessage("${ChatColor.RED} the file ${ChatColor.GRAY}$name ${ChatColor.RED}already exists")
            }),
    ))


    private val stopTree = CommandEmptyLeaf("stop") { p -> BingoSystem.stop(p) }

    private val startTree = CommandPairLeaf("start",
        CommandIntLeaf("amountOfTeams", 1, 10, {_,_->}),
        CommandStringLeaf("bingoPool", BingoFileSystem.getAllBingoPools(), {_,_->}),
        {p,v -> BingoSystem.start(p, v.first,v.second, true) }
    )


    private val bingoTree = CommandTree("Bingo", arrayOf(
        startTree,
        stopTree,
        fileTree,
        settingsTree,
    ))



    override fun onCommand(sender: Player, args : Array<String>) { bingoTree.onCommand(sender, args) }
    override fun onTabComplete(sender: Player,  args : Array<String>) : List<String> { return bingoTree.getTabComplete(sender, args) }
}