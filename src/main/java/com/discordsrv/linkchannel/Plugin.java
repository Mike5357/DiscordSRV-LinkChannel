package com.discordsrv.linkchannel;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

public class Plugin extends JavaPlugin implements Listener {

    private DiscordSRVListener discordsrvListener = new DiscordSRVListener(this);

    @Override
    public void onEnable() {
        DiscordSRV.api.subscribe(discordsrvListener);
        getLogger().info("DiscordSRV-LinkChannel extension has been enabled! Nice job Mike :)");
        getServer().getPluginManager().registerEvents(this, this);
    }

    /*@EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        String discordId = DiscordSRV.getPlugin().getAccountLinkManager().getDiscordId(event.getPlayer().getUniqueId());
        if (discordId == null) {
            event.getPlayer().sendMessage(ChatColor.RED + "You are not linked");
            return;
        }

        User user = DiscordUtil.getJda().getUserById(discordId);
        if (user == null) {
            event.getPlayer().sendMessage(ChatColor.YELLOW + "Couldn't find the user you're linked to");
            return;
        }

        event.getPlayer().sendMessage(ChatColor.GREEN + "You're linked to " + user.getAsTag());
    }*/

    @Override
    public void onDisable() {
        DiscordSRV.api.unsubscribe(discordsrvListener);
    }

}
