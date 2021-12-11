package pl.wolny.wolnynokaut

import net.dzikoysk.cdn.entity.Description

class NokautConfig {
    @Description("Time will be knocked down. Be aware that this have time + 1 formula")
    var dedTime = 60
    @Description("XP needed to heal player (per 5 ticks)")
    var healXP = 5
    @Description("Time required to rescue a player")
    var treatmentTime = 5
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

}