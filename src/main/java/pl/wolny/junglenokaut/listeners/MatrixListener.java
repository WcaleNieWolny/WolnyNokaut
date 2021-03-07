package pl.wolny.junglenokaut.listeners;

import me.rerere.matrix.api.events.PlayerViolationEvent;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.persistence.PersistentDataType;
import pl.wolny.junglenokaut.JungleNokaut;

public class MatrixListener implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void event(PlayerViolationEvent event){
        if(event.getPlayer().getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 0){
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Hej! Nie możesz tego zrobić.");
        }
    }
}
