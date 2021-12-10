package pl.wolny.junglenokaut.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataType;
import pl.wolny.junglenokaut.JungleNokaut;

public class DamageListener implements Listener {

  public class DamageEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDamage(EntityDamageEvent e) {
      if (e.getEntity() instanceof Player) {
        Player p = (Player) e.getEntity();
        if (p.getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 0) {
          if (!(e.getCause().equals(EntityDamageEvent.DamageCause.DROWNING) || e.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION))) {
            e.setCancelled(true);
            return;
          }

        }
      }
    }
  }
}
