package me.wanttobee.maseg.systems.games.bingo

import me.wanttobee.maseg.MASEGPlugin
import me.wanttobee.maseg.MASEGUtil
import me.wanttobee.maseg.systems.utils.interactiveInventory.InteractiveInventorySystem
import me.wanttobee.maseg.systems.utils.interactiveItem.InteractiveItem
import me.wanttobee.maseg.systems.utils.interactiveItem.InteractiveItemSystem
import me.wanttobee.maseg.systems.utils.teams.Team
import me.wanttobee.maseg.systems.utils.teams.TeamSet
import me.wanttobee.maseg.systems.utils.teams.TeamSystem
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerPickupItemEvent
import org.bukkit.inventory.ItemStack

object BingoSystem : Listener {
    const val version = "v1.1 Bingo using [${TeamSystem.version}] [${InteractiveItemSystem.version}] [${InteractiveInventorySystem.version}]"

    //global values to control the game
    private val plugin = MASEGPlugin.instance
    private var bingoGame : TeamSet<BingoInventory>? = null
    private var clickItem : InteractiveItem = InteractiveItem()
    private var bingoPickupLocked = false
    val possibleConditions = arrayOf("1_line","2_lines","3_lines","horizontal_line","vertical_line","diagonal_line", "full_card")
    private var refreshTaskID = -1
    //different settings
    private const val cardSlot = 8
    private const val refreshShuffleTime = 20*60
    var winCondition = possibleConditions.first()
    var easyRatio = 1
    var normalRatio = 3
    var hardRatio = 2
    var seeOtherTeams = true
    //var shuffleToken = 0
    //var refreshToken = 0
    var handInItem = false
    var mutualItems = 15
    var choseTeamsBeforehand = true

    fun startCommand(commander: Player, teams : Int, bingoPoolFile : String, ignoreTeamSize : Boolean = false){
        if(bingoGame != null)  {
            commander.sendMessage("${ChatColor.RED}there is already an bingo game running")
            return
        }
        if(BingoFileSystem.getBingoPoolFile(bingoPoolFile) == null) {
            commander.sendMessage("${ChatColor.RED}the given file doesnt exists ${ChatColor.GRAY} $bingoPoolFile")
            return
        }
        val onlinePlayers = plugin.server.onlinePlayers.shuffled()
        if(teams > onlinePlayers.size && !ignoreTeamSize) {
            commander.sendMessage("${ChatColor.RED}there are not enough players online to make${ChatColor.GRAY } $teams ${ChatColor.RED}teams")
            return
        }

        val pool = BingoFileSystem.getBingoPoolMaterials(bingoPoolFile) ?: run {
            commander.sendMessage("${ChatColor.RED}something went wrong in retrieving the pool data of ${ChatColor.GRAY} $bingoPoolFile")
            return
        }
        val mutualCardItems = generateCard(pool, easyRatio,normalRatio,hardRatio, emptyArray(), mutualItems) ?: run {
            commander.sendMessage("${ChatColor.RED}Cant generate items")
            return
        }
        val cleanedPool = cleanUpPool(pool,mutualCardItems)



        val task : (TeamSet<BingoInventory>) -> Unit = teamTask@{ generatedTeams ->
            bingoGame = generatedTeams
            bingoGame!!.applyToTeams{team,card ->
                val itemList = generateCard(cleanedPool, easyRatio,normalRatio,hardRatio,mutualCardItems,25) ?: run {
                    commander.sendMessage("${ChatColor.RED}Cant generate items")
                    return@applyToTeams
                }
                card.putCard(itemList)
            }
            clickItem = InteractiveItem()
                .setSlot(cardSlot)
                .setItem(MASEGUtil.itemFactory(Material.PAPER, "${ChatColor.GOLD}Bingo Card", null, true))
                .setRightClickEvent { player -> open(player) }

            bingoGame!!.applyToTeams{team,card ->
               // if(shuffleToken == 0 && refreshToken == 0)
               //     card.generateTeamItems(bingoGame!!, seeOtherTeams)
               // else
                    card.generateTeamItems(bingoGame!!, false)
                team.applyToMembers { member -> clickItem.giveToPlayer(member) }
            }

            commander.sendMessage("${ChatColor.GREEN}started bingo game")

            //if(shuffleToken != 0 || refreshToken != 0){
            //    bingoPickupLocked = true
            //    refreshShuffleControl(pool, easyRatio,normalRatio,hardRatio)
            //} else
            beginMessage()
        }

        if(choseTeamsBeforehand)
            TeamSystem.teamMaker(commander, { team -> BingoInventory(team.color) },teams,"Bingo",task)
        else
            task.invoke( TeamSystem.makeXTeams(teams, "Bingo") { team -> BingoInventory(team.color) } )
    }


    private fun beginMessage(){
        if(bingoGame == null) return
        bingoGame!!.applyToAllMembers { p ->
            p.sendMessage("${ChatColor.GREEN}Click with the ${ChatColor.GOLD}Bingo Card${ChatColor.GREEN} in your hand to see your teams progress.")
            if(handInItem)
                p.sendMessage("${ChatColor.GREEN}To submit an item, go in your inventory, drag the item you want to submit, and click with this item on the ${ChatColor.GOLD}Bingo Card${ChatColor.GREEN}.")
            else
                p.sendMessage("${ChatColor.GREEN}To submit an item to the card, you will need to pick it up, or drag it on to your ${ChatColor.GOLD}Bingo Card${ChatColor.GREEN}.")

        }
    }


    private fun open(p : Player){
        if(bingoGame == null) return
        bingoGame!!.applyToOwnT(p) { open(p) }
    }

    private fun generateCard(pool: Triple<Array<Material>, Array<Material>, Array<Material>>, easyRatio: Int, normalRatio: Int, hardRatio: Int, alreadyGenerated : Array<Material>, arraySize :Int = 25) : Array<Material> ? {
        val totalRatio = easyRatio + normalRatio + hardRatio
        if(totalRatio == 0) return null
        val generateHere = if(arraySize - alreadyGenerated.size < 0) 0 else arraySize - alreadyGenerated.size
        val easyAmountGiven = (easyRatio.toDouble() / totalRatio.toDouble() * generateHere).toInt()
        val hardAmountGiven = (hardRatio.toDouble() / totalRatio.toDouble() * generateHere).toInt()
        val easyAmount = if(easyAmountGiven > pool.first.size) pool.first.size else easyAmountGiven
        val hardAmount = if(hardAmountGiven > pool.third.size) pool.third.size else hardAmountGiven
        val normalAmount = generateHere - easyAmount - hardAmount
        if(normalAmount > pool.second.size) return null
        val selectedMaterials = mutableListOf<Material>()
        alreadyGenerated.shuffle()
        selectedMaterials.addAll(alreadyGenerated.take( if(arraySize < alreadyGenerated.size) arraySize else arraySize ))
        pool.first .shuffle()
        pool.second.shuffle()
        pool.third .shuffle()
        selectedMaterials.addAll(pool.first.take(easyAmount))
        selectedMaterials.addAll(pool.second.take(normalAmount))
        selectedMaterials.addAll(pool.third.take(hardAmount))
        selectedMaterials.shuffle()
        return selectedMaterials.toTypedArray()
    }

    private fun cleanUpPool(dirtyPool: Triple<Array<Material>, Array<Material>, Array<Material>>, filter : Array<Material>) : Triple<Array<Material>, Array<Material>, Array<Material>>{
        return Triple(
            dirtyPool.first.filter { item -> !filter.contains(item) }.toTypedArray(),
            dirtyPool.second.filter { item -> !filter.contains(item) }.toTypedArray(),
            dirtyPool.third.filter { item -> !filter.contains(item) }.toTypedArray(),
        )
    }

    fun stop(commander: Player){
        bingoPickupLocked = false
        if(bingoGame == null) {
            commander.sendMessage("${ChatColor.RED}there is no bingo game to be stopped")
            return
        }
        commander.sendMessage("${ChatColor.GREEN}stopped the running bingo game")

        for((_,card) in bingoGame!!.toPairList())
            card.clear()
        bingoGame!!.clear()

        if(refreshTaskID > -1){
            plugin.server.scheduler.cancelTask(refreshTaskID)
            commander.sendMessage("${ChatColor.RED}Note that stopping while in the refresh/shuffle time might have weird side-effects that i didnt bother to fix. But its nothing game breaking")
        }

        clickItem.clear()
        bingoGame = null
    }

    private fun finishTeam(team : Team){
        if(bingoGame == null) return
        bingoPickupLocked = true
        if(!seeOtherTeams){
            bingoGame!!.applyToTeams {_,card ->
                card.generateTeamItems(bingoGame!!, true)
            }
        }
        bingoGame!!.applyToAllMembers { p ->
            bingoGame!!.getT(team)?.open(p)
            p.playSound(p.location, Sound.BLOCK_END_PORTAL_SPAWN, SoundCategory.MASTER, 0.2f, 1f)
            p.playSound(p.location, Sound.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, SoundCategory.MASTER, 0.9f, 2f)
            p.playSound(p.location, Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER, 1f, 1f)
        }

    }

    private fun checkWinningCondition(team : Team) {
        if(bingoGame == null) return
        val card = bingoGame!!.getT(team) ?: return
        val completed = card.getCompletedLines()
        val sum = completed.first + completed.second + completed.third
        val finished = when(winCondition){
            "horizontal_line" -> completed.first >= 1
            "vertical_line" -> completed.second >= 1
            "diagonal_line" -> completed.third >= 1
            "1_line" -> sum >= 1
            "2_lines" -> sum >= 2
            "3_lines" -> sum >= 3
            "full_card" -> completed.first == 5
            else -> false
        }

        if(finished) finishTeam(team)
    }

    private fun checkForItem(player:Player, item : ItemStack) : Boolean{
        if(bingoGame == null || bingoPickupLocked) return false
        var value = false
        bingoGame!!.applyToTeams{ team,card ->
            if(team.containsMember(player)){
                if(card.completeItem(item.type)){
                    value = true
                    team.applyToMembers { member ->
                        member.playSound(member.location, Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.MASTER, 1f, 1f)
                        member.playSound(member.location, Sound.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.MASTER, 2f, 1f)
                        plugin.server.scheduler.runTaskLater(plugin, { _ ->
                            member.playSound(member.location, Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.MASTER, 1f, 1f)
                        }, 2L)
                    }
                    for(p in plugin.server.onlinePlayers){
                        if(seeOtherTeams)
                            p.sendMessage("${team.color}Team ${team.color.name}${ChatColor.RESET} got item: ${ChatColor.GOLD}${MASEGUtil.getRealName(item.type)}")
                        else if(team.containsMember(p))
                            p.sendMessage("${team.color}Team ${team.color.name}${ChatColor.RESET} got an item ${ChatColor.DARK_GRAY}(${MASEGUtil.getRealName(item.type)})")
                        else
                            p.sendMessage("${team.color}Team ${team.color.name}${ChatColor.RESET} got an item")
                    }

                    bingoGame!!.applyToTeams{ _,card2 ->
                        card2.generateTeamItems(bingoGame!!,seeOtherTeams )
                    }
                    checkWinningCondition(team)
                }
            }
        }
        return value
    }

    @EventHandler
    fun onPlayerPickupItem(event: PlayerPickupItemEvent) {
        if(bingoGame == null) return
        if(bingoPickupLocked) return
        if(!handInItem){
            val player = event.player
            val item = event.item.itemStack
            if(player.gameMode == GameMode.CREATIVE){
                for(p in plugin.server.onlinePlayers)
                    p.sendMessage("${ChatColor.RED}${player.name} is cheating >:(")
                return
            }
            checkForItem(player, item)
        }
    }

    @EventHandler
    fun onPlayerInventoryClick(event: InventoryClickEvent){
        if(bingoGame == null) return
        if(bingoPickupLocked) return
        val player = event.whoClicked as? Player ?: return
        val clickedItem = event.cursor ?: return
        if(clickItem.isThisItem(event.currentItem)){
            if(player.gameMode == GameMode.CREATIVE){
                for(p in plugin.server.onlinePlayers)
                    p.sendMessage("${ChatColor.RED}${player.name} is cheating >:(")
                return
            }
            val submitted = checkForItem(player, clickedItem)
            if(!submitted)
                player.sendMessage("${ChatColor.RED} whoops, this item cant be submitted")
            if(handInItem && submitted)
                clickedItem.amount  = clickedItem.amount -1
        }
    }
}