package pl.wolny.junglenokaut;

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
import pl.wolny.junglenokaut.utilities.Metrics;

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
            Bukkit.getLogger().info("Nie wykryto spigot'a na serwerze!");
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
        Metrics metrics = new Metrics(this, 10544);
        //Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new JoinEvent(), this);
        Bukkit.getPluginManager().registerEvents(new DeathEvent(), this);
        Bukkit.getPluginManager().registerEvents(new QuitEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJumpEvent(), this);
        Bukkit.getPluginManager().registerEvents(new PathFindingEvent(), this);
        Bukkit.getPluginManager().registerEvents(new DamageEvent(), this);
        Bukkit.getPluginManager().registerEvents(new SneakEvent(), this);
        Bukkit.getPluginManager().registerEvents(new BlockEvent(), this);
        Bukkit.getPluginManager().registerEvents(new DropListener(), this);
        Bukkit.getPluginManager().registerEvents(new AdminJoinEvent(), this);
        getCommand("zginodrazu").setExecutor(new AkceptujSmierc());
        getCommand("podniesgracza").setExecutor(new PodniesGracza());
        getCommand("rzucgracza").setExecutor(new RzucGracza());
        //Bukkit.getPluginManager().registerEvents(new DismountEvent(), this);
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
}
