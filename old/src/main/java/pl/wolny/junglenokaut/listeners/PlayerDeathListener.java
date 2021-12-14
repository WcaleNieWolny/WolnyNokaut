package pl.wolny.junglenokaut.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pl.wolny.junglenokaut.JungleNokaut;
import pl.wolny.junglenokaut.utilities.*;

public class PlayerDeathListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        PersistentDataContainer data = event.getEntity().getPersistentDataContainer();

        if (player.getPassengers().size() != 0) {
            return;
        }
        if (player.getWorld().getName().contains("end") && 0 > player.getLocation().getY()) {
            return;
        }

        if (data.get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 0) {
            player.setGameMode(GameMode.SURVIVAL);
            player.setWalkSpeed(0.2f);
            data.set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 0);
            ShowPlayer.show(event.getEntity());
            player.setInvisible(false);
            player.setAllowFlight(false);
            return;
        }

        event.setCancelled(true);

        for (Player p : Bukkit.getOnlinePlayers()) {
            GenerateFakePlayer object = new GenerateFakePlayer();
            object.gen(player, p, player.getLocation());
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1));
        player.setAllowFlight(true);
        player.setGameMode(GameMode.ADVENTURE);
        player.setWalkSpeed(0);
        HidePlayer.hide(event.getEntity());
        player.setInvisible(true);
        data.set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 1);

        final int[] TitleStatus = {JungleNokaut.getConfigData().getInt("NocCooldown")};
        String KnockedLine1 = JungleNokaut.getConfigData().getString("KnockedLine1");
        String KnockedLine2 = JungleNokaut.getConfigData().getString("KnockedLine2");

        new BukkitRunnable() {
            public void run() {
                if (data.get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 1) {
                    data.set(new NamespacedKey(JungleNokaut.getMain(), "NokInt"), PersistentDataType.INTEGER, TitleStatus[0]);
                    this.cancel();
                    return;
                }

                if (TitleStatus[0] == 0) {
                    player.setGameMode(GameMode.SURVIVAL);
                    player.setWalkSpeed(0.2f);
                    player.setHealth(0);
                    data.set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 0);
                    ShowPlayer.show(event.getEntity());
                    player.setInvisible(false);
                    this.cancel();
                    return;
                }

                player.sendTitle(ChatColor.translateAlternateColorCodes('&', KnockedLine1), ChatColor.translateAlternateColorCodes('&', KnockedLine2.replace("%TIME-1%", String.valueOf(TimeSystem.getMinute(TitleStatus[0])))).replace("%TIME-2%", TimeSystem.getSecond(TimeSystem.getMinute(TitleStatus[0]), TitleStatus[0])), 0, 20, 0);
                TitleStatus[0]--;
            }
        }.runTaskTimer(JungleNokaut.getMain(), 20, 20);
    }
}
