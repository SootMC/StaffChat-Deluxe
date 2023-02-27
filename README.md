# StaffChat Deluxe
[![Maven Build](https://github.com/SootMC/StaffChat-Deluxe/actions/workflows/maven.yml/badge.svg)](https://github.com/SootMC/StaffChat-Deluxe/actions/workflows/maven.yml)

This plugin is a remake of the plugin UltraStaffChatPro, made for SootMC, this README basically just says how to make your channels and stuff.


## The Config
The config file is the main part of the plugin, its where you define all the things for your channels.

```yaml
discordEnabled:
botToken:
```
The discordEnabled option is where you can chose whether you would like to integrate your staff chats with discord. The botToken config option is where you need to put the token for the Discord bot that will link into your plugin.


### Setting up channels
```yaml
channels:
  staff:
    displayName: "&4&lStaff"
    permission: "staffchat.staff"
    chatColor: "&c"
    chatPrefix: "#"
    command: "sc"
    commandAliases: ["staffchat", "staffc"]
    moveMessages: true
    discordChannel: 328923486723845
```
