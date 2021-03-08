package pl.wolny.junglenokaut;

import me.rerere.matrix.api.MatrixAPI;
import me.rerere.matrix.api.MatrixAPIProvider;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import pl.wolny.junglenokaut.cmds.AkceptujSmierc;
import pl.wolny.junglenokaut.cmds.PodniesGracza;
import pl.wolny.junglenokaut.cmds.RzucGracza;
import pl.wolny.junglenokaut.listeners.*;
import pl.wolny.junglenokaut.updater.GetLastestTag;
import pl.wolny.junglenokaut.updater.*;

import java.util.ArrayList;
import java.util.List;

public final class JungleNokaut extends JavaPlugin implements Listener {
    public static Plugin plugin;
    public static Plugin getMain(){
        return plugin;
    }
    @Override
    public void onEnable() {
        // Plugin startup logic
        if(!(isPaper())){
            Bukkit.getLogger().info("Nie wykryto papera'a na serwerze!");
            Bukkit.getLogger().info("Proszę go zainstalować przed użyciem pluginu");
            Bukkit.getLogger().info("Pobierzesz go tutaj: https://papermc.io/downloads");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        plugin = this;
        getConfig().addDefault("NocCooldown", 60);
        getConfig().addDefault("HealCooldown", 10);
        getConfig().addDefault("HealXP", 7);
        getConfig().addDefault("PickupModule", true);
        getConfig().addDefault("DisableCMD", "&cTa komenda jest wyłączona!");
        getConfig().addDefault("NoPlayerToDrop", "&cBrak graczy których można upuścić!");
        getConfig().addDefault("KnockedLine1", "&a&lJesteś powalony!");
        getConfig().addDefault("KnockedLine2", "&cPozostało: %TIME-1%:%TIME-2%");
        getConfig().addDefault("ResuscitationForDeadLine1", "&a&lReanimowanie!");
        getConfig().addDefault("ResuscitationForDeadLine2", "&cPozostało: %TIME%");
        getConfig().addDefault("ResuscitationForHeal", "&aReanimowanie");
        getConfig().addDefault("AcceptDeathNo", "&cŻycie jest piekne, dlaczego chcesz popełnić samobójstwo?");
        getConfig().addDefault("AcceptDeathYes", "&aHara-kiri popełnione. Miłego dnia.");
        getConfig().addDefault("CanNotDoThat", "&cNie możesz tego zrobić!");
        getConfig().addDefault("CanNotPickupYourSelf", "&cNie możesz podnieść samego siebie!");
        getConfig().addDefault("PickupSuckess", "&aPodniosłeś %USER%.");
        getConfig().addDefault("PickupForUser", "&aJesteś podniesiony.");
        getConfig().addDefault("SuckessDrop", "&aUpuściłeś %USER%.");
        getConfig().options().copyDefaults(true);
        saveConfig();

        registerEvents();
        registerCommands();

        System.out.println(GetLastestTag.OpenCon());
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

    private boolean isPaper(){
        boolean isPapermc = false;
        try {
            isPapermc = Class.forName("com.destroystokyo.paper.VersionHistoryManager$VersionData") != null;
        } catch (ClassNotFoundException e) {
        }
        return  isPapermc;
        //Source: https://papermc.io/forums/t/checking-for-server-type-paper-spigot-or-bukkit/981
    }

    private void registerEvents(){
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener2(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJumpListener(), this);
        Bukkit.getPluginManager().registerEvents(new PathFindingListener(), this);
        Bukkit.getPluginManager().registerEvents(new DamageListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerToggleSneakListener(), this);
        Bukkit.getPluginManager().registerEvents(new BlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new AdminJoinEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PacketLisener(), this);
        Bukkit.getPluginManager().registerEvents(new DismountListener(), this);
    }

    private void registerCommands(){
        getCommand("zginodrazu").setExecutor(new AkceptujSmierc());
        getCommand("podniesgracza").setExecutor(new PodniesGracza());
        getCommand("rzucgracza").setExecutor(new RzucGracza());
    }
}
