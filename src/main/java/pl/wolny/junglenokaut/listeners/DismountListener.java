
package pl.wolny.junglenokaut.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.spigotmc.event.entity.EntityDismountEvent;
import pl.wolny.junglenokaut.JungleNokaut;

public class DismountListener implements Listener {

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onEntityDismount(EntityDismountEvent event) {
    Entity entity = event.getEntity();

    if (!(entity instanceof Player)) {
      return;
    }

    Player player = (Player) entity;

    if (player.getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 3) {
      return;
    }

    event.setCancelled(true);
    System.out.println(player.getName() + " cancel");
  }
}