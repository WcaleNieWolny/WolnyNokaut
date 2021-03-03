package pl.wolny.junglenokaut.cmds;

import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EnumGamemode;
import net.minecraft.server.v1_16_R3.PacketPlayOutGameStateChange;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pl.wolny.junglenokaut.JungleNokaut;
import pl.wolny.junglenokaut.utilities.ShowPlayer;

public class RzucGracza implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)){
            System.out.println("Nice try :)");
            return false;
        }
        Player executor = (Player) sender;
        if(!(JungleNokaut.getMain().getConfig().getBoolean("PickupModule"))){
            executor.sendMessage(ChatColor.translateAlternateColorCodes('&', JungleNokaut.getMain().getConfig().getString("DisableCMD")));
            return false;
        }
        if(executor.getPassengers().size() != 1){
            executor.sendMessage(ChatColor.translateAlternateColorCodes('&', JungleNokaut.getMain().getConfig().getString("NoPlayerToDrop")));
            return false;
        }
        Entity toDropEnt = executor.getPassengers().get(0);
        if(!(toDropEnt instanceof  Player)) {
            executor.sendMessage(ChatColor.translateAlternateColorCodes('&', JungleNokaut.getMain().getConfig().getString("NoPlayerToDrop")));
            return false;
        }
        Player toDrop = (Player) toDropEnt;
        executor.sendMessage(ChatColor.translateAlternateColorCodes('&', JungleNokaut.getMain().getConfig().getString("SuckessDrop").replace("%USER%", toDrop.getName())));
        executor.removePotionEffect(PotionEffectType.SLOW);
        PersistentDataContainer data = toDrop.getPersistentDataContainer();
        data.set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 10);
        executor.removePassenger(executor.getPassengers().get(0));
        toDrop.setGameMode(GameMode.SPECTATOR);
        toDrop.setSpectatorTarget(null);
        toDrop.setGameMode(GameMode.ADVENTURE);
        EntityPlayer KnockedEntity = ((CraftPlayer) toDrop).getHandle();
        KnockedEntity.playerInteractManager.setGameMode(EnumGamemode.ADVENTURE);
        KnockedEntity.playerConnection.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.d, 0));
        //toDrop.teleport(toDrop.getLocation().subtract(0, 1, 0));
        toDrop.teleport(executor.getLocation());
        data.set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 4);
        int[] TitleStatus = {data.get(new NamespacedKey(JungleNokaut.getMain(), "NokInt"), PersistentDataType.INTEGER)};
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
                    toDrop.setGameMode(GameMode.SURVIVAL);
                    toDrop.setWalkSpeed(0.2f);
                    toDrop.setHealth(0);
                    data.set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 0);
                    ShowPlayer.show(toDrop);
                    toDrop.setInvisible(false);
                    this.cancel();
                    return;
                }
                toDrop.sendTitle(ChatColor.translateAlternateColorCodes('&', KnockedLine1), ChatColor.translateAlternateColorCodes('&', KnockedLine2.replace("%TIME%", String.valueOf(TitleStatus[0]))), 0, 20, 0);
                TitleStatus[0]--;

            }
        }.runTaskTimer(JungleNokaut.getMain(), 20, 20);
        return true;
    }
}
