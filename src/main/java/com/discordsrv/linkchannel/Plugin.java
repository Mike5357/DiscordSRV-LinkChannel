package com.discordsrv.linkchannel;

import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin implements Listener {

    private DiscordSRVListener discordsrvListener = new DiscordSRVListener(this);

    @Override
    public void onEnable() {
        DiscordSRV.api.subscribe(discordsrvListener);
        getLogger().info("DiscordSRV-LinkChannel extension has been enabled!");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        DiscordSRV.api.unsubscribe(discordsrvListener);
    }

}
