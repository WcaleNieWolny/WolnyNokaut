package pl.wolny.junglenokaut.listeners;

import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import pl.wolny.junglenokaut.JungleNokaut;
import pl.wolny.junglenokaut.utilities.GenerateFakePlayer;

import java.util.ArrayList;
import java.util.List;

public class JoinEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGH)
    public void event(PlayerJoinEvent event){
        Player p = event.getPlayer();
        //System.out.println(p.getWalkSpeed());
        PersistentDataContainer data = event.getPlayer().getPersistentDataContainer();
        if(!data.has(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER)){
            data.set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 0);
        }
        if(!data.has(new NamespacedKey(JungleNokaut.getMain(), "NokInt"), PersistentDataType.INTEGER)){
            data.set(new NamespacedKey(JungleNokaut.getMain(), "NokInt"), PersistentDataType.INTEGER, 0);
        }
        if(!data.has(new NamespacedKey(JungleNokaut.getMain(), "NokPodnoszenie"), PersistentDataType.INTEGER)){
            data.set(new NamespacedKey(JungleNokaut.getMain(), "NokPodnoszenie"), PersistentDataType.INTEGER, 0);
        }
        List<Player> list = new ArrayList<>(Bukkit.getOnlinePlayers());
        list.remove(event.getPlayer());
        for (Player ListPlayer: list) {
            if(ListPlayer.getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) == 0){return;}
            new BukkitRunnable()
            {
                public void run()
                {
                    GenerateFakePlayer object = new GenerateFakePlayer();
                    object.gen(ListPlayer, event.getPlayer(), event.getPlayer().getLocation());
                    event.getPlayer().hidePlayer(JungleNokaut.getMain(), ListPlayer);
                }
            }.runTaskLater(JungleNokaut.getMain(), 10);
        }
    }
}
