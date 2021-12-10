package pl.wolny.junglenokaut.listeners;

import com.google.common.collect.ImmutableSet;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityToggleSwimEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;
import pl.wolny.junglenokaut.JungleNokaut;

import java.util.Set;

public class BlockListener implements Listener {

  private static final Set<String> commands = ImmutableSet.of("/akceptujsmierc", "/harakiri", "/zginodrazu");

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerDropItem(PlayerDropItemEvent event) {
    if (!checkPersistentDataContainer(event.getPlayer())) {
      return;
    }

    event.setCancelled(true);
    event.getPlayer().sendMessage(ChatColor.RED + "Hej! Nie możesz tego zrobić.");
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    Entity damager = event.getDamager();

    if (!(damager instanceof Player)) {
      return;
    }

    if (!checkPersistentDataContainer(damager)) {
      return;
    }

    event.setCancelled(true);
    damager.sendMessage(ChatColor.RED + "Hej! Nie możesz tego zrobić.");
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
    if (checkPersistentDataContainer(event.getPlayer())) {
      return;
    }

    if (commands.contains(event.getMessage())) {
      return;
    }

    event.setCancelled(true);
    event.getPlayer().sendMessage(ChatColor.RED + "Hej! Nie możesz tego zrobić.");
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (checkPersistentDataContainer(event.getPlayer())) {
      return;
    }

    //event.getPlayer().sendMessage(ChatColor.RED + "Hej! Nie możesz tego zrobić.");
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onBlockBreak(BlockBreakEvent event) {
    if (checkPersistentDataContainer(event.getPlayer())) {
      return;
    }

    event.getPlayer().sendMessage(ChatColor.RED + "Hej! Nie możesz tego zrobić.");
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onBlockPlace(BlockPlaceEvent event) {
    if (checkPersistentDataContainer(event.getPlayer())) {
      return;
    }

    event.getPlayer().sendMessage(ChatColor.RED + "Hej! Nie możesz tego zrobić.");
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onEntityToggleSwim(EntityToggleSwimEvent event) {
    if (!(event.getEntity() instanceof Player)) {
      return;
    }

    if (checkPersistentDataContainer(event.getEntity())) {
      return;
    }

    event.setCancelled(true);
  }

  private boolean checkPersistentDataContainer(Entity player) {
    return player.getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) == 0;
  }
}
