package pl.wolny.junglenokaut.listeners;

import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import pl.wolny.junglenokaut.JungleNokaut;

public class PlayerQuitListener2 implements Listener {

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerQuit(PlayerQuitEvent event) {

    Player player = event.getPlayer();
    player.removePotionEffect(PotionEffectType.SLOW);
    checkVehicle(player);

    PersistentDataContainer data = player.getPersistentDataContainer();
    if (data.get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 0) {
      player.setGameMode(GameMode.SURVIVAL);
      player.setWalkSpeed(0.2f);
      player.setHealth(0);
      player.setInvisible(false);
      player.removePotionEffect(PotionEffectType.BLINDNESS);
      data.set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 0);
      //System.out.println("quit " + event.getPlayer().getName());
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onPlayerKick(PlayerKickEvent event) {

    if (event.getReason().equals("Cannot interact with self!")) {
      event.setCancelled(true);
      return;
    }

    Player player = event.getPlayer();
    player.removePotionEffect(PotionEffectType.SLOW);
    checkVehicle(player);

    PersistentDataContainer data = player.getPersistentDataContainer();
    if (data.get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 0) {
      player.setGameMode(GameMode.SURVIVAL);
      player.setWalkSpeed(0.2f);
      player.setHealth(0);
      player.setInvisible(false);
      player.removePotionEffect(PotionEffectType.BLINDNESS);
      data.set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 0);
      //System.out.println("quit " + event.getPlayer().getName());
    }
  }

  private void checkVehicle(Player player) {
    Entity vehicle = player.getVehicle();

    if (vehicle == null) return;
    if (!(vehicle instanceof Player)) return;

    ((Player) vehicle).removePotionEffect(PotionEffectType.SLOW);
  }
}
