package pl.wolny.junglenokaut.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import pl.wolny.junglenokaut.JungleNokaut;
import pl.wolny.junglenokaut.utilities.GenerateFakePlayer;

import java.util.ArrayList;
import java.util.List;

public class PlayerJoinListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PersistentDataContainer data = event.getPlayer().getPersistentDataContainer();

        NamespacedKey nokStatus = new NamespacedKey(JungleNokaut.getMain(), "NokStatus");
        NamespacedKey nokInt = new NamespacedKey(JungleNokaut.getMain(), "NokInt");
        NamespacedKey nokPodnoszenie = new NamespacedKey(JungleNokaut.getMain(), "NokPodnoszenie");

        setIfNotExist(data, nokStatus);
        setIfNotExist(data, nokInt);
        setIfNotExist(data, nokPodnoszenie);

        if (!(data.get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) == 0)) {
            player.setGameMode(GameMode.SURVIVAL);
            player.setWalkSpeed(0.2f);
            player.setInvisible(false);
            player.removePotionEffect(PotionEffectType.BLINDNESS);
            player.setHealth(0);
            data.set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 0);
            System.out.println("TRUE FOR " + player.getName());
            return;
        }

        List<Player> list = new ArrayList<>(Bukkit.getOnlinePlayers());
        list.remove(player);

        list.forEach(p -> {
            if (p.getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) == 0)
                return;

            Bukkit.getScheduler().runTaskLater(JungleNokaut.getMain(), () -> {
                GenerateFakePlayer object = new GenerateFakePlayer();
                object.gen(p, player, player.getLocation());
                player.hidePlayer(JungleNokaut.getMain(), p);
            }, 10);
        });
    }

    private void setIfNotExist(PersistentDataContainer data, NamespacedKey key) {
        if (!data.has(key, PersistentDataType.INTEGER)) data.set(key, PersistentDataType.INTEGER, 0);
    }
}
