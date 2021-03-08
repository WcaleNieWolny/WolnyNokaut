package pl.wolny.junglenokaut.listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pl.wolny.junglenokaut.JungleNokaut;
import pl.wolny.junglenokaut.utilities.DropPlayer;

public class PlayerQuitListener implements Listener {

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();

    if (player.getPassengers().size() == 0){
      return;
    }

    if (!(player.getPassengers().get(0) instanceof Player)) {
      return;
    }

    Player toDrop = (Player) player.getPassengers().get(0);
    PersistentDataContainer dataContainer = toDrop.getPersistentDataContainer();

    if (dataContainer.get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 3) {
      return;
    }

    DropPlayer.drop(toDrop, player);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerKick(PlayerKickEvent event) {
    Player player = event.getPlayer();

    if (player.getPassengers().size() == 0) {
      return;
    }

    if (!(player.getPassengers().get(0) instanceof Player)) {
      return;
    }

    Player toDrop = (Player) player.getPassengers().get(0);
    PersistentDataContainer dataContainer = toDrop.getPersistentDataContainer();

    if (dataContainer.get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 3) {
      return;
    }

    DropPlayer.drop(toDrop, player);
  }
}
