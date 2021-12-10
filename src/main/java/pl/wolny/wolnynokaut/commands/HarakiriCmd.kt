package pl.wolny.wolnynokaut.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pl.wolny.wolnynokaut.NokautConfig
import pl.wolny.wolnynokaut.knocked.KnockedCache
import pl.wolny.wolnynokaut.utils.ComponentUtils

class HarakiriCmd(val cache: KnockedCache, val config: NokautConfig) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if(sender !is Player){
            sender.sendMessage(ComponentUtils.format(config.notAllowed))
            return false
        }
        if(cache[sender.uniqueId] == null){
            sender.sendMessage(ComponentUtils.format(config.harakiriDisallow))
            return false
        }
        sender.sendMessage(ComponentUtils.format(config.harakiriPermit))
        cache[sender.uniqueId] ?.killAndStop()
        return false
    }
}