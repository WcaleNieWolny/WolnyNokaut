package pl.wolny.junglenokaut.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pl.wolny.junglenokaut.JungleNokaut;
import pl.wolny.junglenokaut.utilities.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DeathEvent implements Listener {
    @EventHandler
    public void event(PlayerDeathEvent event){
        Player p = event.getEntity();
        PersistentDataContainer data = event.getEntity().getPersistentDataContainer();
        if(data.get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 0){
            p.setGameMode(GameMode.SURVIVAL);
            p.setWalkSpeed(0.2f);
            data.set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 0);
            ShowPlayer.show(event.getEntity());
            p.setInvisible(false);
            p.setAllowFlight(false);
            return;
        }
        for(Player pl: Bukkit.getOnlinePlayers()){
            GenerateFakePlayer object = new GenerateFakePlayer();
            object.gen(p, pl, p.getLocation());
        }
        event.setCancelled(true);
        p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1));
        p.setAllowFlight(true);
        p.setGameMode(GameMode.ADVENTURE);
        p.setWalkSpeed(0);
        HidePlayer.hide(event.getEntity());
        p.setInvisible(true);
        data.set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 1);
        final int[] TitleStatus = {JungleNokaut.getMain().getConfig().getInt("NocCooldown")};
        new BukkitRunnable()
       {
            public void run()
            {
                if(data.get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 1){
                    data.set(new NamespacedKey(JungleNokaut.getMain(), "NokInt"), PersistentDataType.INTEGER, TitleStatus[0]);
                    this.cancel();
                    return;
                }
                if(TitleStatus[0] == 0){
                    p.setGameMode(GameMode.SURVIVAL);
                    p.setWalkSpeed(0.2f);
                    p.setHealth(0);
                    data.set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 0);
                    ShowPlayer.show(event.getEntity());
                    p.setInvisible(false);
                    this.cancel();
                    return;
                }
                p.sendTitle(ChatColor.translateAlternateColorCodes('&', "&a&lJesteś powalony!"), ChatColor.translateAlternateColorCodes('&', "&cPozostało: " + TitleStatus[0]), 0, 20, 0);
                TitleStatus[0]--;

            }
        }.runTaskTimer(JungleNokaut.getMain(), 20, 20);
    }

}
