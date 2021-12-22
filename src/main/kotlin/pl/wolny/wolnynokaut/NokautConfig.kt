package pl.wolny.wolnynokaut

import net.dzikoysk.cdn.entity.Description

class NokautConfig {
    @Description("Time will be knocked down. Be aware that this have time + 1 formula")
    var dedTime: Short = 60

    @Description("XP needed to heal player (per 5 ticks)")
    var healXP: Short = 5

    @Description("Time required to rescue a player")
    var treatmentTime: Short = 5

    @Description("Message for medic that is currently healing player")
    var resuscitationForHeal1: List<String> =
        listOf("&aReanimowanie.", "&aReanimowanie..", "&aReanimowanie...")
    var resuscitationForHeal2: List<String> =
        listOf("&c|", "&c/", "&c-", "&c\\", "&c|", "&c/", "&c-", "&c\\")

    @Description("Message for player that is trying to commit harakiri")
    var harakiriDisallow = "&cŻycie jest piękne! Dlaczego chcesz popełnić samobójstwo?"
    var harakiriPermit = "&aHara-kiri popełnione. Miłego dnia."

    @Description("default message if something is not allowed")
    var notAllowed = "&cNie możesz tego zrobić!"

    var noPlayerAsArgument = "&cNie podano gracza jako argument!"
    var playerOffline = "&cTen gracz jest offline"
    var playerToFar = "&cTen gracz jest za daleko!"
    var playerNotKnocked = "&cTen gracz nie jest powalony!"
    @Description("Placeholders: {PLAYER}")
    var pickedSucessfull = "&aPodniosłem {PLAYER}!"
    var noPlayerToDrop = "&cNie masz kogo zrzucić!"

}