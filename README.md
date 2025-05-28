<p align="center">
    <a href="https://app.codacy.com/gh/Fulminazzo/userstalker-minecraft/"><img src="https://fulminazzo.it/badge/code/Fulminazzo/userstalker-minecraft?type=code" alt="Lines of Code" /></a>
    <a href="../../releases/latest"><img src="https://img.shields.io/github/v/release/Fulminazzo/userstalker-minecraft?display_name=tag&color=red" alt="Latest version" /></a>
    <a href="https://app.codacy.com/gh/Fulminazzo/userstalker-minecraft/"><img src="https://fulminazzo.it/badge/code/Fulminazzo/userstalker-minecraft?type=test" alt="Lines of Code" /></a>
</p>
<p align="center">
    <a href="https://github.com/fulminazzo/userstalker-minecraft/actions"><img src="https://fulminazzo.it/badge/coverage/Fulminazzo/userstalker-minecraft/tests.yml" alt="Tests coverage" /></a>
</p>
<p align="center">
    <img src="https://img.shields.io/badge/They%20are-spying%20on%20us-aa0000?style=for-the-badge" alt="" />
</p>

**UserStalker** is a **statistic plugin** created for **Bukkit 1.13+** (it may work on lower versions, but it has not
been tested).

It uses the [UserStalker REST API](https://github.com/fulminazzo/user-stalker) to keep track of every **player access**
in the server and display them in a **GUI format**.

It also provides a **skin cache** system that directly interfaces with the **Mojang API** for displaying
customized player heads.

| **Table of Contents**       |
|-----------------------------|
| [Commands](#commands)       |
| [Skin Cache](#skin-cache)   |
| [API Client](#api-client)   |
| [GUI Manager](#gui-manager) |

The plugin generates four files upon loading:

- [`config.yml`](./blob/master/src/main/resources/config.yml), to configure the plugin behaviour;
- `messages.yml`, to edit all the messages sent by the plugin;
- `skin_cache`, the [skin cache](#skin-cache) (if set to file in the `config.yml`);
- `guis.yml`, to customize the GUIs shown by the [GUI Manager](#gui-manager).

In the following section every module of the plugin will be described, along with the entries in the
`config.yml` required to configure it.

### Commands

The following are all the commands with usages and permissions of the plugin.
All the messages can be edited in the `messages.yml`.

| **Command usage**                 | **Permission**                | **Aliases**               | **Description**                                                                                                                                                                          |
|-----------------------------------|-------------------------------|---------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `/userstalker <subcommand>`       | _userstalker.command_         | `users`, `ustalker`, `us` | The main command of the plugin. Requires a subcommand to be specified.                                                                                                                   |
| `/userstalker reload`             | _userstalker.command.reload_  |                           | Reloads the plugin configuration files and managers. If an error occurs, it is shown in console and the plugin disabled.                                                                 |
| `/userstalker opengui <username>` | _userstalker.command.opengui_ | `open`, `gui`             | Opens the main GUI of the plugin. If a username is specified, a GUI displaying the accesses of that user will be used.                                                                   |
| `/userstalker help <subcommand>`  | _userstalker.command.help_    | `?`                       | Shows description and usage for all the available subcommands (if the issuer has permission). If an argument is specified, all the subcommands that contain that argument will be shown. |

### Skin Cache

### API Client

### GUI Manager