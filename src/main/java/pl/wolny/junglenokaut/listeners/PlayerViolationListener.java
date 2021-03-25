package pl.wolny.junglenokaut.listeners;

import me.rerere.matrix.api.events.PlayerViolationEvent;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import pl.wolny.junglenokaut.JungleNokaut;

public class PlayerViolationListener implements Listener {
  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerViolation(PlayerViolationEvent event) {
    Player player = event.getPlayer();

    if (player.getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) == 0) {
      return;
    }

    event.setCancelled(true);
    //player.sendMessage(ChatColor.RED + "Hej! Nie możesz tego zrobić.");
  }
}
