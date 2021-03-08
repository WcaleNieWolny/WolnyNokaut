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

  @EventHandler(priority = EventPriority.HIGH)
  public void onEntityDamge(EntityDamageEvent event) {
    Entity entity = event.getEntity();

    if (!(entity instanceof Player)) {
      return;
    }

    if (entity.getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) == 0) {
      return;
    }

    EntityDamageEvent.DamageCause damageCause = event.getCause();

    if (damageCause == EntityDamageEvent.DamageCause.DROWNING || damageCause == EntityDamageEvent.DamageCause.SUFFOCATION) {
      return;
    }

    event.setCancelled(true);
  }
}
