package pl.wolny.junglenokaut.listeners;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityToggleSwimEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;
import pl.wolny.junglenokaut.JungleNokaut;

public class BlockEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void event(PlayerDropItemEvent event){
        if(event.getPlayer().getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 0){
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Hej! Nie możesz tego zrobić.");
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void event2(EntityDamageByEntityEvent event){
        if(!(event.getDamager() instanceof Player)){return;}
        if(event.getDamager().getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 0){
            event.setCancelled(true);
            event.getDamager().sendMessage(ChatColor.RED + "Hej! Nie możesz tego zrobić.");
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void event3(PlayerCommandPreprocessEvent event){
        if(event.getPlayer().getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) == 0){ return;}
        if(event.getMessage().contains("/akceptujsmierc") || event.getMessage().contains("/harakiri") || event.getMessage().contains("/zginodrazu")){return;}
        event.setCancelled(true);
        event.getPlayer().sendMessage(ChatColor.RED + "Hej! Nie możesz tego zrobić.");
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void event4(PlayerInteractEvent event){
        if(event.getPlayer().getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 0){
            //event.getPlayer().sendMessage(ChatColor.RED + "Hej! Nie możesz tego zrobić.");
            event.setCancelled(true);
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void event5(BlockBreakEvent event){
        if(event.getPlayer().getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 0){
            event.getPlayer().sendMessage(ChatColor.RED + "Hej! Nie możesz tego zrobić.");
            event.setCancelled(true);
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void event6(BlockPlaceEvent event){
        if(event.getPlayer().getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 0){
            event.getPlayer().sendMessage(ChatColor.RED + "Hej! Nie możesz tego zrobić.");
            event.setCancelled(true);
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void event7(EntityToggleSwimEvent event){
        if(!(event.getEntity() instanceof Player)){return;}
        if(event.getEntity().getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 0){
            event.setCancelled(true);
        }
    }
}
