package pl.wolny.junglenokaut.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.persistence.PersistentDataType;
import pl.wolny.junglenokaut.JungleNokaut;

public class PlayerJumpEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void event(PlayerMoveEvent event){
        int i = event.getPlayer().getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER);
        if(i != 0 && i != 3){
            if(event.getFrom().getX() != event.getTo().getX() || event.getFrom().getY() != event.getTo().getY() || event.getFrom().getZ() != event.getTo().getZ()){
                event.setCancelled(true);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void event2(PlayerVelocityEvent event){
        int i = event.getPlayer().getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER);
        if(i != 0 && i != 3){
            event.setCancelled(true);
        }
    }
}
