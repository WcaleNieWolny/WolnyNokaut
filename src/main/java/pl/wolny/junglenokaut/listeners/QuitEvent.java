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
import pl.wolny.junglenokaut.utilities.DropPlayer;

public class QuitEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void event(PlayerQuitEvent event){
        Player p = event.getPlayer();
        if(p.getVehicle() != null){
            if(p.getVehicle() instanceof Player){
                ((Player) p.getVehicle()).removePotionEffect(PotionEffectType.SLOW);
                DropPlayer.drop(p, ((Player) p.getVehicle()));
                return;
            }
        }
        PersistentDataContainer data = p.getPersistentDataContainer();
        p.removePotionEffect(PotionEffectType.SLOW);
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
        p.removePotionEffect(PotionEffectType.SLOW);
        if(p.getVehicle() != null){
            if(p.getVehicle() instanceof Player){
                ((Player) p.getVehicle()).removePotionEffect(PotionEffectType.SLOW);
            }
        }
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