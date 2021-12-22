package pl.wolny.wolnynokaut.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pl.wolny.wolnynokaut.knocked.KnockedCache
import pl.wolny.wolnynokaut.transfer.TransferController
import pl.wolny.wolnynokaut.utils.ComponentUtils
import pl.wolny.wolnynokaut.utils.sendLegacyMessage

class PickUpCommand(
    private val transferController: TransferController,
    private val cache: KnockedCache,
    private val notAllowed: String,
    private val noPlayerAsArgument: String,
    private val playerOffline: String,
    private val playerToFar: String,
    private val playerNotKnocked: String,
    private val pickedSucessfull: String
    ): CommandExecutor {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(ComponentUtils.format(notAllowed))
            return true
        }
        if(cache.getPlayersWithDriver(sender).isNotEmpty()){
            sender.sendMessage(ComponentUtils.format(notAllowed))
            return true
        }
        if(args.isEmpty()){
            sender.sendLegacyMessage(noPlayerAsArgument)
            return true
        }
        val player = Bukkit.getPlayer(args[0])
        if(player == null){
            sender.sendLegacyMessage(playerOffline)
            return true
        }
        val knockedPlayer = cache[player]
        if(knockedPlayer == null){
            sender.sendLegacyMessage(playerNotKnocked)
            return true
        }
        if(player.location.distance(sender.location) >= 1.25){
            sender.sendLegacyMessage(playerToFar)
            return true
        }
        sender.sendLegacyMessage(pickedSucessfull.replace("{PLAYER}", player.name))
        transferController.pickUp(Bukkit.getPlayer(args[0])!!, sender)
        return true
    }
}