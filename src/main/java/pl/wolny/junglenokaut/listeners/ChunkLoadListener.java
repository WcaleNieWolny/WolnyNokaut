package pl.wolny.junglenokaut.listeners;

import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import pl.wolny.junglenokaut.JungleNokaut;

import java.util.ArrayList;
import java.util.List;

public class ChunkLoadListener implements Listener {

  @EventHandler(priority = EventPriority.HIGH)
  public void onChunkLoad(org.bukkit.event.world.ChunkLoadEvent event) {
    Chunk chunk = event.getChunk();
    List<Player> players = new ArrayList<>();

    for (Entity entity : chunk.getEntities()) {
      if (entity instanceof Player) {
        players.add((Player) entity);
      }
    }

    if (players.size() < 1) {
      return;
    }

    for (Player player : players) {
      if (player.getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 0) {
        players.remove(player);
      }
    }

    for (Player player : players) {
      //GenerateFakePlayer.gen(ListPlayer, ListPlayer, ListPlayer.getLocation());
    }
  }
}
