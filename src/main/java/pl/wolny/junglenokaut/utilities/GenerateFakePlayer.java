package pl.wolny.junglenokaut.utilities;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.*;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftWitch;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pl.wolny.junglenokaut.JungleNokaut;
import sun.security.util.KnownOIDs;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class GenerateFakePlayer{
    private final AtomicBoolean running = new AtomicBoolean(false);
    private Thread worker;
    public void gen(Player knocked, Player reciver, Location l){
        GameProfile gameProfile = new GameProfile(knocked.getUniqueId(), knocked.getName());
        GameProfile playerProfile = ((CraftPlayer) knocked).getHandle().getProfile();
        Property property = playerProfile.getProperties().get("textures").iterator().next();
        reciver.sendMessage(property.getValue() + " " + property.getSignature());
        gameProfile.getProperties().putAll(((CraftPlayer) knocked).getHandle().getProfile().getProperties());
        EntityPlayer entityPlayer = new EntityPlayer(
                ((CraftServer) Bukkit.getServer()).getServer(),
                ((CraftWorld) knocked.getWorld()).getHandle(),
                gameProfile,
                new PlayerInteractManager(((CraftWorld) knocked.getWorld()).getHandle()));
        entityPlayer.setPosition(knocked.getLocation().getX(), knocked.getLocation().getY()-0.1, knocked.getLocation().getZ());
        entityPlayer.setPose(EntityPose.SWIMMING);
        PlayerConnection connection = ((CraftPlayer)reciver.getPlayer()).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
        DataWatcher watcher = entityPlayer.getDataWatcher();
        connection.sendPacket(new PacketPlayOutEntityMetadata(entityPlayer.getId(), watcher, false));
        PersistentDataContainer data = knocked.getPersistentDataContainer();
        new BukkitRunnable()
        {
            public void run()
            {
                if(data.get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) == 0){
                    this.cancel();
                }
                if(data.get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) == 4){
                    System.out.println("NOK_TEST + @2");
                    entityPlayer.setLocation(knocked.getLocation().getX(), knocked.getLocation().getY()-0.1, knocked.getLocation().getZ(), (knocked.getLocation().getYaw() * 256 / 360), (knocked.getLocation().getPitch() * 256 / 360));
                    entityPlayer.yaw = knocked.getLocation().getYaw() * 256 / 360;
                    entityPlayer.pitch = knocked.getLocation().getPitch() * 256 / 360;
                    entityPlayer.lastYaw = knocked.getLocation().getYaw() * 256 / 360;
                    entityPlayer.lastPitch = knocked.getLocation().getPitch() * 256 / 360;
                    connection.sendPacket(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte)(knocked.getLocation().getYaw() * 256 / 360)));
                    connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(entityPlayer.getId(), (byte)(knocked.getLocation().getYaw() * 256 / 360), (byte)(knocked.getLocation().getPitch() * 256 / 360), true));
                    Bukkit.getScheduler().runTaskAsynchronously(JungleNokaut.plugin, () ->{
                        connection.sendPacket(new PacketPlayOutEntityTeleport(entityPlayer));
                    });
                    data.set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 1);
                    return;
                }
                if(data.get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) != 3){
                    return;
                }
                //connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(entityPlayer.getId(), (byte)(knocked.getLocation().getYaw() * 256 / 360), (byte)(knocked.getLocation().getPitch() * 256 / 360), true));
                entityPlayer.setLocation(knocked.getLocation().getX(), knocked.getLocation().getY()+0.9, knocked.getLocation().getZ(), (knocked.getLocation().getYaw() * 256 / 360), (knocked.getLocation().getPitch() * 256 / 360));
                entityPlayer.yaw = knocked.getLocation().getYaw() * 256 / 360;
                entityPlayer.pitch = knocked.getLocation().getPitch() * 256 / 360;
                entityPlayer.lastYaw = knocked.getLocation().getYaw() * 256 / 360;
                entityPlayer.lastPitch = knocked.getLocation().getPitch() * 256 / 360;
                connection.sendPacket(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte)(knocked.getLocation().getYaw() * 256 / 360)));
                connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(entityPlayer.getId(), (byte)(knocked.getLocation().getYaw() * 256 / 360), (byte)(knocked.getLocation().getPitch() * 256 / 360), true));
                Bukkit.getScheduler().runTaskAsynchronously(JungleNokaut.plugin, () ->{
                    connection.sendPacket(new PacketPlayOutEntityTeleport(entityPlayer));
                });
                //connection.sendPacket(new PacketPlayOutEntityHeadRotation(entityPlayer, (byte)(knocked.getLocation().getYaw() * 256 / 360)));

            }
        }.runTaskTimerAsynchronously(JungleNokaut.getMain(), 3, 3);
        if(knocked == reciver){
            Location loc = knocked.getLocation();
            new BukkitRunnable()
            {
                public void run()
                {
                    knocked.teleport(loc);
                }
            }.runTaskLater(JungleNokaut.getMain(), 5);
        }

        new BukkitRunnable()
        {
            public void run()
            {
                if(data.get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) == 0){
                    data.set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 0);
                    this.cancel();
                    connection.sendPacket(new PacketPlayOutEntityDestroy(entityPlayer.getId()));
                    //connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer));
                    return;
                }
            }
        }.runTaskTimerAsynchronously(JungleNokaut.getMain(), 20, 20);
        System.out.println(knocked.getName() + " " + reciver.getName());
//        new BukkitRunnable()
//        {
//            public void run()
//            {
//                Location location = p.getLocation();
//                entityPlayer.setLocation(p.getLocation().getX(), p.getLocation().getY()+1.9, p.getLocation().getZ(), location.getYaw(), 0);
//                //entityPlayer.setPosition(p.getLocation().getX(), p.getLocation().getY()-0.1+2, p.getLocation().getZ());
//                connection.sendPacket(new PacketPlayOutEntityTeleport(entityPlayer));
//            }
//        }.runTaskTimerAsynchronously(this, 3, 3);

    }
    public static void lookNPCPacket(EntityPlayer npc, PlayerConnection connection, Location loc, World world, float yaw, float pitch) {
        connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte)(yaw * 256 / 360)));
        connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(npc.getId(), (byte)(yaw * 256 / 360), (byte)(pitch * 256 / 360), true));
        npc.setLocation(loc.getX(), loc.getY()+0.9, loc.getZ(), (yaw * 256 / 360), (pitch * 256 / 360));
        connection.sendPacket(new PacketPlayOutEntityTeleport(npc));
        npc.yaw = yaw * 256 / 360;
        npc.pitch = pitch * 256 / 360;
        connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(npc.getId(), (byte)(yaw * 256 / 360), (byte)(pitch * 256 / 360), true));
        //npc.teleportTo(((CraftWorld) world).getHandle(), new BlockPosition(loc.getX(), loc.getY(), loc.getZ()));
    }
}