package pl.wolny.wolnynokaut.knocked

import java.util.*

class KnockedCache() {
    lateinit var factory: KnockedFactory
    operator fun get(uniqueId: UUID): KnockedPlayer? {
        return knockedPlayers[uniqueId]
    }

    val knockedPlayers = mutableMapOf<UUID, KnockedPlayer>()
}