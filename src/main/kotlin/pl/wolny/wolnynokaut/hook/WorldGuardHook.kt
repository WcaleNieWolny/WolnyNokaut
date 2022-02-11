package pl.wolny.wolnynokaut.hook

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.util.Location
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.flags.StateFlag
import com.sk89q.worldguard.protection.regions.RegionQuery
import org.bukkit.Bukkit
import org.bukkit.entity.Player


class WorldGuardHook {

    companion object{
        var flag: StateFlag? = null
    }

    private var worldGuard: WorldGuard? = null
    private var worldGuardPlugin: WorldGuardPlugin? = null
    private var accessible = false
    private val plugin = Bukkit.getPluginManager().getPlugin("WorldGuard")

    fun init(){
        if(plugin == null){
            return
        }
        if(!plugin.isEnabled){
            return
        }
        worldGuard = WorldGuard.getInstance()
        worldGuardPlugin = WorldGuardPlugin.inst()
        accessible = true
        registerFlag()
    }

    fun accessible(): Boolean{
        if(!plugin!!.isEnabled){
            return false
        }
        return accessible
    }

    fun allowKnockout(player: Player): Boolean{
        if(!accessible()){
            return true
        }
        val localPlayer = worldGuardPlugin?.wrapPlayer(player)
        val query: RegionQuery = worldGuard!!.platform.regionContainer.createQuery()
        val set = query.getApplicableRegions(BukkitAdapter.adapt(player.location))
        return !set.testState(localPlayer, flag)
    }

    private fun registerFlag(){
        val flag = StateFlag("disable-nokaut", false)
        worldGuard?.flagRegistry?.register(flag)
        WorldGuardHook.flag = flag
    }
}