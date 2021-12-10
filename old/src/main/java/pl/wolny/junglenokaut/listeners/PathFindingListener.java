package pl.wolny.junglenokaut.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import pl.wolny.junglenokaut.JungleNokaut;

public class PathFindingListener implements Listener {
  @EventHandler(priority = EventPriority.HIGH)
  public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
    LivingEntity target = event.getTarget();

    if (!(target instanceof Player)) {
      return;
    }

    Player player = (Player) target;

    if (player.getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) == 0) {
      return;
    }

    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    Entity damager = event.getDamager();
    Entity entity = event.getEntity();

    if (!(damager instanceof Monster && entity instanceof Player)) {
      return;
    }

    Player player = (Player) entity;

    if (player.getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) == 0) {
      return;
    }

    ((Monster) damager).setTarget(null);
  }
}
