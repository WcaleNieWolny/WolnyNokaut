package pl.wolny.junglenokaut.listeners;

import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.persistence.PersistentDataType;
import pl.wolny.junglenokaut.JungleNokaut;
import pl.wolny.junglenokaut.utilities.GenerateFakePlayer;

import java.util.ArrayList;
import java.util.List;

public class ChunkLoadEvent implements Listener {
    @EventHandler
    public void event(org.bukkit.event.world.ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();
        List<Player> players = new ArrayList<>();
        for (Entity ent: chunk.getEntities()) {
            if(ent instanceof Player){
                players.add((Player) ent);
            }
        }
        if(players.size() <1){return;}
        for (Player ListPlayer: players) {
            if(ListPlayer.getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) == 0){
                players.remove(ListPlayer);
            }
        }
        for (Player ListPlayer: players) {
            //GenerateFakePlayer.gen(ListPlayer, ListPlayer, ListPlayer.getLocation());
        }
    }
}
