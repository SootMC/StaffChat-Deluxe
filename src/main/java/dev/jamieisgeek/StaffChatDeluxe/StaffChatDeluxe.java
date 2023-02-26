package dev.jamieisgeek.StaffChatDeluxe;

import dev.jamieisgeek.StaffChatDeluxe.Events.ChatEvent;
import dev.jamieisgeek.StaffChatDeluxe.Events.JoinEvent;
import dev.jamieisgeek.StaffChatDeluxe.Events.ServerSwitchEvent;
import dev.jamieisgeek.StaffChatDeluxe.Models.Channel;
import dev.jamieisgeek.StaffChatDeluxe.Models.Database;
import dev.jamieisgeek.StaffChatDeluxe.Models.DiscordBot;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public final class StaffChatDeluxe extends Plugin {
    private Configuration configuration;
    private Configuration messages;
    private Manager manager;

    @Override
    public void onEnable() {
        try {
            this.setupConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        manager = new Manager(this, messages);
        this.setupChannels();

        if(configuration.getBoolean("discordEnabled"))
            try {
                new DiscordBot(configuration.getString("botToken"), manager.getChannels());
            } catch (LoginException | InterruptedException e) {
                throw new RuntimeException(e);
            }

        if(configuration.getBoolean("vanishChecking"))
            try {
                this.setupDatabaseConnection();
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        getProxy().getPluginManager().registerListener(this, new ChatEvent());
        getProxy().getPluginManager().registerListener(this, new JoinEvent());
        getProxy().getPluginManager().registerListener(this, new ServerSwitchEvent());

        getProxy().getPluginManager().registerCommand(this, new StaffListCommand());
        getProxy().getPluginManager().registerCommand(this, new ReloadCommand());
        getLogger().info("StaffChat Deluxe has enabled");
    }

    @Override
    public void onDisable() {
        if(configuration.getBoolean("discordEnabled"))
            DiscordBot.getBOT().shutdown();

        if(configuration.getBoolean("vanishChecking"))
            try {
                Database.getDatabase().closeConnection();
            } catch (SQLException ignored) {
            }
        getLogger().info("StaffChat Deluxe has disabled");
    }

    private void setupConfig() throws IOException {
        if (!getDataFolder().exists()) {
            getLogger().info("Created config folder: " + getDataFolder().mkdir());
        }

        File configFile = new File(getDataFolder(), "config.yml");
        File messagesFile = new File(getDataFolder(), "messages.yml");

        // Copy default config if it doesn't exist
        if (!configFile.exists()) {
            FileOutputStream outputStream = new FileOutputStream(configFile); // Throws IOException
            InputStream in = getResourceAsStream("config.yml"); // This file must exist in the jar resources folder
            in.transferTo(outputStream); // Throws IOException
        }

        if(!messagesFile.exists()) {
            FileOutputStream outputStream = new FileOutputStream(messagesFile); // Throws IOException
            InputStream in = getResourceAsStream("messages.yml"); // This file must exist in the jar resources folder
            in.transferTo(outputStream); // Throws IOException
        }

        if (!getDataFolder().exists()) {
            getLogger().info("Created config folder: " + getDataFolder().mkdir());
        }

        configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        messages = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "messages.yml"));
    }

    private void setupChannels() {
        Configuration channels = configuration.getSection("channels");
        for (String channelName : channels.getKeys()) {
            Configuration channel = channels.getSection(channelName);
            String displayName = channel.getString("displayName");
            String permission = channel.getString("permission");
            String chatColor = channel.getString("chatColor");
            String chatPrefix = channel.getString("chatPrefix");
            String command = channel.getString("command");
            String[] aliases = channel.getStringList("aliases").toArray(new String[0]);
            String discordChannelID = channel.getString("discordChannel");
            String format = channel.getString("format");
            boolean moveMessages = channel.getBoolean("moveMessages");
            manager.addChannel(new Channel(channelName, displayName, permission, chatColor, chatPrefix, command, aliases, discordChannelID, format, moveMessages));

            getLogger().info("Registered channel: " + channelName);
        }

        for(Channel channel : manager.getChannels()) {
            getProxy().getPluginManager().registerCommand(this, new ChatCommand(channel.command(), channel, manager));
        }
    }

    public void reload() throws SQLException, LoginException, InterruptedException {
        try {
            this.setupConfig();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        manager = new Manager(this, messages);

        Manager.getManager().getChannels().clear();
        this.setupChannels();

        DiscordBot.getBOT().shutdownNow();

        if(configuration.getBoolean("discordEnabled"))
            new DiscordBot(configuration.getString("botToken"), manager.getChannels());

        Database.getDatabase().closeConnection();

        if(configuration.getBoolean("vanishChecking"))
            try {
                this.setupDatabaseConnection();
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
    }

    private void setupDatabaseConnection() throws SQLException, ClassNotFoundException {

        Class.forName("org.mariadb.jdbc.Driver");
        Configuration databaseConfig = configuration.getSection("sql");
        String host = databaseConfig.getString("host");
        String username = databaseConfig.getString("username");
        String password = databaseConfig.getString("password");
        String database = databaseConfig.getString("database");
        String port = databaseConfig.getString("port");

        new Database(host, database, username, password, port);
    }
}
