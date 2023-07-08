package me.wanttobee.maseg.systems.utils.teams

import me.wanttobee.maseg.MASEGPlugin
import org.bukkit.ChatColor
import org.bukkit.conversations.BooleanPrompt
import org.bukkit.entity.Player

class Team(private val color: ChatColor) {
    constructor(colorID : Int) : this(
        arrayOf(ChatColor.BLUE, ChatColor.RED, ChatColor.GREEN, ChatColor.YELLOW, ChatColor.LIGHT_PURPLE, ChatColor.GOLD, ChatColor.AQUA, ChatColor.DARK_GREEN, ChatColor.DARK_PURPLE, ChatColor.DARK_AQUA)
            [colorID%10])
    private val members : MutableList<Player> = mutableListOf()
    private val observers : MutableList<ITeamObserver> = mutableListOf()

    fun subscribe(ob : ITeamObserver){
        if(!observers.contains(ob))
            observers.add(ob)
    }
    fun unsubscribe(ob : ITeamObserver) : Boolean{
        return observers.remove(ob)
    }

    fun getColor() : ChatColor{
        return color
    }
    fun getMembers() : List<Player>{
        return members.toList()
    }
    fun addMember(players:Collection<Player>){
        for(p in players)
            this.addMember(p)
    }
    fun addMember(p : Player){
        if(members.contains(p)) return
        members.add(p)
        p.setDisplayName("$color${p.name}${ChatColor.RESET}")
        for(ob in observers.toList()) //its already a list, but the toList creates a clone, so in the can modify the observer list without messing with this current loop
            ob.onAddMember(p)
    }
    fun removeMember(p: Player) : Boolean{
        val done = members.remove(p)
        if(done){
            p.setDisplayName(p.name)//resetting the name (so any color gets removed)
            for(ob in observers.toList())
                ob.onRemoveMember(p)
        }
        return done
    }
    fun containsMember(p : Player) : Boolean{
        return members.contains(p)
    }
    fun clearTeam() {
        members.forEach { member ->
            member.setDisplayName(member.name)
        }
        members.clear()
        for(ob in observers)
            ob.onTeamClear()
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
