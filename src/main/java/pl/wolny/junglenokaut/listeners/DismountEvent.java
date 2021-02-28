package pl.wolny.junglenokaut.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataType;
import org.spigotmc.event.entity.EntityDismountEvent;
import pl.wolny.junglenokaut.JungleNokaut;

public class DismountEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void event(EntityDismountEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            int i = p.getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER);
            if (i == 3) {
                e.setCancelled(true);
                System.out.println(p.getName() + " cancel");
            }
        }
    }
}
