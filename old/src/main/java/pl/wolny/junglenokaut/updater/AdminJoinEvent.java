package pl.wolny.junglenokaut.updater;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pl.wolny.junglenokaut.JungleNokaut;

public class AdminJoinEvent implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void event(PlayerJoinEvent event) {
        if (!(event.getPlayer().hasPermission("tobiasznokaut.updateinfo"))) {
            return;
        }
        String con = GetLastestTag.OpenCon();
        if (con == null) {
            return;
        }
        if (con.equals(JungleNokaut.getMain().getDescription().getVersion())) {
            return;
        }
        Player player = event.getPlayer();
        player.sendMessage(fixChat("&c&lNOKAUT NA SERWERZE JEST NIE AKUTALNY!"));
        TextComponent message = new TextComponent("Pobierz go TUTAJ");
        message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/WcaleNieWolny/TobiaszNokaut/releases/"));
        message.setBold(true);
        message.setColor(ChatColor.RED);
        player.spigot().sendMessage(message);
    }

    private String fixChat(String string) {
        return string.replace("&", "§");
    }
}
