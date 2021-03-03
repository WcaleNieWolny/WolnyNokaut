package pl.wolny.junglenokaut.cmds;

import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.EnumGamemode;
import net.minecraft.server.v1_16_R3.PacketPlayOutGameStateChange;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pl.wolny.junglenokaut.JungleNokaut;
import pl.wolny.junglenokaut.utilities.ShowPlayer;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class PodniesGracza implements CommandExecutor {

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
        if(executor.getPassengers().size() != 0){
            executor.sendMessage(ChatColor.translateAlternateColorCodes('&', JungleNokaut.getMain().getConfig().getString("CanNotDoThat")));
        }
        //PersistentDataContainer ExecutorData = executor.getPersistentDataContainer();
        List<Entity> nearbyEntites = (List<Entity>) executor.getWorld().getNearbyEntities(executor.getLocation(), 1, 3, 1);
        if(nearbyEntites.size() == 0){
            executor.sendMessage(ChatColor.translateAlternateColorCodes('&', JungleNokaut.getMain().getConfig().getString("CanNotPickupYourSelf")));
            return false;
        }
        List<Player> players = new ArrayList<>();
        for (Entity entity: nearbyEntites) {
            if(entity instanceof Player){
                players.add((Player) entity);
            }
        }
        players.removeIf(ent -> ent.getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 1);
        if(players.contains(executor)){
            players.remove(executor);
        }
        if(players.size() != 1){
            executor.sendMessage(ChatColor.translateAlternateColorCodes('&', JungleNokaut.getMain().getConfig().getString("CanNotPickupYourSelf")));
            return false;
        }
        Player knocked = players.get(0);
        PersistentDataContainer KnockedData = knocked.getPersistentDataContainer();
        KnockedData.set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 3);
        executor.sendMessage(ChatColor.translateAlternateColorCodes('&', JungleNokaut.getMain().getConfig().getString("PickupSuckess").replace("%USER%", players.get(0).getName())));
        executor.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0));
        EntityPlayer KnockedEntity = ((CraftPlayer) knocked).getHandle();
        knocked.setGameMode(GameMode.SPECTATOR);
        knocked.setSpectatorTarget(executor);
        KnockedEntity.playerInteractManager.setGameMode(EnumGamemode.ADVENTURE);
        //sendGameState(knocked, executor);
        String PickupForUser = JungleNokaut.getMain().getConfig().getString("PickupForUser");
        new BukkitRunnable()
        {
            public void run()
            {
                if(KnockedData.get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 3){
                    this.cancel();
                    return;
                }
                knocked.sendTitle(ChatColor.translateAlternateColorCodes('&', PickupForUser), "", 0, 11, 0);
            }
        }.runTaskTimer(JungleNokaut.getMain(), 10, 10);
//        new BukkitRunnable()
//        {
//            public void run()
//            {
//                if(KnockedData.get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 3){
//                    this.cancel();
//                    return;
//                }
//                if(!executor.isOnline()){
//                    this.cancel();
//                    return;
//                }
//                knocked.teleport(executor.getLocation().add(0, 1, 0));
//
//            }
//        }.runTaskTimer(JungleNokaut.getMain(), 3, 3);
        executor.addPassenger(knocked);
        return true;

    }
    public void sendGameState(Player player, Player player2) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        EntityPlayer entityPlayer = craftPlayer.getHandle();
        entityPlayer.playerInteractManager.setGameMode(EnumGamemode.SPECTATOR);
        player.setSpectatorTarget(player2);
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_GAME_MODE, entityPlayer);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

    }
}
