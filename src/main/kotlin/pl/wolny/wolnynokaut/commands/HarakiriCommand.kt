package pl.wolny.wolnynokaut.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pl.wolny.wolnynokaut.NokautConfig
import pl.wolny.wolnynokaut.knocked.KnockedCache
import pl.wolny.wolnynokaut.knocked.KnockedController
import pl.wolny.wolnynokaut.utils.ComponentUtils
import pl.wolny.wolnynokaut.utils.sendLegacyMessage

class HarakiriCommand(private val cache: KnockedCache,
                      private val controller: KnockedController,
                      private val notAllowed: String,
                      private val harakiriDisallow: String,
                      private val harakiriPermit: String
) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(ComponentUtils.format(notAllowed))
            return true
        }else if (cache[sender.uniqueId] == null){
            sender.sendLegacyMessage(harakiriDisallow)
            return true
        }
        sender.sendLegacyMessage(harakiriPermit)
        controller.killAndStop(cache[sender.uniqueId]!!)
        return true
    }
}