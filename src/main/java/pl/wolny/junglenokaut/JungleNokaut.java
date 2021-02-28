package pl.wolny.junglenokaut;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import pl.wolny.junglenokaut.listeners.*;
import pl.wolny.junglenokaut.cmds.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class JungleNokaut extends JavaPlugin implements Listener {
    public static Plugin plugin;
    public static Plugin getMain(){
        return plugin;
    }
    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        getConfig().addDefault("NocCooldown", 60);
        getConfig().addDefault("HealCooldown", 10);
        getConfig().addDefault("HealXP", 7);
        getConfig().options().copyDefaults(true);
        saveConfig();
        //Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new JoinEvent(), this);
        Bukkit.getPluginManager().registerEvents(new DeathEvent(), this);
        Bukkit.getPluginManager().registerEvents(new QuitEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJumpEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PathFindingEvent(), this);
        Bukkit.getPluginManager().registerEvents(new DamageEvent(), this);
        Bukkit.getPluginManager().registerEvents(new SneakEvent(), this);
        Bukkit.getPluginManager().registerEvents(new BlockEvent(), this);
        getCommand("zginodrazu").setExecutor(new AkceptujSmierc());
        getCommand("podniesgracza").setExecutor(new PodniesGracza());
        getCommand("rzucgracza").setExecutor(new RzucGracza());
        Bukkit.getPluginManager().registerEvents(new DismountEvent(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        List<Player> list = new ArrayList<>(Bukkit.getOnlinePlayers());
        list.removeIf(ent -> ent.getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) == 0);
        for (Player p: list) {
            p.setGameMode(GameMode.SURVIVAL);
            p.setWalkSpeed(0.2f);
            p.setHealth(0);
            p.setInvisible(false);
            p.removePotionEffect(PotionEffectType.BLINDNESS);
            p.getPersistentDataContainer().set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 0);
        }
    }
    @EventHandler
    public void drop(PlayerDropItemEvent event){
        event.getPlayer().sendMessage("nkt=test");
        if(event.getItemDrop().getItemStack().getType() == Material.BAMBOO){
            event.getPlayer().getPersistentDataContainer().set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 3);
            System.out.println(event.getPlayer().getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokPodnoszenie"), PersistentDataType.INTEGER));
            return;
        }
        event.getPlayer().getPersistentDataContainer().set(new NamespacedKey(JungleNokaut.getMain(), "NokPodnoszenie"), PersistentDataType.INTEGER, 0);
    }
    public void sendBedEnterAnimation(Player p, Location l){
        String UUID_PATTERN = "00000000-0000-%s-0000-000000000000";
        GameProfile gameProfile = new GameProfile(p.getUniqueId(), p.getName());
        EntityPlayer entityPlayer = new EntityPlayer(
                ((CraftServer) Bukkit.getServer()).getServer(),
                ((CraftWorld) p.getWorld()).getHandle(),
                gameProfile,
                new PlayerInteractManager(((CraftWorld) p.getWorld()).getHandle()));
        entityPlayer.setPosition(p.getLocation().getX(), p.getLocation().getY()-0.1, p.getLocation().getZ());
        entityPlayer.setPose(EntityPose.SWIMMING);
//        entityPlayer.getBukkitEntity().setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&l" + p.getName()));
//        entityPlayer.getBukkitEntity().setCustomName(ChatColor.translateAlternateColorCodes('&', "&c&l" + p.getName()));
        PlayerConnection connection = ((CraftPlayer)p.getPlayer()).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutNamedEntitySpawn(entityPlayer));
        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer));
//        Location bed = p.getLocation().add(1, 0, 0).subtract(0,0.5, 0);
//        entityPlayer.e(new BlockPosition(bed.getX(), bed.getY(), bed.getZ()));
        DataWatcher watcher = entityPlayer.getDataWatcher();
        //entityPlayer.entitySleep(new BlockPosition(bed.getX(), bed.getY(), bed.getZ()));

        connection.sendPacket(new PacketPlayOutEntityMetadata(entityPlayer.getId(), watcher, false));
        new BukkitRunnable()
        {
            public void run()
            {
                Location location = p.getLocation();
                entityPlayer.setLocation(p.getLocation().getX(), p.getLocation().getY()+1.9, p.getLocation().getZ(), location.getYaw(), 0);
                //entityPlayer.setPosition(p.getLocation().getX(), p.getLocation().getY()-0.1+2, p.getLocation().getZ());
                connection.sendPacket(new PacketPlayOutEntityTeleport(entityPlayer));
            }
        }.runTaskTimerAsynchronously(this, 3, 3);

    }
    private void tp(Player p){
        String UUID_PATTERN = "00000000-0000-%s-0000-000000000000";
        GameProfile gameProfile = new GameProfile(UUID.fromString(String.format(UUID_PATTERN, "01")), p.getName());
        EntityPlayer entityPlayer = new EntityPlayer(
                ((CraftServer) Bukkit.getServer()).getServer(),
                ((CraftWorld) p.getWorld()).getHandle(),
                gameProfile,
                new PlayerInteractManager(((CraftWorld) p.getWorld()).getHandle()));
        entityPlayer.getBukkitEntity().teleport(p);
        PlayerConnection connection = ((CraftPlayer)p.getPlayer()).getHandle().playerConnection;
        connection.sendPacket(new PacketPlayOutEntityTeleport(entityPlayer));
        p.sendMessage("1dada");
    }
    private byte[] bigIntToByteArray( final int i ) {
        BigInteger bigInt = BigInteger.valueOf(i);
        return bigInt.toByteArray();
    }
}
