package pl.wolny.junglenokaut.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pl.wolny.junglenokaut.JungleNokaut;
import pl.wolny.junglenokaut.utilities.*;

import java.util.ArrayList;
import java.util.List;

public class SneakEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void event(PlayerToggleSneakEvent event){
        Player player = event.getPlayer();
        if(player.getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 0){return;}
        List<Entity> nearbyEntites = (List<Entity>) player.getWorld().getNearbyEntities(player.getLocation(), 1, 3, 1);
        if(nearbyEntites.size() == 0){return;}
        List<Player> players = new ArrayList<>();
        for (Entity entity: nearbyEntites) {
            if(entity instanceof Player){
                players.add((Player) entity);
            }
        }
        players.removeIf(ent -> ent.getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 1);
        if(players.contains(player)){
            players.remove(player);
        }
        if(players.size() != 1){return;}
        int i = Integer.parseInt(JungleNokaut.getMain().getConfig().getString("HealXP"));
        if(event.getPlayer().getTotalExperience() < i){
            return;
        }
        event.getPlayer().setWalkSpeed(0f);
        PersistentDataContainer data = players.get(0).getPersistentDataContainer();
        data.set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 2);
        //data.set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 2);
        final int[] TitleStatus2 = {JungleNokaut.getMain().getConfig().getInt("HealCooldown")};
        final int[] TitleStatus3 = {1};
        new BukkitRunnable()
        {
            public void run()
            {
                if(data.get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 2|| (!(event.getPlayer().isSneaking()))){
                    data.set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 1);
                    event.getPlayer().setWalkSpeed(0.2f);
                    final int[] TitleStatus = {data.get(new NamespacedKey(JungleNokaut.getMain(), "NokInt"), PersistentDataType.INTEGER)};
                    String KnockedLine1 = JungleNokaut.getMain().getConfig().getString("KnockedLine1");
                    String KnockedLine2 = JungleNokaut.getMain().getConfig().getString("KnockedLine2");
                    new BukkitRunnable()
                    {
                        public void run()
                        {
                            if(data.get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 1){
                                data.set(new NamespacedKey(JungleNokaut.getMain(), "NokInt"), PersistentDataType.INTEGER, TitleStatus[0]);
                                this.cancel();
                                return;
                            }
                            if(TitleStatus[0] == 0){
                                players.get(0).setGameMode(GameMode.SURVIVAL);
                                players.get(0).setWalkSpeed(0.2f);
                                players.get(0).setHealth(0);
                                data.set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 0);
                                ShowPlayer.show(players.get(0));
                                players.get(0).setInvisible(false);
                                this.cancel();
                                return;
                            }
                            players.get(0).sendTitle(ChatColor.translateAlternateColorCodes('&', KnockedLine1), ChatColor.translateAlternateColorCodes('&', KnockedLine2.replace("%TIME%", String.valueOf(TitleStatus[0]))), 0, 20, 0);
                            TitleStatus[0]--;

                        }
                    }.runTaskTimer(JungleNokaut.getMain(), 20, 20);
                    this.cancel();
                    return;
                }
                if(TitleStatus2[0] == 0){
                    players.get(0).setGameMode(GameMode.SURVIVAL);
                    players.get(0).setWalkSpeed(0.2f);
                    players.get(0).getPersistentDataContainer().set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 0);
                    players.get(0).setAllowFlight(false);
                    ShowPlayer.show(players.get(0));
                    players.get(0).setInvisible(false);
                    event.getPlayer().setWalkSpeed(0.2f);
                    players.get(0).removePotionEffect(PotionEffectType.BLINDNESS);
                    this.cancel();
                    return;
                }
                if((float) i > player.getTotalExperience()){
                    event.getPlayer().setWalkSpeed(0.2f);
                    data.set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 1);
                }
                players.get(0).getPersistentDataContainer().set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 2);
                players.get(0).sendTitle(ChatColor.translateAlternateColorCodes('&', "&a&lReanimowanie!"), ChatColor.translateAlternateColorCodes('&', "&cPozostało: " + TitleStatus2[0]), 0, 20, 0);
                TitleStatus2[0]--;
            }
        }.runTaskTimer(JungleNokaut.getMain(), 20, 20);
        new BukkitRunnable() {
            public void run() {
                if(data.get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 2){
                    this.cancel();
                    return;
                }
                int i = JungleNokaut.getMain().getConfig().getInt("HealXP");
                if(event.getPlayer().getTotalExperience() < i){
                    event.getPlayer().setWalkSpeed(0.2f);
                    data.set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 1);
                    this.cancel();
                    return;
                }
                event.getPlayer().giveExp(-i);
                if(TitleStatus3[0] == 9){TitleStatus3[0] = 1;}
                switch (TitleStatus3[0]){
                    case 1:
                        event.getPlayer().sendTitle(ChatColor.translateAlternateColorCodes('&', "&c|"), ChatColor.translateAlternateColorCodes('&', "&aReanimowanie."), 0, 6, 0);
                        TitleStatus3[0]++;
                        break;
                    case 2:
                        event.getPlayer().sendTitle(ChatColor.translateAlternateColorCodes('&', "&c/"), ChatColor.translateAlternateColorCodes('&', "&aReanimowanie.."), 0, 6, 0);
                        TitleStatus3[0]++;
                        break;
                    case 3:
                        event.getPlayer().sendTitle(ChatColor.translateAlternateColorCodes('&', "&c-"), ChatColor.translateAlternateColorCodes('&', "&aReanimowanie..."), 0, 6, 0);
                        TitleStatus3[0]++;
                        break;
                    case 4:
                        event.getPlayer().sendTitle(ChatColor.translateAlternateColorCodes('&', "&c\\"), ChatColor.translateAlternateColorCodes('&', "&aReanimowanie."), 0, 6, 0);
                        TitleStatus3[0]++;
                        break;
                    case 5:
                        event.getPlayer().sendTitle(ChatColor.translateAlternateColorCodes('&', "&c|"), ChatColor.translateAlternateColorCodes('&', "&aReanimowanie.."), 0, 6, 0);
                        TitleStatus3[0]++;
                        break;
                    case 6:
                        event.getPlayer().sendTitle(ChatColor.translateAlternateColorCodes('&', "&c/"), ChatColor.translateAlternateColorCodes('&', "&aReanimowanie..."), 0, 6, 0);
                        TitleStatus3[0]++;
                        break;
                    case 7:
                        event.getPlayer().sendTitle(ChatColor.translateAlternateColorCodes('&', "&c-"), ChatColor.translateAlternateColorCodes('&', "&aReanimowanie."), 0, 6, 0);
                        TitleStatus3[0]++;
                        break;
                    case 8:
                        event.getPlayer().sendTitle(ChatColor.translateAlternateColorCodes('&', "&c\\"), ChatColor.translateAlternateColorCodes('&', "&aReanimowanie.."), 0, 6, 0);
                        TitleStatus3[0]++;
                        break;
                }
            }
        }.runTaskTimer(JungleNokaut.getMain(), 5, 5);
    }
}
