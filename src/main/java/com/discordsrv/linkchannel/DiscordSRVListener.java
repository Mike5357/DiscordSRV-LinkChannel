package com.discordsrv.linkchannel;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.ListenerPriority;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.*;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.util.DiscordUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class DiscordSRVListener {

    private final Plugin plugin;

    public DiscordSRVListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void discordReadyEvent(DiscordReadyEvent event) {
        // Example of using JDA's events
        // We need to wait until DiscordSRV has initialized JDA, thus we're doing this inside DiscordReadyEvent
        DiscordUtil.getJda().addEventListener(new JDAListener(plugin));

        // ... we can also do anything other than listen for events with JDA now,
        plugin.getLogger().info("Chatting on Discord with " + DiscordUtil.getJda().getUsers().size() + " users!");
        // see https://ci.dv8tion.net/job/JDA/javadoc/ for JDA's javadoc
        // see https://github.com/DV8FromTheWorld/JDA/wiki for JDA's wiki
    }

    /*@Subscribe(priority = ListenerPriority.MONITOR)
    public void discordMessageReceived(DiscordGuildMessageReceivedEvent event) {
        // Example of logging a message sent in Discord

        plugin.getLogger().info("Received a chat message on Discord: " + event.getMessage());
    }

    @Subscribe(priority = ListenerPriority.MONITOR)
    public void aMessageWasSentInADiscordGuildByTheBot(DiscordGuildMessageSentEvent event) {
        // Example of logging a message sent in Minecraft (being sent to Discord)

        plugin.getLogger().info("A message was sent to Discord: " + event.getMessage());
    }*/

    @Subscribe
    public void accountLinked(AccountLinkedEvent event) {
        // Example of broadcasting a message when a new account link has been made
        //User user = DiscordUtil.getJda().getUserById(event.getUser().getId());
        //user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Your account has been linked!").queue());
        Bukkit.broadcastMessage(ChatColor.GRAY + "[" + net.md_5.bungee.api.ChatColor.of("#78E174") + "\u2714" + ChatColor.GRAY + "] " + net.md_5.bungee.api.ChatColor.of("#FF9B58") + event.getPlayer().getName() + ChatColor.GRAY + " has linked their" + net.md_5.bungee.api.ChatColor.of("#7289DA") + " Discord" + ChatColor.GRAY + " account!");

        // Example of sending a message to a channel called "unlinks" (defined in the config.yml using the Channels option) when a user unlinks
        TextChannel textChannel = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("links");

        // null if the channel isn't specified in the config.yml
        if (textChannel != null) {
            textChannel.sendMessage(event.getPlayer().getName() + " has linked their Discord account!").queue();
        } else {
            plugin.getLogger().warning("Channel called \"links\" could not be found in the DiscordSRV configuration");
        }
    }

    @Subscribe
    public void accountUnlinked(AccountUnlinkedEvent event) {
        // Example of DM:ing user on unlink
        User user = DiscordUtil.getJda().getUserById(event.getDiscordId());

        // will be null if the bot isn't in a Discord server with the user (eg. they left the main Discord server)
        if (user != null) {

            // opens/retrieves the private channel for the user & sends a message to it (if retrieving the private channel was successful)
            //user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Your account has been unlinked").queue());
            Bukkit.broadcastMessage(ChatColor.GRAY + "[" + net.md_5.bungee.api.ChatColor.of("#DB5860") + "\u2718" + ChatColor.GRAY + "] " + net.md_5.bungee.api.ChatColor.of("#FF9B58") + event.getPlayer().getName() + ChatColor.GRAY + " has unlinked their" + net.md_5.bungee.api.ChatColor.of("#7289DA") + " Discord" + ChatColor.GRAY + " account.");
        }

        // Example of sending a message to a channel called "unlinks" (defined in the config.yml using the Channels option) when a user unlinks
        TextChannel textChannel = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("unlinks");

        // null if the channel isn't specified in the config.yml
        if (textChannel != null) {
            textChannel.sendMessage(event.getPlayer().getName() + " has unlinked their Discord account.").queue();
        } else {
            plugin.getLogger().warning("Channel called \"unlinks\" could not be found in the DiscordSRV configuration");
        }
    }

    /*
    @Subscribe
    public void discordMessageProcessed(DiscordGuildMessagePostProcessEvent event) {
        // Example of modifying a Discord -> Minecraft message

        event.setProcessedMessage(event.getProcessedMessage().replace("cat", "dog")); // dogs are superior to cats, obviously
    }
    */

}
