package me.wanttobee.maseg.systems.utils.playerTracker

import me.wanttobee.maseg.systems.utils.teams.Team
import org.bukkit.entity.Player

//this took ages to make right, but I think its cool now
//trust me, you don't want to see the code of v1, it's horrible
object PlayerTrackerSystem {
    const val version = "v2.0 Player Tracker"

    private val trackers : MutableMap<Int, PlayerTracker> = mutableMapOf()
    private var trackerID = 0
    fun printListTo(sender : Player){
        if(trackers.isEmpty()){
            sender.sendMessage("§cThere are no player compasses to list")
            return
        }
        sender.sendMessage("§aPlayer Compasses List: ")
        for((id, tracker) in trackers)
            sender.sendMessage("§6$id: $tracker")
    }

   fun startTracker(target: Team, refreshTime: Int, focusClosest: Boolean, saveWorldLocation: Boolean) : Int{
       val id = trackerID++
       trackers[id] = PlayerTracker(target,refreshTime,focusClosest,saveWorldLocation)
       return id
   }


    fun giveTracker(p : Player, id:Int) : Boolean{
        if(!trackers.containsKey(id)) return false
        trackers[id]?.givePlayerItem(p)
        return true
    }
    fun stopTracking(id: Int) : Boolean{
        if(!trackers.containsKey(id)) return false
        trackers[id]?.stopTracking()
        trackers.remove(id)
        return true
    }
    fun getIDList() : MutableSet<Int>{
        return trackers.keys
    }
}


