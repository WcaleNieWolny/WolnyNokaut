package pl.wolny.junglenokaut.utilities;

import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EnumGamemode;
import net.minecraft.server.v1_16_R3.PacketPlayOutGameStateChange;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import pl.wolny.junglenokaut.JungleNokaut;

public class DropPlayer {
    public static void drop(Player toDrop, Player root){
        PersistentDataContainer data = toDrop.getPersistentDataContainer();
        data.set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 10);
        root.removePassenger(root.getPassengers().get(0));
        toDrop.setGameMode(GameMode.SPECTATOR);
        toDrop.setSpectatorTarget(null);
        toDrop.setGameMode(GameMode.ADVENTURE);
        EntityPlayer KnockedEntity = ((CraftPlayer) toDrop).getHandle();
        KnockedEntity.playerInteractManager.setGameMode(EnumGamemode.ADVENTURE);
        KnockedEntity.playerConnection.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.d, 0));
        //toDrop.teleport(toDrop.getLocation().subtract(0, 1, 0));
        toDrop.teleport(root.getLocation());
        data.set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 4);
        int[] TitleStatus = {data.get(new NamespacedKey(JungleNokaut.getMain(), "NokInt"), PersistentDataType.INTEGER)};
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
                    toDrop.setGameMode(GameMode.SURVIVAL);
                    toDrop.setWalkSpeed(0.2f);
                    toDrop.setHealth(0);
                    data.set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 0);
                    ShowPlayer.show(toDrop);
                    toDrop.setInvisible(false);
                    this.cancel();
                    return;
                }
                toDrop.sendTitle(ChatColor.translateAlternateColorCodes('&', "&a&lJesteś powalony!"), ChatColor.translateAlternateColorCodes('&', "&cPozostało: " + TitleStatus[0]), 0, 20, 0);
                TitleStatus[0]--;

            }
        }.runTaskTimer(JungleNokaut.getMain(), 20, 20);
    }
}
