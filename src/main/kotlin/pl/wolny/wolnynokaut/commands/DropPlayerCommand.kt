package pl.wolny.wolnynokaut.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import pl.wolny.wolnynokaut.knocked.KnockedCache
import pl.wolny.wolnynokaut.transfer.TransferController
import pl.wolny.wolnynokaut.utils.ComponentUtils

class DropPlayerCommand(
    private val transferController: TransferController,
    private val cache: KnockedCache,
    private val notAllowed: String,
    private val noPlayerToDrop: String
): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage(ComponentUtils.format(notAllowed))
            return true
        }
        if(sender.passengers.isEmpty()){
            sender.sendMessage(ComponentUtils.format(noPlayerToDrop))
            return true
        }
        val passenger = sender.passengers[0]
        if(passenger !is Player){
            sender.sendMessage(ComponentUtils.format(noPlayerToDrop))
            return true
        }
        val knockedPlayer = cache.knockedPlayers[passenger.uniqueId]
        if(knockedPlayer == null){
            sender.sendMessage(ComponentUtils.format(noPlayerToDrop))
            return true
        }
        transferController.placeOnGround(knockedPlayer)
        return true
    }
}