package me.wanttobee.maseg.systems.utils.teams

import me.wanttobee.maseg.MASEGPlugin
import org.bukkit.ChatColor
import org.bukkit.entity.Player

class Team(teamColor : ChatColor) {
    constructor(colorID : Int) : this(
        arrayOf(ChatColor.BLUE, ChatColor.RED, ChatColor.GREEN, ChatColor.YELLOW, ChatColor.LIGHT_PURPLE, ChatColor.GOLD, ChatColor.AQUA, ChatColor.DARK_GREEN, ChatColor.DARK_PURPLE, ChatColor.DARK_AQUA)
            [colorID%10])
    private val plugin = MASEGPlugin.instance
    private val members : MutableList<Player> = mutableListOf()
    private val observers : MutableList<ITeamObserver> = mutableListOf()

    var leaveTeamOnQuit = false
        private set
    var color : ChatColor = ChatColor.WHITE
        private set
    var leaveTeamOnDeath = false
        private set

    fun setLeaveTeamOnQuit(value : Boolean) : Team{
        this.leaveTeamOnQuit = value
        return this
    }
    fun setLeaveTeamOnDeath(value : Boolean) : Team{
        this.leaveTeamOnDeath = value
        return this
    }
    fun setColor(value : ChatColor) : Team{
        color = value
        return this
    }

    init{
        this.color = teamColor
        TeamSystem.addTeam(this)
    }
    fun subscribe(ob : ITeamObserver){
        if(!observers.contains(ob))
            observers.add(ob)
    }
    fun unsubscribe(ob : ITeamObserver) : Boolean{
        return observers.remove(ob)
    }


    fun getMembers() : List<Player>{
        return members.toList()
    }

    fun addMember(players:Collection<Player>){
        for(p in players)
            this.addMember(p)
    }
    fun swapPlayer(leave:Player,enter:Player){
        if(!members.contains(leave) || members.contains(enter)) return
        plugin.logger.info("swap begin")
        members.remove(leave)
        members.add(enter)

        for(ob in observers.toList())
            ob.onSwapMember(leave, enter)
    }
    fun addMember(p : Player){
        if(members.contains(p)) return
        members.add(p)
        //p.setDisplayName("$color${p.name}${ChatColor.RESET}")
        for(ob in observers.toList()) //it's already a list, but the toList creates a clone, so in the can modify the observer list without messing with this current loop
            ob.onAddMember(p)
    }
    fun removeMember(p: Player) : Boolean{
        val done = members.remove(p)
        if(done){
            //p.setDisplayName(p.name)//resetting the name (so any color gets removed)
            for(ob in observers.toList())
                ob.onRemoveMember(p)
        }
        return done
    }
    fun containsMember(p : Player) : Boolean{
        return members.contains(p)
    }
    fun clear() {
        members.forEach { member ->
            member.setDisplayName(member.name)
        }
        members.clear()
        for(ob in observers)
            ob.onTeamClear()
        TeamSystem.removeTeam(this)
    }

    fun applyToMembers( thing : (Player) -> Unit ){
        for(member in members){
            thing.invoke(member)
        }
    }
    override fun toString(): String {
        var text ="Team $color${color.name.lowercase()}${ChatColor.RESET}: "
        for(member in members){
            text += member.name
            if(member != members.last()){
                text += ", "
            }
        }
        return text
    }
}
