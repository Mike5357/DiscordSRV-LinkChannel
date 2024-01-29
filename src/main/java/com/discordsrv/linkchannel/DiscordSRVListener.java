package com.discordsrv.linkchannel;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.events.*;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.kyori.adventure.text.Component;
import github.scarsz.discordsrv.util.DiscordUtil;

import org.bukkit.Bukkit;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordSRVListener {

    private final Plugin plugin;

    public DiscordSRVListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @Subscribe
    public void discordReadyEvent(DiscordReadyEvent event) {
        DiscordUtil.getJda().addEventListener(new JDAListener(plugin));

        plugin.getLogger().info("Chatting on Discord with " + DiscordUtil.getJda().getUsers().size() + " users!");
    }

    @Subscribe
    public void accountLinked(AccountLinkedEvent event) {
        User user = DiscordUtil.getJda().getUserById(event.getUser().getId());
        user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Successfully linked with " + event.getPlayer().getName() + "!").queue());
        Bukkit.broadcastMessage(DiscordSRV.config().getString("LinkingMessageLinkedInGame").replace("%player%",event.getPlayer().getName()));

        TextChannel textChannel = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("links");

        // null if the channel isn't specified in the config.yml
        if (textChannel != null) {
            textChannel.sendMessage(DiscordSRV.config().getString("LinkingMessageLinkedInDiscord").replace("%player%",event.getPlayer().getName())).queue();
        } else {
            plugin.getLogger().warning("Channel called \"links\" could not be found in the DiscordSRV configuration");
        }
    }

    @Subscribe
    public void accountUnlinked(AccountUnlinkedEvent event) {
        User user = DiscordUtil.getJda().getUserById(event.getDiscordId());

        // will be null if the bot isn't in a Discord server with the user (eg. they left the main Discord server)
        if (user != null) {
            // opens/retrieves the private channel for the user & sends a message to it (if retrieving the private channel was successful)
            user.openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage("Your account has been unlinked from " + event.getPlayer().getName() + ".").queue());
            Bukkit.broadcastMessage(DiscordSRV.config().getString("LinkingMessageUnlinkedInGame").replace("%player%",event.getPlayer().getName()));

            //remove linked role when unlinked
            Role linkedRole = DiscordUtil.getRole(DiscordSRV.config().getString("MinecraftDiscordAccountLinkedRoleNameToAddUserTo"));
            if (linkedRole != null) {
                DiscordUtil.removeRolesFromMember(DiscordUtil.getMemberById(user.getId()),linkedRole);
            } else {
                plugin.getLogger().warning("Linked role not found in config; cannot remove role on unlink.");
            }
        }

        // sending a message to a channel called "unlinks" (defined in the config.yml using the Channels option) when a user unlinks
        TextChannel textChannel = DiscordSRV.getPlugin().getDestinationTextChannelForGameChannelName("unlinks");

        // null if the channel isn't specified in the config.yml
        if (textChannel != null) {
            textChannel.sendMessage(DiscordSRV.config().getString("LinkingMessageUnlinkedInDiscord").replace("%player%",event.getPlayer().getName())).queue();
        } else {
            plugin.getLogger().warning("Channel called \"unlinks\" could not be found in the DiscordSRV configuration");
        }
    }

    @Subscribe
    public void discordMessageProcessed(DiscordGuildMessagePostProcessEvent event) {
        Component message = event.getMinecraftMessage();
        event.setMinecraftMessage(parseTimestamps(message));
    }

    public Component parseTimestamps(Component message) {
        Pattern pattern = Pattern.compile("<t:\\d*:R>");
        Matcher matcher = pattern.matcher(message.toString());
        while(matcher.find()) {
            System.out.println("Matched timestamp regex");
            String timestamp = matcher.group().substring(3,13); // trim the timestamp to just the numbers

            Date timestampDate = new Date(Long.parseLong(timestamp) * 1000);
            String timeUntil = String.valueOf(Duration.between(Instant.now(), timestampDate.toInstant()).toMinutes());

            System.out.println("Parsed minutes: " + timeUntil);
            if (Integer.parseInt(timeUntil) < 0) {
                System.out.println("<0");
                message = message.replaceText(component -> {
                    component.match(pattern);
                    component.replacement("[" + timeUntil.substring(1) + " minutes ago]");
                    component.once();
                });
            } else if (Integer.parseInt(timeUntil) == 0) {
                System.out.println("==0");
                message = message.replaceText(component -> {
                    component.match(pattern);
                    component.replacement("[now]");
                    component.once();
                });
            } else if (Integer.parseInt(timeUntil) == 1) {
                System.out.println("==1");
                message = message.replaceText(component -> {
                    component.match(pattern);
                    component.replacement("[in 1 minute]");
                    component.once();
                });
            } else {
                System.out.println(">0");
                message = message.replaceText(component -> {
                    component.match(pattern);
                    component.replacement("[in " + timeUntil + " minutes]");
                    component.once();
                });
            }
        }
        return message;
    }

    @Subscribe
    public void gameMessageProcessed(GameChatMessagePreProcessEvent event) {
        //event.setCancelled(true);
        //System.out.println("Game chat message received: " + event.getMessageComponent());
        event.setMessageComponent(parseTimestamps(event.getMessageComponent()));
        //event.setCancelled(false);
    }


}
