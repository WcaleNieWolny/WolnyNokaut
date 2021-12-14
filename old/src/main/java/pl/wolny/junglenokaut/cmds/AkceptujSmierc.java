package pl.wolny.junglenokaut.cmds;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;
import pl.wolny.junglenokaut.JungleNokaut;

public class AkceptujSmierc implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            System.out.println("Nice try :)");
            return true;
        }

        Player executor = (Player) sender;
        PersistentDataContainer data = executor.getPersistentDataContainer();

        if (data.get(new NamespacedKey(JungleNokaut.getMain(), "NokStatus"), PersistentDataType.INTEGER) == 0) {
            executor.sendMessage(ChatColor.translateAlternateColorCodes('&', JungleNokaut.getConfigData().getString("AcceptDeathNo")));
            return true;
        }

        executor.setHealth(0);
        executor.sendMessage(ChatColor.translateAlternateColorCodes('&', JungleNokaut.getConfigData().getString("AcceptDeathYes")));

        if (executor.getVehicle() != null) {
            if (executor.getVehicle() instanceof Player) {
                ((Player) executor.getVehicle()).removePotionEffect(PotionEffectType.SLOW);
            }
        }

        return true;
    }
}
