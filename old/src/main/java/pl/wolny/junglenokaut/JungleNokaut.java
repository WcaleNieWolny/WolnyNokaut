package pl.wolny.junglenokaut;

import me.rerere.matrix.api.MatrixAPI;
import me.rerere.matrix.api.MatrixAPIProvider;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
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
import pl.wolny.junglenokaut.utilities.ConfigFile;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class JungleNokaut extends JavaPlugin implements Listener {
    public static YamlConfiguration configData;
    public static Plugin plugin;

    public static Plugin getMain() {
        return plugin;
    }

    public static YamlConfiguration getConfigData() {
        return configData;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        if (!(isPaper())) {
            Bukkit.getLogger().info("Nie wykryto papera'a na serwerze!");
            Bukkit.getLogger().info("Proszę go zainstalować przed użyciem pluginu");
            Bukkit.getLogger().info("Pobierzesz go tutaj: https://papermc.io/downloads");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        plugin = this;
        checkConfig();
        registerEvents();
        registerCommands();

        System.out.println(GetLastestTag.OpenCon());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        List<Player> list = new ArrayList<>(Bukkit.getOnlinePlayers());
        list.removeIf(ent -> ent.getPersistentDataContainer().get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) == 0);
        for (Player p : list) {
            p.setGameMode(GameMode.SURVIVAL);
            p.setWalkSpeed(0.2f);
            p.setHealth(0);
            p.setInvisible(false);
            p.removePotionEffect(PotionEffectType.BLINDNESS);
            p.getPersistentDataContainer().set(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER, 0);
        }
    }

    private boolean isPaper() {
        boolean isPapermc = false;
        try {
            isPapermc = Class.forName("com.destroystokyo.paper.VersionHistoryManager$VersionData") != null;
        } catch (ClassNotFoundException e) {
        }
        return isPapermc;
        //Source: https://papermc.io/forums/t/checking-for-server-type-paper-spigot-or-bukkit/981
    }

    private void registerEvents() {
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
        Bukkit.getPluginManager().registerEvents(new PlayerViolationListener(), this);
    }

    private void registerCommands() {
        getCommand("zginodrazu").setExecutor(new AkceptujSmierc());
        getCommand("podniesgracza").setExecutor(new PodniesGracza());
        getCommand("rzucgracza").setExecutor(new RzucGracza());
    }

    public void checkConfig() {
        Logger logger = Bukkit.getLogger();
        logger.info(">>> " + this.getDescription().getName() + " <<<");
        logger.info("Initializing the configuration file");
        logger.info("Performing tests");
        ConfigFile config = new ConfigFile(this);
        if (!(config.getFile().exists())) {
            genConfig(config);
            logger.info("Test 1 failed!");
            return;
        }
        if (!(config.check(new String[]{"NocCooldown", "HealCooldown", "HealXP", "PickupModule", "DisableCMD", "NoPlayerToDrop", "KnockedLine1",
                "KnockedLine2", "ResuscitationForDeadLine1", "ResuscitationForDeadLine2", "ResuscitationForHeal",
                "AcceptDeathNo", "AcceptDeathYes", "CanNotDoThat", "CanNotPickupYourSelf", "PickupSuckess", "PickupForUser", "SuckessDrop"}))) {
            config.getFile().delete();
            genConfig(config);
            logger.info("Test 2 failed!");
            return;
        }
        logger.info("Each test was successful!");
        logger.info(">>> " + this.getDescription().getName() + " <<<");
        configData = config.getYamlConfig();
    }

    public void genConfig(ConfigFile config) {
        YamlConfiguration yamlConfiguration = config.getYamlConfig();
        yamlConfiguration.set("NocCooldown", 60);
        yamlConfiguration.set("HealCooldown", 10);
        yamlConfiguration.set("HealXP", 7);
        yamlConfiguration.set("PickupModule", true);
        yamlConfiguration.set("DisableCMD", "&cTa komenda jest wyłączona!");
        yamlConfiguration.set("NoPlayerToDrop", "&cBrak graczy których można upuścić!");
        yamlConfiguration.set("KnockedLine1", "&a&lJesteś powalony!");
        yamlConfiguration.set("KnockedLine2", "&cPozostało: %TIME-1%:%TIME-2%");
        yamlConfiguration.set("ResuscitationForDeadLine1", "&a&lReanimowanie!");
        yamlConfiguration.set("ResuscitationForDeadLine2", "&cPozostało: %TIME%");
        yamlConfiguration.set("ResuscitationForHeal", "&aReanimowanie");
        yamlConfiguration.set("AcceptDeathNo", "&cŻycie jest piekne, dlaczego chcesz popełnić samobójstwo?");
        yamlConfiguration.set("AcceptDeathYes", "&aHara-kiri popełnione. Miłego dnia.");
        yamlConfiguration.set("CanNotDoThat", "&cNie możesz tego zrobić!");
        yamlConfiguration.set("CanNotPickupYourSelf", "&cNie możesz podnieść samego siebie!");
        yamlConfiguration.set("PickupSuckess", "&aPodniosłeś %USER%.");
        yamlConfiguration.set("PickupForUser", "&aJesteś podniesiony.");
        yamlConfiguration.set("SuckessDrop", "&aUpuściłeś %USER%.");
        config.setYamlConfig(yamlConfiguration);
        config.save();
        configData = config.getYamlConfig();
    }
}
