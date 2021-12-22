package pl.wolny.junglenokaut.utilities;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import pl.wolny.junglenokaut.JungleNokaut;

import java.util.ArrayList;
import java.util.List;

public class HidePlayer {
    public static void hide(Player player) {
        List<Player> list = new ArrayList<>(Bukkit.getOnlinePlayers());
        list.remove(player);
        for (Player ListPlayer : list) {
            ListPlayer.hidePlayer(JungleNokaut.getMain(), player);
        }
    }
}
