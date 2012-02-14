package com.dumptruckman.spamhammer.listeners;

import com.dumptruckman.spamhammer.config.ConfigPath;
import com.dumptruckman.spamhammer.SpamHammer;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import static com.dumptruckman.spamhammer.config.ConfigPath.*;

/**
 * @author dumptruckman
 */
public class SpamHammerPlayerListener extends PlayerListener {

    private SpamHammer plugin;

    public SpamHammerPlayerListener(SpamHammer plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.getPlayer().isOp() || event.getPlayer().hasPermission("spamhammer.ignore"))
            return;
        if (plugin.isMuted(event.getPlayer().getName())) {
            event.getPlayer().sendMessage(plugin.config.getString(MUTED_MESSAGE.toString()));
            event.setCancelled(true);
            return;
        }
        if (plugin.addChatMessage(event.getPlayer().getName(), event.getMessage())
                && plugin.preventMessages) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(plugin.config.getString(RATE_LIMIT_MESSAGE.toString()));
        }
    }

    public void onPlayerLogin(PlayerLoginEvent event) {
        if (plugin.isBanned(event.getPlayer().getName())) {
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                    plugin.config.getString(BAN_MESSAGE.toString()));
        }
    }

    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.getPlayer().isOp() || event.getPlayer().hasPermission("spamhammer.ignore"))
            return;
        if (plugin.isMuted(event.getPlayer().getName())) {
            event.getPlayer().sendMessage(plugin.config.getString(MUTED_MESSAGE.toString()));
            event.setCancelled(true);
            return;
        }
        if (!plugin.spamCommands.contains(event.getMessage().split("\\s")[0])) return;
        if (plugin.addChatMessage(event.getPlayer().getName(), event.getMessage())
                && Boolean.parseBoolean(plugin.config.getString(PREVENT_MESSAGES.toString()))) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(plugin.config.getString(RATE_LIMIT_MESSAGE.toString()));
        }
    }
}
