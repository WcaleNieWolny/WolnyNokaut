package pl.wolny.wolnynokaut.transfer

import org.bukkit.entity.Player
import pl.wolny.wolnynokaut.knocked.KnockedCache
import pl.wolny.wolnynokaut.knocked.KnockedController
import pl.wolny.wolnynokaut.knocked.KnockedPlayer
import pl.wolny.wolnynokaut.knocked.KnockedState
import pl.wolny.wolnynokaut.limbo.LimboController
import pl.wolny.wolnynokaut.utils.sendLegacyMessage

class TransferController(private val limboController: LimboController, private val cache: KnockedCache, private val knockedController: KnockedController) {
    @Throws(java.lang.IllegalStateException::class)
    fun pickUp(player: Player, driver: Player){
        val knockedPlayer = cache[player]
        if(knockedPlayer == null){
            driver.sendLegacyMessage("KnockedPlayer ${player.name} does not exist")
            return
        }
        if(knockedPlayer.state == KnockedState.PLAYER_HEAD){
            driver.sendLegacyMessage("KnockedPlayer ${player.name} is being transferred")
            return
        }
        if(knockedPlayer.isTreated){
            driver.sendLegacyMessage("You cannot move a player that is being healed!")
            return
        }
        if(driver.passengers.contains(player)){throw IllegalStateException("KnockedPlayer ${player.name} is not working. Create issue on github.")}
        knockedPlayer.state = KnockedState.PLAYER_HEAD
        knockedPlayer.driver = player
        limboController.positionList.remove(player)
        limboController.metaList.remove(player)
        limboController.standUp(player)
        limboController.updatePlayerSlot(player, 8)
        knockedController.stopInternalTimers(true, knockedPlayer)
        driver.addPassenger(player)
    }
    @Throws(java.lang.IllegalStateException::class)
    fun placeOnGround(knockedPlayer: KnockedPlayer){
        val player = knockedPlayer.player
        if(knockedPlayer.state == KnockedState.GROUND){throw IllegalStateException("KnockedPlayer ${player.name} is grounded")}
        if(knockedPlayer.driver == null){throw IllegalStateException("KnockedPlayer ${player.name} is not working. Create issue on github.")}
        knockedPlayer.driver?.removePassenger(player)
        knockedPlayer.state = KnockedState.GROUND
        knockedPlayer.driver = null
        limboController.metaList.add(player)
        limboController.positionList.add(player)
        limboController.updatePlayerSlot(player, 0)
        knockedController.startInternalTimers(knockedPlayer)
        limboController.forceGround(player)
    }
}