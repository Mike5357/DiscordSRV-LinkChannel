package com.discordsrv.linkchannel;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.events.DiscordGuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.GuildUnavailableEvent;
import github.scarsz.discordsrv.dependencies.jda.api.events.message.guild.GuildMessageReceivedEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class JDAListener extends ListenerAdapter {

    private final Plugin plugin;

    public JDAListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override // we can use any of JDA's events through ListenerAdapter, just by overriding the methods
    public void onGuildUnavailable(@NotNull GuildUnavailableEvent event) {
        plugin.getLogger().severe("Oh no " + event.getGuild().getName() + " went unavailable :(");
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        //If the message was sent in the discord linking channel
        if (event.getChannel().getId().toString().equals(DiscordSRV.config().getString("LinkingDiscordChannel"))) {
            //don't process messages sent by ANY bot
            if (!event.getAuthor().isBot()) {
                DiscordSRV.api.callEvent(new DiscordGuildMessageReceivedEvent(event));

                String reply = DiscordSRV.getPlugin().getAccountLinkManager().process(event.getMessage().getContentRaw(), event.getAuthor().getId());
                if (reply != null) event.getChannel().sendMessage(reply).queue();
            }
            event.getChannel().deleteMessageById(event.getMessage().getId()).queueAfter(20, TimeUnit.SECONDS);
        }
    }
}
