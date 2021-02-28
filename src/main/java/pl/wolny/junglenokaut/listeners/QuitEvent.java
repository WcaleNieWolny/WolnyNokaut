package pl.wolny.junglenokaut.listeners;

import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import pl.wolny.junglenokaut.JungleNokaut;

public class QuitEvent implements Listener {
    @EventHandler
    public void event(PlayerQuitEvent event){
        Player p = event.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        if(data.get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 0){
            p.setGameMode(GameMode.SURVIVAL);
            p.setWalkSpeed(0.2f);
            p.setHealth(0);
            p.setInvisible(false);
            p.removePotionEffect(PotionEffectType.BLINDNESS);
            data.set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 0);
            //System.out.println("quit " + event.getPlayer().getName());
            return;
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void event2(PlayerKickEvent event){
        if(event.getReason() == "Cannot interact with self!"){
            event.setCancelled(true);
            return;
        }
        Player p = event.getPlayer();
        PersistentDataContainer data = p.getPersistentDataContainer();
        if(data.get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 0){
            p.setGameMode(GameMode.SURVIVAL);
            p.setWalkSpeed(0.2f);
            p.setHealth(0);
            p.setInvisible(false);
            p.removePotionEffect(PotionEffectType.BLINDNESS);
            data.set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 0);
            //System.out.println("quit " + event.getPlayer().getName());
            return;
        }
    }
}
