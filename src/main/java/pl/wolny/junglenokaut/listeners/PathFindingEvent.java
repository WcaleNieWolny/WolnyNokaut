package pl.wolny.junglenokaut.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import pl.wolny.junglenokaut.JungleNokaut;

import java.util.Objects;

public class PathFindingEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void target(EntityTargetLivingEntityEvent event){
        if(event.getTarget() instanceof Player){
            Player target = (Player) event.getTarget();
            if(target.getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 0){
                event.setCancelled(true);
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGH)
    public void event2(EntityDamageByEntityEvent event){
        if(event.getDamager() instanceof Monster && event.getEntity() instanceof Player){
            Player p = (Player) event.getEntity();
            if (p.getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 0) {
                ((Monster) event.getDamager()).setTarget(null);
            }
        }
    }
}
