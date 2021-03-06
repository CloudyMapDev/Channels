package com.github.rmsy.channels;

import com.github.rmsy.channels.impl.SimpleChannel;
import com.github.rmsy.channels.impl.SimplePlayerManager;
import com.github.rmsy.channels.listener.ChatListener;
import com.github.rmsy.channels.listener.PlayerListener;
import com.google.common.base.Preconditions;
import com.sk89q.bukkit.util.BukkitCommandsManager;
import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.event.HandlerList;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

public class ChannelsPlugin extends JavaPlugin {
    public static final String GLOBAL_CHANNEL_PARENT_NODE = "channels.global";
    public static final String GLOBAL_CHANNEL_SEND_NODE = ChannelsPlugin.GLOBAL_CHANNEL_PARENT_NODE + ".send";
    public static final String GLOBAL_CHANNEL_RECEIVE_NODE = ChannelsPlugin.GLOBAL_CHANNEL_PARENT_NODE + ".receive";
    /** The plugin instance. */
    private static ChannelsPlugin plugin;
    /** The global channel. */
    private Channel globalChannel;
    /** The default channel. */
    private Channel defaultChannel;
    /** The player manager. */
    private PlayerManager playerManager;

    /**
     * Gets the plugin instance
     *
     * @return The plugin instance.
     */
    public static ChannelsPlugin get() {
        return ChannelsPlugin.plugin;
    }

    /**
     * Gets the universal player manager.
     *
     * @return The universal player manager.
     */
    public PlayerManager getPlayerManager() {
        return this.playerManager;
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        this.playerManager = null;
        this.defaultChannel = null;
        this.globalChannel = null;
    }

    @Override
    public void onEnable() {
        ChannelsPlugin.plugin = this;

        Configuration config = this.getConfig();
        config.options().copyDefaults(true);
        this.saveConfig();

        this.globalChannel = new SimpleChannel(
                config.getString(
                        "global-chat.format",
                        ChatColor.WHITE + "<{1}" + ChatColor.RESET + ChatColor.WHITE + ">: {3}"
                ),
                new Permission(ChannelsPlugin.GLOBAL_CHANNEL_PARENT_NODE, PermissionDefault.TRUE)
        );
        this.defaultChannel = this.globalChannel;
        this.playerManager = new SimplePlayerManager();
        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
    }

    /**
     * Gets the global channel.
     *
     * @return The global channel.
     */
    public Channel getGlobalChannel() {
        return this.globalChannel;
    }

    /**
     * Sets the global channel.
     *
     * @param channel The new channel.
     */
    public void setGlobalChannel(Channel channel) {
        this.globalChannel = Preconditions.checkNotNull(channel, "Channel");
    }

    /**
     * Gets the channel that newly-connected players will be added to.
     *
     * @return The channel.
     */
    public Channel getDefaultChannel() {
        return this.defaultChannel;
    }

    /**
     * Sets the channel that newly-connected players will be added to.
     *
     * @param channel The channel.
     */
    public void setDefaultChannel(Channel channel) {
        this.defaultChannel = Preconditions.checkNotNull(channel, "Channel");
    }
}
